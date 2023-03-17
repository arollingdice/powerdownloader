package com.node.util;

import java.io.File;

public class FileUtils {

    /**
     *  get local file size
     */
    public static long getFileContentLength(String path) {
        File file = new File(path);

        return file.exists() && file.isFile() ? file.length() : 0;
    }
}