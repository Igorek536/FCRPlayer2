package org.freecore.fcrplayer2.player;

public interface Player {
    void play();
    void stop();
    void setSource(SourceType type, String source);
    void setVolume(int volume);
    void setBalance(int balance);
}