package org.freecore.fcrplayer2.player;

public interface Player {
    void play(String url);
    void stop();
    void pause();
    void start();
    void setVolume(float volume);
    void setDefaultVolume(float volume);
    String getMeta();
}