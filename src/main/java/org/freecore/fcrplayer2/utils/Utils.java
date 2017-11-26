package org.freecore.fcrplayer2.utils;

import org.freecore.fcrplayer2.exceptions.PIDException;
import org.freecore.fcrplayer2.exceptions.UnsupportedOSException;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;

public class Utils {

    public static URL getResource(String path) {
        return Utils.class.getResource(path);
    }

    public static OS getOS() {
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

    // Some edited by me.
    // Original on StackOverflow:
    // https://stackoverflow.com/a/7690178
    public static String getProcessId() throws PIDException {
        // Note: may fail in some JVM implementations
        // therefore fallback has to be provided

        // something like '<pid>@<hostname>', at least in SUN / Oracle JVMs
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        int index = jvmName.indexOf('@');

        if (index < 1) {
            // part before '@' empty (index = 0) / '@' not found (index = -1)
            throw new PIDException();
        }

        try {
            return Long.toString(Long.parseLong(jvmName.substring(0, index)));
        } catch (NumberFormatException e) {
            throw new PIDException();
        }
    }

    public static void killProcess(String pid) throws IOException, UnsupportedOSException {
        if (getOS() == OS.LINUX || getOS() == OS.LINUX64 || getOS() == OS.MACOS) {
            Runtime.getRuntime().exec("kill -9 " + pid);
        } else if (getOS() == OS.WINDOWS || getOS() == OS.WINDOWS64) {
            Runtime.getRuntime().exec("taskkill " + pid);
        } else {
            throw new UnsupportedOSException();
        }
    }
}
