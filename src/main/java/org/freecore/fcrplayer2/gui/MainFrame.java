package org.freecore.fcrplayer2.gui;

import org.freecore.fcrplayer2.components.SpectrumPanel;
import org.freecore.fcrplayer2.player.StreamPlayer;

import javax.swing.*;

@SuppressWarnings("FieldCanBeLocal")
public class MainFrame extends JFrame {

    private final int width = 400, height = 150;
    private final SpectrumPanel spectrumPanel = new SpectrumPanel(width, height);

    public MainFrame() {
        this.setSize(spectrumPanel.getSize());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.add(spectrumPanel);
        this.setVisible(true);
    }

    public void init() {
        String mp3_rr = "http://air.radiorecord.ru:8101/rr_320";
        String aac_test2 = "http://ruhit3.imgradio.pro:80/RusHit48";
        StreamPlayer player = new StreamPlayer(spectrumPanel);
        player.play(mp3_rr);
    }
}
