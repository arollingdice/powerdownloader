package com.node.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LogUtils {

    public static void info(String msg, Object... args){
        print(msg, "-info-", args);
    }

    public static void error(String msg, Object... args){
        print(msg, "-error-", args);
    }

    // Object is undefined parameters.
    private static void print(String msg, String level, Object... args){
        if(args != null && args.length > 0){
            String.format(msg.replace("{}", "%s"), args);
        }

        String name = Thread.currentThread().getName();
        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm::ss")) + " "
                + name + level + msg);

    }
}
