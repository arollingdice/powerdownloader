package com.node.core;

import com.node.constant.Constant;
import com.node.util.FileUtils;
import com.node.util.HttpUtils;
import com.node.util.LogUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.concurrent.*;

/**
 *  downloader
 */
public class Downloader {

    public ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    public ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(Constant.THREAD_NUM,
            Constant.THREAD_NUM, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(Constant.THREAD_NUM));
    public void download(String url){
        //get the file name
        String httpFileName = HttpUtils.getHttpFileName(url);
        // file for the path
        httpFileName = Constant.PATH + httpFileName;

        // get local file size
        long localFileLength = FileUtils.getFileContentLength(httpFileName);

        // get the url from internet
        HttpURLConnection httpURLConnection = null;
        DownloadInfoThread downloadInfoThread = null;
        try{
            httpURLConnection = HttpUtils.getHttpURLConnection(url);
            // get file size
            int contentLength = httpURLConnection.getContentLength();

            // check if file has been downloaded
            if (localFileLength >= contentLength) {
                LogUtils.info("{} file already downloaded",httpFileName);
                return;
            }

            // create a threading task for the file to be downloaded
            downloadInfoThread = new DownloadInfoThread(contentLength);
            // feed the task to the thread
            scheduledExecutorService.scheduleAtFixedRate(downloadInfoThread,1,1, TimeUnit.SECONDS);

            // split the task(file)
            ArrayList<Future> list = new ArrayList<>();
            split(url, list);

            list.forEach(future ->{
                try {
                    future.get();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });


        } catch (IOException e){
            e.printStackTrace();
        }

        try(
                InputStream input = httpURLConnection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(input);
                FileOutputStream fos = new FileOutputStream(httpFileName);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                ){
            int len = -1;
            while((len = bis.read()) != -1){
                bos.write(len);
            }

            // merge the temp files
            if(merge(httpFileName)){
                // if merged, remove temp files.
                clearTemp(httpFileName);
            }

        } catch (FileNotFoundException e){
            LogUtils.error("error: File not exist{}", url );
        } catch (Exception e){
            LogUtils.error("download failed");
        } finally{
            // close the http connection
            System.out.print("\r");
            System.out.print("Download Success!");
            if(httpURLConnection != null){
                httpURLConnection.disconnect();
            }

            // close the thread
            scheduledExecutorService.shutdownNow();

            // close the thread pool
            poolExecutor.shutdown();
        }
    }

    /**
     * split the files
     * @param url
     * @param futureList
     */
    public void split(String url, ArrayList<Future> futureList){

        try {
            // get file size
            long contentLength = HttpUtils.getHttpFileContentLength(url);

            // get file size after it get split(by the number of threads)
            long size = contentLength / Constant.THREAD_NUM;

            // get the number of blocks
            for(int i = 0; i < Constant.THREAD_NUM; i ++){
                // get starting point of the block
                long startPos = i * size;
                // get ending point of the block
                long endPos;

                if(i == Constant.THREAD_NUM - 1){
                    // downloading the final block
                    endPos = 0;
                } else{
                    endPos = startPos + size;
                }

                // if not in first block, starting point should increment by 1
                if(startPos != 0){
                    startPos++;
                }

                // get download task
                DownloaderTask downloaderTask = new DownloaderTask(url, startPos, endPos, i);

                // feed the task to thread pool
                Future<Boolean> future = poolExecutor.submit(downloaderTask);

                futureList.add(future);

            }

        } catch (IOException e) {


            e.printStackTrace();
        }
    }

    /**
     *  Merge file
      * @param fileName
     * @return
     */
    public boolean merge(String fileName){
        LogUtils.info("Merging files{}", fileName);
        byte[] buffer = new byte[Constant.BYTE_SIZE];
        int len = -1;

        try(RandomAccessFile accessFile = new RandomAccessFile(fileName, "rw")){
            for(int i = 0; i < Constant.THREAD_NUM; i ++){
                try(BufferedInputStream bis =
                            new BufferedInputStream(new FileInputStream(fileName + ".temp" + i))){
                  while( (len = bis.read(buffer)) != -1){
                      accessFile.write(buffer, 0 , len);
                  }

                }
            }
            LogUtils.info("File {} Merge Completed." , fileName);

        }catch (Exception e){
            e.printStackTrace();
            return false;

        }
        return true;
    }

    /**
     *  Delete temporary files
      * @param fileName
     * @return
     */
    public boolean clearTemp(String fileName){
        for(int i = 0; i < Constant.THREAD_NUM; i ++){
            File file = new File(fileName + ".temp" + i);
            file.delete();
        }

        return true;
    }

}
