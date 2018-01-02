package org.freecore.fcrplayer2.player;

import jouvieje.bass.callbacks.DOWNLOADPROC;
import jouvieje.bass.structures.HSTREAM;
import jouvieje.bass.utils.BufferUtils;
import jouvieje.bass.utils.Pointer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.freecore.fcrplayer2.components.SpectrumPanel;
import org.freecore.fcrplayer2.gui.MainFrame;

import java.util.Timer;
import java.util.TimerTask;

import static jouvieje.bass.Bass.*;
import static jouvieje.bass.defines.BASS_ATTRIB.BASS_ATTRIB_PAN;
import static jouvieje.bass.defines.BASS_ATTRIB.BASS_ATTRIB_VOL;
import static jouvieje.bass.defines.BASS_CONFIG.BASS_CONFIG_NET_PLAYLIST;
import static jouvieje.bass.defines.BASS_FILEPOS.*;
import static jouvieje.bass.defines.BASS_STREAM.*;
import static jouvieje.bass.defines.BASS_TAG.BASS_TAG_META;
import static jouvieje.bass.defines.BASS_TAG.BASS_TAG_OGG;

public class ChannelPlayer implements Player {

    private static HSTREAM channel;
    private float volume;
    private float balance;
    private Thread radioThread = null;
    private Timer channelTimer;
    private Timer visTimer;
    private SpectrumPanel spectrum;
    private final Logger logger = LogManager.getLogger("ChannelPlayer");

    private MainFrame mainFrame;

    public ChannelPlayer(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        BASS_SetConfig(BASS_CONFIG_NET_PLAYLIST, 1);
        BASS_StreamFree(channel);
    }

    public ChannelPlayer(SpectrumPanel spectrum, MainFrame mainFrame) {
        this.spectrum = spectrum;
        this.mainFrame = mainFrame;
        BASS_SetConfig(BASS_CONFIG_NET_PLAYLIST, 1);
        BASS_StreamFree(channel);
    }

    private DOWNLOADPROC statusProc = (buffer, length, user) -> {
        if (buffer != null && length == 0) {
            logger.debug("Connection: " + BufferUtils.toString(buffer));
        }
    };

    private void runTimer() {
        channelTimer = new Timer();
        channelTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int progress;
                progress = (int) (BASS_StreamGetFilePosition(channel, BASS_FILEPOS_BUFFER)
                        * 100 / BASS_StreamGetFilePosition(channel, BASS_FILEPOS_END));

                if (progress > 75 || BASS_StreamGetFilePosition(channel, BASS_FILEPOS_CONNECTED) != 0) {
                    channelTimer.cancel();
                    BASS_ChannelPlay(channel.asInt(), false);
                } else {
                    logger.info("Buffering..." + progress + "%");
                }
            }
        }, 0, 25);
    }

    private void updateVis() {
        visTimer = new Timer();
        visTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (spectrum != null && !mainFrame.mainFrameIconified) {
                    try {
                        spectrum.update(channel.asInt());
                    } catch (Exception ignored) {
                    }
                }
            }
        }, 0, 25);
    }

    @Override
    public void play(String url) {
        if (radioThread != null) {
            return;
        }

        radioThread = new Thread() {
            @Override
            public synchronized void start() {
                try {
                    channelTimer.cancel();
                    visTimer.cancel();
                } catch (Exception ignored) {
                }
                BASS_StreamFree(channel);
                channel = BASS_StreamCreateURL(url, 0,
                        BASS_STREAM_BLOCK | BASS_STREAM_STATUS | BASS_STREAM_AUTOFREE,
                        statusProc, null);
                if (channel == null) {
                    logger.debug("Trying to use AAC plugin...");
                    channel = BASS_AAC_StreamCreateURL(url, 0,
                            BASS_STREAM_BLOCK | BASS_STREAM_STATUS | BASS_STREAM_AUTOFREE,
                            statusProc, null);
                    if (channel != null) {
                        logger.debug("This is AAC stream! It should works!");
                    }
                } else {
                    logger.debug("Using default!");
                }
                if (channel != null) {
                    runTimer();
                    updateVis();
                    BASS_ChannelSetAttribute(channel.asInt(), BASS_ATTRIB_VOL, volume);
                    BASS_ChannelSetAttribute(channel.asInt(), BASS_ATTRIB_PAN, balance);
                }
                radioThread = null;
            }
        };
        radioThread.setName("RadioThread");
        radioThread.start();
    }

    @Override
    public void stop() {
        BASS_Stop();
    }

    @Override
    public void pause() {
        BASS_Pause();
    }

    @Override
    public void start() {
        BASS_Start();
    }

    @Override
    public void setVolume(float volume) {
        if (channel != null) {
            BASS_ChannelSetAttribute(channel.asInt(), BASS_ATTRIB_VOL, volume);
        }
    }

    @Override
    public void setDefaultVolume(float volume) {
        this.volume = volume;
        setVolume(volume);
    }

    @Override
    public void setBalance(float balance) {
        if (channel != null) {
            BASS_ChannelSetAttribute(channel.asInt(), BASS_ATTRIB_PAN, balance);
        }
    }

    @Override
    public void setDefaultBalance(float balance) {
        this.balance = balance;
        setBalance(balance);
    }

    @Override
    public String getMeta() {
        String result = null;
        Pointer metaBuff;
        try {
            metaBuff = BASS_ChannelGetTags(channel.asInt(), BASS_TAG_META);
        } catch (Exception e) {
            return null;
        }

        if (metaBuff != null) {
            String meta = metaBuff.asString();
            // got Shoutcast metadata
            String STREAM_TITLE = "StreamTitle='";
            int index = meta.indexOf(STREAM_TITLE);
            if (index != -1) {
                String p = meta.substring(index + STREAM_TITLE.length());
                if (p.contains(";")) {
                    p = p.substring(0, p.indexOf(";"));
                    p = p.substring(0, p.length() - 1);
                }
                result = p;
            }
        } else {
            metaBuff = BASS_ChannelGetTags(channel.asInt(), BASS_TAG_OGG);
            if (metaBuff != null) {
                String artist = null, title = null;

                // got Icecast/OGG tags
                int length;
                while ((length = metaBuff.asString().length()) > 0) {
                    String s = metaBuff.asString();
                    if (s.startsWith("artist=")) {
                        // found the artist
                        artist = s.substring(7);
                    }
                    if (s.startsWith("title=")) {
                        // found the title
                        title = s.substring(6);
                    }
                    metaBuff = metaBuff.asPointer(length + 1);
                }

                if (artist != null && title != null) {
                    result = artist + " - " + title;
                } else if (title != null) {
                    result = title;
                }
            }
        }
        return (result != null && result.equals("''")) ? "undefined" : result;
    }
}