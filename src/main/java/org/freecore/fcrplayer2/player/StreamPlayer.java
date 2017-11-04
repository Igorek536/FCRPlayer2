package org.freecore.fcrplayer2.player;

import jouvieje.bass.callbacks.DOWNLOADPROC;
import jouvieje.bass.structures.HSTREAM;
import jouvieje.bass.utils.BufferUtils;
import org.freecore.fcrplayer2.components.SpectrumPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

import static jouvieje.bass.Bass.*;
import static jouvieje.bass.defines.BASS_CONFIG.BASS_CONFIG_NET_PLAYLIST;
import static jouvieje.bass.defines.BASS_FILEPOS.*;
import static jouvieje.bass.defines.BASS_STREAM.*;

public class StreamPlayer extends JFrame {

    private HSTREAM stream_channel;
    private Thread radioThread = null;
    private Timer radioTimer = new Timer();
    private TimerTask cacheTask;
    private final Logger logger = LoggerFactory.getLogger(StreamPlayer.class);

    public StreamPlayer() { }

    public StreamPlayer(SpectrumPanel spectrumPanel) {
        cacheTask = new CacheTask(spectrumPanel);
    }

    private final class CacheTask extends TimerTask {

        private SpectrumPanel spectrum = null;

        private CacheTask(SpectrumPanel spectrum) {
            this.spectrum = spectrum;
        }

        @Override
        public void run() {
            int progress;
            progress = (int) (BASS_StreamGetFilePosition(stream_channel, BASS_FILEPOS_BUFFER)
                    * 100 / BASS_StreamGetFilePosition(stream_channel, BASS_FILEPOS_END));

            if (progress > 75 || BASS_StreamGetFilePosition(stream_channel, BASS_FILEPOS_CONNECTED) != 0) {
                BASS_ChannelPlay(stream_channel.asInt(), false);
            }

            if (spectrum != null) {
                try {
                    spectrum.update(stream_channel.asInt());
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void init() {
        BASS_SetConfig(BASS_CONFIG_NET_PLAYLIST, 1);
        BASS_StreamFree(stream_channel);
    }

    private DOWNLOADPROC statusProc = (buffer, length, user) -> {
        if (buffer != null && length == 0) {
            logger.debug("Connection: " + BufferUtils.toString(buffer));
        }
    };

    public void play(String url) {
        if (radioThread != null) return;
        radioThread = new Thread(() -> {
            BASS_StreamFree(stream_channel);
            stream_channel = BASS_StreamCreateURL(url, 0,
                    BASS_STREAM_BLOCK | BASS_STREAM_STATUS | BASS_STREAM_AUTOFREE,
                    statusProc, null);
            if (stream_channel == null) {
                stream_channel = BASS_AAC_StreamCreateURL(url, 0,
                        BASS_STREAM_BLOCK | BASS_STREAM_STATUS | BASS_STREAM_AUTOFREE,
                        statusProc, null);
            }
            if (stream_channel != null) radioTimer.schedule(cacheTask, 0, 25);
        });
        radioThread.start();
    }
}