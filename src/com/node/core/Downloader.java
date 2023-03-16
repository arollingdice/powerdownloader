package com.node.core;

import com.node.constant.Constant;
import com.node.util.HttpUtils;
import com.node.util.LogUtils;

import java.io.*;
import java.net.HttpURLConnection;

/**
 *  downloader
 */
public class Downloader {

    public void download(String url){
        //get the file name
        String httpFileName = HttpUtils.getHttpFileName(url);
        // file for the path
        httpFileName = Constant.PATH + httpFileName;

        // get the url from internet
        HttpURLConnection httpURLConnection = null;
        try{
            httpURLConnection = HttpUtils.getHttpURLConnection(url);
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

        } catch (FileNotFoundException e){
            LogUtils.error("error: File not exist{}", url );
        } catch (Exception e){
            LogUtils.error("download failed");
        } finally{
            // close the http connection
            if(httpURLConnection != null){
                httpURLConnection.disconnect();
            }
        }
    }
}
