package com.kodcu.other;

/**
 * Created by usta on 23.01.2015.
 */
public class OSHelper {

    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {

        return (OS.indexOf("win") >= 0);

    }

    public static boolean isMac() {

        return (OS.indexOf("mac") >= 0);

    }

    public static boolean isUnix() {

        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );

    }

    public static boolean isSolaris() {

        return (OS.indexOf("sunos") >= 0);

    }
}
