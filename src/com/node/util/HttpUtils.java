package com.node.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/*
    utils for http
 */
public class HttpUtils {

    /**
     * get the size of downloading file
     * @param url
     * @return
     * @throws IOException
     */
    public static long getHttpFileContentLength(String url) throws IOException{
        int contentLength;
        HttpURLConnection httpURLConnection = null;

        try {
           httpURLConnection = getHttpURLConnection(url);
           contentLength = httpURLConnection.getContentLength();
        } finally {
            if(httpURLConnection != null){
                httpURLConnection.disconnect();
            }

        }
        return contentLength;
    }

    /**
     * blocked dividing download
     * @param url
     * @param startPos
     * @param endPos
     * @return
     */
    public static HttpURLConnection getHttpURLConnection(String url, long startPos, long endPos) throws IOException {
        HttpURLConnection httpURLConnection = getHttpURLConnection(url);
        LogUtils.info("Downloading range: {}-{}", startPos, endPos);

        if(endPos != 0){
            // bytes = 100 - 200
            httpURLConnection.setRequestProperty("RANGE", "bytes=" + startPos + "-" + endPos);
        }else{
            httpURLConnection.setRequestProperty("RANGE", "bytes=" + startPos + "-");
        }

        return httpURLConnection;
    }

    /**
     * Get the object for HttpURLConnection
     * @param url file's url
     * @return
     */
    public static HttpURLConnection getHttpURLConnection(String url) throws IOException {
        URL httpURL = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection)httpURL.openConnection();
        // send user agent info to the server
        httpURLConnection.setRequestProperty("USer-Agent", "Mozilla/5.0 (X11; Linux x86_64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
        return httpURLConnection;
    }

    /**
     * Get the downloaded file name
     * @param url
     * @return
     */
    public static String getHttpFileName(String url){
       int index = url.lastIndexOf("/");
       return url.substring(index + 1);

    }

}
