package org.freecore.fcrplayer2.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class GuiUtils {

    private static final Logger logger = LoggerFactory.getLogger("GuiUtils");

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
            if (frame.getExtendedState() == JFrame.ICONIFIED) {
                result = true;
            } else {
                result = false;
            }
        }
        return result;
    }
}
