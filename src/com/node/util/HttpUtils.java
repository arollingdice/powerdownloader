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
