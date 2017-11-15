package org.freecore.fcrplayer2.gui;

import org.freecore.fcrplayer2.components.MemoryMonitor;
import org.freecore.fcrplayer2.components.SpectrumPanel;
import org.freecore.fcrplayer2.player.ChannelPlayer;
import org.freecore.fcrplayer2.player.Player;
import org.freecore.fcrplayer2.utils.GuiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("FieldCanBeLocal")
public class MainFrame extends JFrame {

    private final int width = 450, height = 315;
    private final String laf = "Nimbus";
    private final int sliderMin = 0, sliderMax = 100, sliderCurr = 0;

    String mp3_rr = "http://air.radiorecord.ru:8101/rr_320";
    String aac_test2 = "http://ruhit3.imgradio.pro:80/RusHit48";

    String[] strs = {"hello", "QWERTYUIOPASDFGHJKLZXCVBNMQWERTYUIOPASDFGHJKLZXCVBNMQWERTYUIOPASDFGHJKLZXCVBNMN", "YYYYYEAH!"};
    private final Logger logger = LoggerFactory.getLogger("MainFrame");
    private boolean pause = false;
    private Timer metaTimer;

    // Player
    private Player player = null;

    // Components
    private final SpectrumPanel soundVis;
    private final JComboBox<String> stationList;
    private final JSlider volumeSlider;
    private final JButton playButton;
    private final JButton stopButton;
    private final JButton pauseButton;
    private final JButton managerButton;
    private final JButton aboutButton;
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
            managerButtonC = new GridBagConstraints(),
            aboutButtonC = new GridBagConstraints(),
            monitorCheckC = new GridBagConstraints(),
            heapMonitorC = new GridBagConstraints(),
            trackFieldC = new GridBagConstraints();

    // Constraints initialization

    {
        // SoundVis
        soundVisC.gridx      = 0; // Положение по X
        soundVisC.gridy      = 0; // Положение по Y
        soundVisC.gridwidth  = 5; // Сколько занимает клеток по X
        soundVisC.gridheight = 3; // Сколько занимает клеток по Y
        soundVisC.weightx    = 0; // На сколько может растягиваться по X
        soundVisC.weighty    = 0; // На сколько может растягиваться по Y
        soundVisC.anchor     = GridBagConstraints.NORTH;        // Якорь
        soundVisC.fill       = GridBagConstraints.HORIZONTAL;   // Как будет заполняться?
        soundVisC.insets = new Insets(0, 0, 1, 0); // top, left, bottom, right  - отступы

        // StationList
        stationListC.gridx      = 0;
        stationListC.gridy      = 3;
        stationListC.gridwidth  = 5;
        stationListC.gridheight = 1;
        stationListC.weightx    = 0;
        stationListC.weighty    = 0;
        stationListC.anchor     = GridBagConstraints.NORTH;
        stationListC.fill       = GridBagConstraints.HORIZONTAL;
        stationListC.insets = new Insets( 1, 0, 1, 0);

        // PlayButton
        playButtonC.gridx      = 0;
        playButtonC.gridy      = 4;
        playButtonC.gridwidth  = 1;
        playButtonC.gridheight = 1;
        playButtonC.weightx    = 0;
        playButtonC.weighty    = 0;
        playButtonC.anchor     = GridBagConstraints.NORTHEAST;
        playButtonC.fill       = GridBagConstraints.HORIZONTAL;
        playButtonC.insets = new Insets(1, 0, 0, 1);

        // StopButton
        stopButtonC.gridx      = 1;
        stopButtonC.gridy      = 4;
        stopButtonC.gridwidth  = 1;
        stopButtonC.gridheight = 1;
        stopButtonC.weightx    = 0;
        stopButtonC.weighty    = 0;
        stopButtonC.anchor     = GridBagConstraints.NORTHWEST;
        stopButtonC.fill       = GridBagConstraints.HORIZONTAL;
        stopButtonC.insets = new Insets(1, 1, 0, 1);

        // PauseButton
        pauseButtonC.gridx      = 2;
        pauseButtonC.gridy      = 4;
        pauseButtonC.gridwidth  = 1;
        pauseButtonC.gridheight = 1;
        pauseButtonC.weightx    = 0;
        pauseButtonC.weighty    = 0;
        pauseButtonC.anchor     = GridBagConstraints.NORTHWEST;
        pauseButtonC.fill       = GridBagConstraints.HORIZONTAL;
        pauseButtonC.insets = new Insets(1, 1, 0, 1);

        // VolumeSlider
        volumeSliderC.gridx      = 3;
        volumeSliderC.gridy      = 4;
        volumeSliderC.gridwidth  = 2;
        volumeSliderC.gridheight = 1;
        volumeSliderC.weightx    = 0;
        volumeSliderC.weighty    = 0;
        volumeSliderC.anchor     = GridBagConstraints.NORTHWEST;
        volumeSliderC.fill       = GridBagConstraints.HORIZONTAL;
        volumeSliderC.insets = new Insets(1, 1, 1, 1);

        // ManagerButton
        managerButtonC.gridx      = 0;
        managerButtonC.gridy      = 5;
        managerButtonC.gridwidth  = 2;
        managerButtonC.gridheight = 1;
        managerButtonC.weightx    = 0;
        managerButtonC.weighty    = 0;
        managerButtonC.anchor     = GridBagConstraints.NORTHWEST;
        managerButtonC.fill       = GridBagConstraints.HORIZONTAL;
        managerButtonC.insets = new Insets(0, 0, 1, 1);

        // AboutButton
        aboutButtonC.gridx      = 2;
        aboutButtonC.gridy      = 5;
        aboutButtonC.gridwidth  = 1;
        aboutButtonC.gridheight = 1;
        aboutButtonC.weightx    = 0;
        aboutButtonC.weighty    = 0;
        aboutButtonC.anchor     = GridBagConstraints.NORTHWEST;
        aboutButtonC.fill       = GridBagConstraints.HORIZONTAL;
        aboutButtonC.insets = new Insets(0, 1, 1, 1);

        // MonitorCheck
        monitorCheckC.gridx      = 0;
        monitorCheckC.gridy      = 6;
        monitorCheckC.gridwidth  = 3;
        monitorCheckC.gridheight = 1;
        monitorCheckC.weightx    = 0;
        monitorCheckC.weighty    = 0;
        monitorCheckC.anchor     = GridBagConstraints.NORTHWEST;
        monitorCheckC.fill       = GridBagConstraints.HORIZONTAL;
        monitorCheckC.insets = new Insets(1, 0, 1, 1);

        // HeapMonitor
        heapMonitorC.gridx      = 4;
        heapMonitorC.gridy      = 5;
        heapMonitorC.gridwidth  = 1;
        heapMonitorC.gridheight = 3;
        heapMonitorC.weightx    = 0;
        heapMonitorC.weighty    = 0;
        heapMonitorC.anchor     = GridBagConstraints.EAST;
        heapMonitorC.fill       = GridBagConstraints.NONE;
        heapMonitorC.insets = new Insets(1, 1, 1, 1);

        // TrackField
        trackFieldC.gridx      = 0;
        trackFieldC.gridy      = 7;
        trackFieldC.gridwidth  = 4;
        trackFieldC.gridheight = 1;
        trackFieldC.weightx    = 0.6;
        trackFieldC.weighty    = 0;
        trackFieldC.anchor     = GridBagConstraints.SOUTH;
        trackFieldC.fill       = GridBagConstraints.HORIZONTAL;
        trackFieldC.insets = new Insets(1, 0, 1, 1);
    }

    // Components initialization

    {
        soundVis = new SpectrumPanel(width, 150);
        stationList = new JComboBox<>(strs);
        volumeSlider = new JSlider(sliderMin, sliderMax, sliderCurr);
        playButton = new JButton("Play");
        stopButton = new JButton("Stop");
        pauseButton = new JButton("Pause");
        managerButton = new JButton("Radio Manager");
        aboutButton = new JButton("About");
        monitorCheck = new JCheckBox("Show memory monitor?");
        heapMonitor = new MemoryMonitor(130, 70, 700);
        trackField = new JTextField();

        // Actions

        // VolumeSlider
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                player.setVolume(volumeSlider.getValue() * 0.01f);
                volumeSlider.setToolTipText("Volume: " + volumeSlider.getValue());
            }
        });

        // PlayButton
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                player.play(mp3_rr);
                player.start();
                player.setDefaultVolume(volumeSlider.getValue() * 0.01f);
                if (metaTimer != null) metaTimer.cancel();
                metaTimer();
                if (pause) {
                    pause = false;
                    pauseButton.setText("Pause");
                }
            }
        });

        // StopButton
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                player.stop();
            }
        });

        // PauseButton
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!pause) {
                    player.pause();
                    pauseButton.setText("Resume");
                    pause = true;
                } else {
                    player.start();
                    pauseButton.setText("Pause");
                    pause = false;
                }
            }
        });

        // TrackField
        trackField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                String txt = trackField.getText();
                Toolkit.getDefaultToolkit()
                        .getSystemClipboard()
                        .setContents(new StringSelection(txt), null);
                logger.debug("String '" + txt + "' copied to clipboard!");
            }
        });

        // Other options

        volumeSlider.setToolTipText("Volume: " + volumeSlider.getValue());
        trackField.setToolTipText("Click to copy text to clipboard");

        volumeSlider.setMinorTickSpacing(1);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        trackField.setEditable(false);

        // LAF

        GuiUtils.setlaf(laf);
        GuiUtils.updateComponentsUi(this, soundVis, stationList, playButton,
                stopButton, pauseButton, volumeSlider, managerButton, aboutButton,
                monitorCheck, heapMonitor, trackField);
    }

    public MainFrame() {
        this.setSize(width, height);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridBagLayout());
        this.setVisible(true);
    }

    private void metaTimer() {
        metaTimer = new Timer();
        metaTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            trackField.setText(player.getMeta());
                        }
                    });
                } catch (InvocationTargetException | InterruptedException e) {
                    logger.error("Err in SwingWorker", e);
                }
            }
        }, 200, 1500);
    }

    public void init() {
        this.add(soundVis, soundVisC);
        this.add(stationList, stationListC);
        this.add(playButton, playButtonC);
        this.add(stopButton, stopButtonC);
        this.add(pauseButton, pauseButtonC);
        this.add(volumeSlider, volumeSliderC);
        this.add(managerButton, managerButtonC);
        this.add(aboutButton, aboutButtonC);
        this.add(monitorCheck, monitorCheckC);
        this.add(heapMonitor, heapMonitorC);
        this.add(trackField, trackFieldC);

        player = new ChannelPlayer(soundVis);
        heapMonitor.start();
        metaTimer();
    }
}
