package org.freecore.fcrplayer2.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utils for swing gui by Igorek536
 * @author Igorek536
 * @version 1.1
 */

public class GuiUtils {

    private static final Logger logger = LogManager.getLogger("GuiUtils");

    public static void setlaf(String laf) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (laf.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("LookAndFeel " + laf + " not found!", e);
        }
    }

    public static void updateComponentsUi(Component... components) {
        for (Component component : components) {
            SwingUtilities.updateComponentTreeUI(component);
        }
    }

    public static boolean isWindowIconified() {
        boolean result = false;
        for (Frame frame : JFrame.getFrames()) {
            result = frame.getExtendedState() == JFrame.ICONIFIED;
        }
        return result;
    }

    /**
     * Example:
     *         setJtableColWidth(table1, new int[]{1, 5}, new int[]{2, 5})
     *         set 5 width for col with index 1 and 2
     *
     *
     * @param table - JTable's object. Should be not null!
     * @param colWidth - int[column index, width]
     */
    public static void setJtableColWidth(JTable table, int[]... colWidth) {
        for (int[] a : colWidth) {
            if (a.length == 2) {
                if (a[1] != 0) {
                    table.getColumnModel().getColumn(a[0]).setMinWidth(a[1]);
                    table.getColumnModel().getColumn(a[0]).setPreferredWidth(a[1]);
                }
            }
        }
    }

    /**
     * Example: Font f = GuiUtils.getFont("fonts/font.ttf", Font.PLAIN, 11);
     * @param path path to font in resources folder(without "/" at first)
     * @param type font type (plain - Font.PLAIN  bold - Font.BOLD etc.)
     * @param size size of font
     * @return font
     */
    public static Font getFont(String path, int type, int size) {
        Font result = null;
        ClassLoader classLoader = GuiUtils.class.getClassLoader();
        try (InputStream fontStream = classLoader.getResourceAsStream(path)) {
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            result = font.deriveFont(type, size);
        } catch (IOException | FontFormatException ignored) {
        }
        return result;
    }
}
