package org.freecore.fcrplayer2.player;

import java.io.File;

public interface Player {
    void play(String url);
    void play(File file);
    void stop();
    void pause();
    void setVolume(float volume);
    String getTrack();
}