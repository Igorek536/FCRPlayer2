package org.freecore.fcrplayer2.utils;

public class OSUtils {

    public OS getOS() {
        String osName = System.getProperty("os.name");
        String arch = System.getProperty("sun.arch.data.model");
        boolean is64 = false;
        if (arch.contains("64")) is64 = true;
        if (osName.contains("nux")) {
            if (is64) return OS.LINUX64;
            else return OS.LINUX;
        } else if (osName.contains("Windows")) {
            if (is64) return OS.WINDOWS64;
            else return OS.WINDOWS;
        } else if (osName.contains("Mac") || osName.contains("darwin")) {
            return OS.MACOS;
        } else {
            return OS.OTHER;
        }
    }
}
