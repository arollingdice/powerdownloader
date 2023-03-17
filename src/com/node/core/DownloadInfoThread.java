package com.node.core;

import com.node.constant.Constant;

import java.util.concurrent.atomic.LongAdder;

/**
 * show downloading information
 */
public class DownloadInfoThread implements Runnable{

    // to download file size
    private long httpFileContentLength;

    // already downloaded file size (parts of the file)
    public static LongAdder finishedSize = new LongAdder();

    // downloaded size in previous cycle
    public double prevSize;

    // downloaded size in current cycle
    public static volatile LongAdder downSize = new LongAdder();

    public DownloadInfoThread(long httpFileContentLength){
        this.httpFileContentLength = httpFileContentLength;

    }

    @Override
    public void run(){
        // calculate file size
       String httpFileSize = String.format("%.2f", httpFileContentLength / Constant.MB);

        // calculate download speed in kb
       int speed = (int)((downSize.doubleValue() - prevSize) / 1024d);
       prevSize = downSize.doubleValue();

       // remaining file size
       double remainingSize = httpFileContentLength - finishedSize.doubleValue() - downSize.doubleValue();

       // calculate remaining time
       String remainTime = String.format("%.1f", remainingSize / 1024d / speed);

       if("Infinity".equalsIgnoreCase(remainTime)){
           remainTime = "INF";
       }

       // downloaded size
        String currentFileSize = String.format("%.2f", (downSize.doubleValue() - finishedSize.doubleValue()) / Constant.MB);

        String downInfo = String.format("Downloaded %smb/%smb, Speed %skb/s, EST %ss",
                currentFileSize, httpFileSize, speed, remainTime);

        System.out.print("\r");
        System.out.print(downInfo);

    }
}
