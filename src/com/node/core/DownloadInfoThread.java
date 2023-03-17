package com.node.core;

import com.node.constant.Constant;

import java.util.ConcurrentModificationException;

/**
 * show downloading information
 */
public class DownloadInfoThread implements Runnable{

    // to download file size
    private long httpFileContentLength;

    // downloaded file size
    public double finishedSize;

    // downloaded size in previous cycle
    public double prevSize;

    // downloaded size in current cycle
    public volatile double downSize;

    public DownloadInfoThread(long httpFileContentLength){
        this.httpFileContentLength = httpFileContentLength;

    }

    @Override
    public void run(){
        // calculate file size
       String httpFileSize = String.format("%.2f", httpFileContentLength / Constant.MB);

        // calculate download speed in kb
       int speed = (int)((downSize - prevSize) / 1024d);
       prevSize = downSize;

       // remaining file size
       double remainingSize = httpFileContentLength - finishedSize - downSize;

       // calculate remaining time
       String remainTime = String.format("%.1f", remainingSize / 1024d / speed);

       if("Infinity".equalsIgnoreCase(remainTime)){
           remainTime = "INF";
       }

       // downloaded size
        String currentFileSize = String.format("%.2f", (downSize - finishedSize) / Constant.MB);

        String downInfo = String.format("Downloaded %smb/%smbi, Speed %skb/s, EST %ss",
                currentFileSize, speed, remainTime);

        System.out.print("\r");
        System.out.print(downInfo);

    }
}
