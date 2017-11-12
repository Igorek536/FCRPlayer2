package org.freecore.fcrplayer2.player;

import jouvieje.bass.callbacks.DOWNLOADPROC;
import jouvieje.bass.structures.HMUSIC;
import jouvieje.bass.structures.HSTREAM;
import jouvieje.bass.utils.BufferUtils;
import org.freecore.fcrplayer2.components.SpectrumPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import static jouvieje.bass.Bass.*;
import static jouvieje.bass.defines.BASS_CONFIG.BASS_CONFIG_NET_PLAYLIST;
import static jouvieje.bass.defines.BASS_FILEPOS.*;
import static jouvieje.bass.defines.BASS_STREAM.*;

public class ChannelPlayer implements Player {

    private HSTREAM channel;
    private HMUSIC music;
    private Thread radioThread = null;
    private final Timer radioTimer = new Timer();
    private TimerTask cacheTask;
    private final Logger logger = LoggerFactory.getLogger("ChannelPlayer");

    public ChannelPlayer() {
        BASS_SetConfig(BASS_CONFIG_NET_PLAYLIST, 1);
        BASS_StreamFree(channel);
    }

    public ChannelPlayer(SpectrumPanel spectrumPanel) {
        cacheTask = new CacheTask(spectrumPanel);
        BASS_SetConfig(BASS_CONFIG_NET_PLAYLIST, 1);
        BASS_StreamFree(channel);
    }

    private final class CacheTask extends TimerTask {

        private SpectrumPanel spectrum = null;

        private CacheTask(SpectrumPanel spectrum) {
            this.spectrum = spectrum;
        }

        @Override
        public void run() {
            int progress;
            progress = (int) (BASS_StreamGetFilePosition(channel, BASS_FILEPOS_BUFFER)
                    * 100 / BASS_StreamGetFilePosition(channel, BASS_FILEPOS_END));

            if (progress > 75 || BASS_StreamGetFilePosition(channel, BASS_FILEPOS_CONNECTED) != 0) {
                BASS_ChannelPlay(channel.asInt(), false);
            }

            if (spectrum != null) {
                try {
                    spectrum.update(channel.asInt());
                } catch (Exception ignored) {
                }
            }
        }
    }

    private DOWNLOADPROC statusProc = (buffer, length, user) -> {
        if (buffer != null && length == 0)
            logger.debug("Connection: " + BufferUtils.toString(buffer));
    };

    @Override
    public void play(String url) {
        if (radioThread != null) return;
        radioThread = new Thread(() -> {
            BASS_StreamFree(channel);
            channel = BASS_StreamCreateURL(url, 0,
                    BASS_STREAM_BLOCK | BASS_STREAM_STATUS | BASS_STREAM_AUTOFREE,
                    statusProc, null);
            if (channel == null) {
                logger.debug("Trying to use AAC plugin...");
                channel = BASS_AAC_StreamCreateURL(url, 0,
                        BASS_STREAM_BLOCK | BASS_STREAM_STATUS | BASS_STREAM_AUTOFREE,
                        statusProc, null);
                if (channel != null) logger.debug("This is AAC stream! It should works!");
            } else {
                logger.debug("Using default!");
            }
            if (channel != null) radioTimer.schedule(cacheTask, 0, 25);
        });
        radioThread.start();
    }

    @Override
    public void play(File file) {

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
    public void setVolume(float volume) {
        BASS_SetVolume(volume);
    }

    @Override
    public String getTrack() {
        return null;
    }
}