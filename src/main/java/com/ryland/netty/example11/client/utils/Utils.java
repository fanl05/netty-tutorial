package com.ryland.netty.example11.client.utils;

/**
 * @author Ryland
 */
public class Utils {

    private Utils() {
    }

    public static void consolePrintMenu() {
        consolePrintSeparator();
        System.out.println("send [username] [content]");
        System.out.println("gsend [group name] [content]");
        System.out.println("gcreate [group name] [m1,m2,m3...]");
        System.out.println("gmembers [group name]");
        System.out.println("gjoin [group name]");
        System.out.println("gquit [group name]");
        System.out.println("quit");
        consolePrintSeparator();
    }

    public static void consolePrintSeparator() {
        System.out.println("==================================");
    }

}
