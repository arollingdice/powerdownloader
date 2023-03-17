package com.node.core;

import com.node.constant.Constant;
import com.node.util.HttpUtils;
import com.node.util.LogUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/*
    parts downloading task
 */
public class DownloaderTask implements Callable<Boolean> {

    // download url
    private final String url;

    // start position for downloading
    private final long startPos;

    // end position for downloading
    private final long endPos;

    // to display current downloading part
    private final int part;

    private CountDownLatch countDownLatch;

    public DownloaderTask(String url, long startPos, long endPos, int part, CountDownLatch countDownLatch) {
        this.url = url;
        this.startPos = startPos;
        this.endPos = endPos;
        this.part = part;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public Boolean call() throws Exception {

        // get file name
        String httpFileName = HttpUtils.getHttpFileName(url);

        // get part name
        httpFileName = httpFileName + ".temp" + part;

        // file path
        httpFileName = Constant.PATH + httpFileName;

        // get connection for parts downloading
        HttpURLConnection httpURLConnection = HttpUtils.getHttpURLConnection(url, startPos, endPos);

        try (
                InputStream input = httpURLConnection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(input);
                RandomAccessFile accessFile = new RandomAccessFile(httpFileName, "rw");
        ) {
            byte[] buffer = new byte[Constant.BYTE_SIZE];
            int len = -1;

            // read the file data in a loop
            while ((len = bis.read(buffer)) != -1) {
                // get sum of data per second, using Atomic class
                // this is for displaying download info and get the speed.
                DownloadInfoThread.downSize.add(len);

                // write the buffer
                accessFile.write(buffer, 0, len);
            }


        } catch (FileNotFoundException e) {
            LogUtils.error("File not exists!");
            return false;
        } catch (Exception e) {
            LogUtils.error("Errors, please contact admin");
            return false;
        } finally {
            httpURLConnection.disconnect();

            countDownLatch.countDown();
        }

        return true;

    }
}
