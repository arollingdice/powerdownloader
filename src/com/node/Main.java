package com.node;

import com.node.core.Downloader;
import com.node.util.LogUtils;

import java.util.Scanner;

/*
    Main class
 */
public class Main {
    public static void main(String[] args){
       // Get the download link
        String url = null;
        if(args == null  || args.length == 0){
            for(; ; ){
                LogUtils.info("Input download url:");
                System.out.println();
                Scanner scanner = new Scanner(System.in);
                url = scanner.next();
                if(url != null){
                    break;
                }
            }
        }else {
            url = args[0];
        }

        Downloader downloader = new Downloader();
        downloader.download(url);
    }
}
