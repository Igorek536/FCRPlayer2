package org.freecore.fcrplayer2;

import jouvieje.bass.BassInit;
import org.freecore.fcrplayer2.gui.MainFrame;
import org.freecore.fcrplayer2.utils.OSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static jouvieje.bass.Bass.BASS_Free;
import static jouvieje.bass.Bass.BASS_Init;

@SuppressWarnings("FieldCanBeLocal")
class Launcher {

    private boolean isSystemSupported = true;
    private boolean bassInit = false;
    private final Logger logger = LoggerFactory.getLogger("Launcher");
    private final Runtime runtime = Runtime.getRuntime();

    Launcher() {
        setLibPath();
        if (isSystemSupported) {
            logger.debug("Loading natives...");
            loadNatives();
        }
        runtime.addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("Goodbye...");
                BASS_Free();
            }
        });
    }

    private void setLibPath() {
        OSUtils os = new OSUtils();
        StringBuilder sb = new StringBuilder();
        String fs = File.separator;
        sb.append("libraries").append(fs);
        sb.append("natives").append(fs);
        sb.append("bass").append(fs);
        switch (os.getOS()) {
            case LINUX: {
                sb.append("linux").append(fs);
                sb.append("i386");
                logger.debug("OS - Linux, arch i386");
                break;
            }
            case LINUX64: {
                sb.append("linux").append(fs);
                sb.append("amd64");
                logger.debug("OS - Linux, arch amd64");
                break;
            }
            case WINDOWS: {
                sb.append("windows").append(fs);
                sb.append("x86");
                logger.debug("OS - Windows, arch x32");
                break;
            }
            case WINDOWS64: {
                sb.append("windows").append(fs);
                sb.append("x64");
                logger.debug("OS - Windows, arch x64");
                break;
            }
            case MACOS: {
                sb.append("mac");
                logger.debug("OS - OS X, arch amd64");
                break;
            }
            case OTHER: {
                isSystemSupported = false;
                logger.error("This operating system is not supported!");
                break;
            }
        }
        System.setProperty("java.library.path", String.valueOf(sb));
        logger.debug("Set library path to '" + String.valueOf(sb) + "'.");
    }

    private void loadNatives() {
        try {
            BassInit.loadLibraries();
            logger.debug("Natives loaded successfull!");
        } catch (Exception e) {
            logger.error("Unable to load natives!", e);
        }
        if (BassInit.NATIVEBASS_LIBRARY_VERSION() != BassInit.NATIVEBASS_JAR_VERSION()) {
            StringBuilder sb = new StringBuilder();
            sb.append("NativeBass library version is wrong! ");
            sb.append(BassInit.NATIVEBASS_LIBRARY_VERSION());
            sb.append(" ");
            sb.append(BassInit.NATIVEBASS_JAR_VERSION());
            sb.append(".");
            logger.error(String.valueOf(sb));
        }
        bassInit = BASS_Init(-1, 44100, 0, null, null);
        if (bassInit) logger.debug("BASS fully initialized!");
    }

    void launch() {
        MainFrame mainFrame = new MainFrame();
        logger.debug("Launching...");
        mainFrame.init();
    }
}
