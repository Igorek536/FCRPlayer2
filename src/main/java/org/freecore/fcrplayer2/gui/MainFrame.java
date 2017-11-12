package org.freecore.fcrplayer2.gui;

import org.freecore.fcrplayer2.components.MemoryMonitor;
import org.freecore.fcrplayer2.components.SpectrumPanel;
import org.freecore.fcrplayer2.player.ChannelPlayer;
import org.freecore.fcrplayer2.player.Player;
import org.freecore.fcrplayer2.utils.GuiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("FieldCanBeLocal")
public class MainFrame extends JFrame {

    private final int width = 450, height = 340;
    private final String laf = "Metal";

    String[] strs = {"hello", "A lagre string. This is a very large string. Hmm...", "YYYYYEAH!"};
    private final Logger logger = LoggerFactory.getLogger("MainFrame");

    // Components
    private final SpectrumPanel soundVis;
    private final JComboBox<String> stationList;
    private final JSlider volumeSlider;
    private final JButton playButton;
    private final JButton stopButton;
    private final JButton pauseButton;
    private final JCheckBox monitorCheck;
    private final MemoryMonitor heapMonitor;
    private final JTextField trackField;
    private final GridBagConstraints
            soundVisC = new GridBagConstraints(),
            stationListC = new GridBagConstraints(),
            volumeSliderC = new GridBagConstraints(),
            playButtonC = new GridBagConstraints(),
            stopButtonC = new GridBagConstraints(),
            pauseButtonC = new GridBagConstraints(),
            monitorCheckC = new GridBagConstraints(),
            heapMonitorC = new GridBagConstraints(),
            trackFieldC = new GridBagConstraints();
    {
        int sliderMin = 0, sliderMax = 10, sliderCurr = 5;
        soundVis = new SpectrumPanel(width, 150);
        stationList = new JComboBox<>(strs);
        volumeSlider = new JSlider(sliderMin, sliderMax, sliderCurr);
        playButton = new JButton("Play");
        stopButton = new JButton("Stop");
        pauseButton = new JButton("Pause");
        monitorCheck = new JCheckBox("Show memory monitor?");
        heapMonitor = new MemoryMonitor(150, 100, 100);
        trackField = new JTextField();
    }

    public MainFrame() {
        setDefaultLookAndFeelDecorated(true);
        GuiUtils.setlaf(laf);

        this.setSize(width, height);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridBagLayout());
        this.setVisible(true);
    }

    public void init() {
        // SoundVis
        soundVisC.gridx      = 0; // Положение по X
        soundVisC.gridy      = 0; // Положение по Y
        soundVisC.gridwidth  = 4; // Сколько занимает клеток по X
        soundVisC.gridheight = 3; // Сколько занимает клеток по Y
        soundVisC.weightx    = 0; // На сколько может растягиваться по X
        soundVisC.weighty    = 0; // На сколько может растягиваться по Y
        soundVisC.anchor     = GridBagConstraints.NORTH;        // Якорь
        soundVisC.fill       = GridBagConstraints.HORIZONTAL;   // Как будет заполняться?
        soundVisC.insets = new Insets(0, 1, 1, 0); // top, left, bottom, right  - отступы
        this.add(soundVis, soundVisC);

        // StationList
        stationListC.gridx      = 0;
        stationListC.gridy      = 3;
        stationListC.gridwidth  = 4;
        stationListC.gridheight = 1;
        stationListC.weightx    = 0;
        stationListC.weighty    = 0;
        stationListC.anchor     = GridBagConstraints.NORTH;
        stationListC.fill       = GridBagConstraints.HORIZONTAL;
        stationListC.insets = new Insets( 1, 1, 1, 0);
        this.add(stationList, stationListC);

        // PlayButton
        playButtonC.gridx      = 0;
        playButtonC.gridy      = 4;
        playButtonC.gridwidth  = 1;
        playButtonC.gridheight = 1;
        playButtonC.weightx    = 0;
        playButtonC.weighty    = 0;
        playButtonC.anchor     = GridBagConstraints.NORTHEAST;
        playButtonC.fill       = GridBagConstraints.HORIZONTAL;
        playButtonC.insets = new Insets(1, 1, 1, 1);
        this.add(playButton, playButtonC);

        // StopButton
        stopButtonC.gridx      = 1;
        stopButtonC.gridy      = 4;
        stopButtonC.gridwidth  = 1;
        stopButtonC.gridheight = 1;
        stopButtonC.weightx    = 0;
        stopButtonC.weighty    = 0;
        stopButtonC.anchor     = GridBagConstraints.NORTHWEST;
        stopButtonC.fill       = GridBagConstraints.HORIZONTAL;
        stopButtonC.insets = new Insets(1, 1, 1, 1);
        this.add(stopButton, stopButtonC);

        // PauseButton
        pauseButtonC.gridx      = 2;
        pauseButtonC.gridy      = 4;
        pauseButtonC.gridwidth  = 1;
        pauseButtonC.gridheight = 1;
        pauseButtonC.weightx    = 0;
        pauseButtonC.weighty    = 0;
        pauseButtonC.anchor     = GridBagConstraints.NORTHWEST;
        pauseButtonC.fill       = GridBagConstraints.HORIZONTAL;
        pauseButtonC.insets = new Insets(1, 1, 1, 1);
        this.add(pauseButton, pauseButtonC);

        // VolumeSlider
        volumeSliderC.gridx      = 3;
        volumeSliderC.gridy      = 4;
        volumeSliderC.gridwidth  = 1;
        volumeSliderC.gridheight = 1;
        volumeSliderC.weightx    = 0.1;
        volumeSliderC.weighty    = 0;
        volumeSliderC.anchor     = GridBagConstraints.NORTHWEST;
        volumeSliderC.fill       = GridBagConstraints.HORIZONTAL;
        volumeSliderC.insets = new Insets(1, 1, 1, 1);
        volumeSlider.setMinorTickSpacing(1);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        this.add(volumeSlider, volumeSliderC);

        // MonitorCheck
        monitorCheckC.gridx      = 0;
        monitorCheckC.gridy      = 5;
        monitorCheckC.gridwidth  = 3;
        monitorCheckC.gridheight = 1;
        monitorCheckC.weightx    = 0;
        monitorCheckC.weighty    = 0;
        monitorCheckC.anchor     = GridBagConstraints.NORTHWEST;
        monitorCheckC.fill       = GridBagConstraints.HORIZONTAL;
        monitorCheckC.insets = new Insets(1, 1, 1, 1);
        this.add(monitorCheck, monitorCheckC);

        // HeapMonitor
        heapMonitorC.gridx      = 3;
        heapMonitorC.gridy      = 5;
        heapMonitorC.gridwidth  = 1;
        heapMonitorC.gridheight = 2;
        heapMonitorC.weightx    = 0;
        heapMonitorC.weighty    = 0;
        heapMonitorC.anchor     = GridBagConstraints.EAST;
        heapMonitorC.fill       = GridBagConstraints.NONE;
        heapMonitorC.insets = new Insets(1, 1, 1, 1);
        this.add(heapMonitor, heapMonitorC);



        // ------------ //
        String mp3_rr = "http://air.radiorecord.ru:8101/rr_320";
        String aac_test2 = "http://ruhit3.imgradio.pro:80/RusHit48";
        Player player = new ChannelPlayer(soundVis);
        heapMonitor.start();
        //player.setVolume(0.5f);
        player.play(mp3_rr);
    }
}
