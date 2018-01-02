package org.freecore.fcrplayer2;

import java.io.Serializable;
import java.util.*;

public class Config implements Serializable {
    private final Map<String, String> stations = new LinkedHashMap<>();
    private int balance;
    private int volume;
    private boolean monitor;

    public List<String> getStations() {
        return new ArrayList<>(stations.keySet());
    }

    public void putStation(String name, String url) {
        stations.put(name, url);
    }

    public void removeStation(String name) {
        stations.remove(name);
    }

    public String getStation(String name) {
        return stations.get(name);
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public boolean isMonitor() {
        return monitor;
    }

    public void setMonitor(boolean monitor) {
        this.monitor = monitor;
    }
}
