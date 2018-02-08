package org.freecore.fcrplayer2;

import jouvieje.bass.BassInit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.freecore.fcrplayer2.exceptions.SerializationException;
import org.freecore.fcrplayer2.exceptions.UnsupportedOSException;
import org.freecore.fcrplayer2.gui.GuiFrame;
import org.freecore.fcrplayer2.gui.MainFrame;
import org.freecore.fcrplayer2.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static jouvieje.bass.Bass.BASS_Init;

@SuppressWarnings("FieldCanBeLocal")
public class Launcher {

    private static final String version = "2.1.0";


    private static Config config;

    private String configName = "config.dat";
    private boolean isSystemSupported = true;
    private boolean bassInit = false;
    private final Logger logger = LogManager.getRootLogger();
    private final Runtime runtime = Runtime.getRuntime();

    Launcher() {
        setLibPath();
        if (isSystemSupported) {
            logger.debug("Loading natives...");
            loadNatives();
        }
        runtime.addShutdownHook(new Thread(() -> {
            logger.debug("Saving configuration...");
            try {
                Utils.serializeObject(config, configName);
            } catch (SerializationException e) {
                logger.error("Can't serialize configuration file!", e);
            }
            logger.info("Goodbye...");
            try {
                Utils.killCurrentProcess();
            } catch (IOException | UnsupportedOSException e) {
                logger.error("Can't kill process on this system! Maybe OS is unsupported!", e);
            }
        }));
        initConfig();
    }

    void launch() {
        GuiFrame mainFrame = new MainFrame();
        logger.debug("Launching...");
        mainFrame.frameShow();
    }

    // Other methods

    public static Config getConfig() {
        return config;
    }

    public static String getVersion() {
        return version;
    }

    private void setLibPath() {
        StringBuilder sb = new StringBuilder();
        String fs = File.separator;
        sb.append("libraries").append(fs);
        sb.append("natives").append(fs);
        sb.append("bass").append(fs);
        switch (Utils.getOS()) {
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

    private void initConfig() {
        if (!Files.exists(Paths.get(Utils.getCurrentPath() + File.separator + configName))) {
            logger.info("Creating new configuration...");
            Config config = new Config();
            config.setVolume(100);
            config.setBalance(0);
            config.setMonitor(false);
            config.putStation("Record Dance", "http://air.radiorecord.ru:8101/rr_320");
            config.putStation("Record Megamix", "http://air.radiorecord.ru:805/mix_320");
            config.putStation("Record Deep", "http://air.radiorecord.ru:805/deep_320");
            config.putStation("Record Club", "http://air.radiorecord.ru:805/club_320");
            config.putStation("Record Future", "http://air.radiorecord.ru:805/fut_320");
            config.putStation("Record Trancemission", "http://air.radiorecord.ru:805/tm_320");
            config.putStation("Record Chillout", "http://air.radiorecord.ru:805/chil_320");
            config.putStation("Record Minimal", "http://air.radiorecord.ru:805/mini_320");
            config.putStation("Record Pirate Station", "http://air.radiorecord.ru:805/ps_320");
            config.putStation("Record Russian Mix", "http://air.radiorecord.ru:805/rus_320");
            config.putStation("Record Vip Mix", "http://air.radiorecord.ru:805/vip_320");
            config.putStation("Record Superdiscoteka 90's", "http://air.radiorecord.ru:805/sd90_320");
            config.putStation("Record Breaks", "http://air.radiorecord.ru:805/brks_320");
            config.putStation("Record Dubstep", "http://air.radiorecord.ru:805/dub_320");
            config.putStation("Record Dancecore", "http://air.radiorecord.ru:805/dc_320");
            config.putStation("Record Techno", "http://air.radiorecord.ru:805/techno_320");
            config.putStation("Record Teodor", "http://air.radiorecord.ru:805/teo_320");
            config.putStation("Record Trap", "http://air.radiorecord.ru:805/trap_320");
            config.putStation("Record Pump'n'Klubb", "http://air.radiorecord.ru:805/pump_320");
            config.putStation("Record Rock", "http://air.radiorecord.ru:805/rock_320");
            config.putStation("Record Medlyak FM", "http://air.radiorecord.ru:805/mdl_320");
            config.putStation("Record GOP FM", "http://air.radiorecord.ru:805/gop_320");
            config.putStation("Record YO FM", "http://air.radiorecord.ru:805/yo_320");
            config.putStation("Record Rave", "http://air.radiorecord.ru:805/rave_320");
            config.putStation("Радио Улитка", "http://air.radioulitka.ru:8000/ulitka_128");
            config.putStation("Radio Rocks", "http://online-radioroks.tavrmedia.ua/RadioROKS");
            config.putStation("Просто Радио", "http://62.80.190.246:8000/ProstoRadiO128");
            config.putStation("Русское Радио", "http://online-rusradio.tavrmedia.ua/RusRadio");
            config.putStation("Наше Радио", "http://cast.radiogroup.com.ua:8000/nashe");
            config.putStation("Европа Плюс", "http://cast.radiogroup.com.ua:8000/europaplus");
            config.putStation("Авто Радио", "http://cast.radiogroup.com.ua:8000/avtoradio");
            config.putStation("DJ FM", "http://r.ezbt.me:80/djfm-ukraine");
            config.putStation("Радио Сон", "http://www.radioson.ru:8009/amsterdam-trance-radio.mp3");
            config.putStation("Best FM", "http://radio.bestfm.fm:8080/bestfm");
            config.putStation("Ретро ФМ", "http://cast.radiogroup.com.ua:8000/retro");
            config.putStation("KissFM UA", "http://online-kissfm.tavrmedia.ua/KissFM_Live");
            config.putStation("Русский Хит", "http://ruhit3.imgradio.pro:80/RusHit48");
            config.putStation("Хит ФМ", "http://online-hitfm.tavrmedia.ua/HitFM");
            Launcher.config = config;
        } else {
            logger.info("Configuration file found!");
            try {
                config = (Config) Utils.deserializeObject(configName);
            } catch (SerializationException e) {
                logger.error("Can't deserialize configuration file!", e);
            }
        }
    }
}
