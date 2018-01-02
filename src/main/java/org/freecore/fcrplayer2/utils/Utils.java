package org.freecore.fcrplayer2.utils;

import org.freecore.fcrplayer2.exceptions.ResourceExportException;
import org.freecore.fcrplayer2.exceptions.SerializationException;
import org.freecore.fcrplayer2.exceptions.UnsupportedOSException;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;

/**
 * Utils by Igorek536
 * @author Igorek536
 * @version 1.1
 */

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
        } else if (osName.contains("Mac") || osName.contains("darwin") || osName.contains("OSX")) {
            return OS.MACOS;
        } else {
            return OS.OTHER;
        }
    }

    /*
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
    */

    /**
     * This method kill the current process. It use system commands
     * and use Process API to get current process PID.
     * @throws IOException if system command is wrong
     * @throws UnsupportedOSException if OS is unsupported
     */
    public static void killCurrentProcess() throws IOException, UnsupportedOSException {
        String pid = String.valueOf(ProcessHandle.current().pid());
        if (getOS() == OS.LINUX || getOS() == OS.LINUX64 || getOS() == OS.MACOS) {
            Runtime.getRuntime().exec("kill -9 " + pid);
        } else if (getOS() == OS.WINDOWS || getOS() == OS.WINDOWS64) {
            Runtime.getRuntime().exec("taskkill " + pid);
        } else {
            throw new UnsupportedOSException();
        }
    }

    /**
     * This method return path, where jar file located.
     * @return path of the running jar file.
     */
    public static String getCurrentPath() {
        String result = null;
        try {
            CodeSource codeSource = Utils.class.getProtectionDomain().getCodeSource();
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            result = jarFile.getParentFile().getPath();
        } catch (URISyntaxException ignored) {
        }
        return result;
    }

    /**
     * This method export resource from jar file to current jar's path.
     * Example: exportResource("/icons/play.png");
     * @param resName path to resource. Use "/" before!
     * @throws ResourceExportException if resource name wrong
     */
    public static void exportResource(String resName) throws ResourceExportException {
        try (InputStream stream = Utils.class.getResourceAsStream(resName);
             OutputStream resStreamOut = new FileOutputStream(getCurrentPath() + resName)) {
            if (stream == null) {
                throw new ResourceExportException();
            }
            int readBytes;
            byte[] buffer = new byte[4096];
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (IOException e) {
            throw new ResourceExportException();
        }
    }

    /**
     * This method serialize any object, that implements Serializable interface.
     * @param obj object
     * @param filename file name, where object be stored
     * @throws SerializationException if object couldn't be serialized
     */
    public static void serializeObject(Object obj, String filename) throws SerializationException {
        try (FileOutputStream fos = new FileOutputStream(filename);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(obj);
        } catch (IOException e) {
            throw new SerializationException();
        }
    }

    /**
     * This method deserialize object.
     * @param filename file name of serialized object
     * @return deserialized object
     * @throws SerializationException if object couldn't be deserialized
     */
    public static Object deserializeObject(String filename) throws SerializationException {
        try (FileInputStream fis = new FileInputStream(filename);
        ObjectInputStream in = new ObjectInputStream(fis)) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new SerializationException();
        }
    }

    /**
     * This method copy string to clipboard. Very useful thing)
     * @param text the string to be copied
     */
    public static void copyToClipboard(String text) {
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(text), null);
    }
}
