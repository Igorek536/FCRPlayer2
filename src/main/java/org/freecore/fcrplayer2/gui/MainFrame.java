package org.freecore.fcrplayer2.gui;

import org.freecore.fcrplayer2.components.MemoryMonitor;
import org.freecore.fcrplayer2.components.SpectrumPanel;
import org.freecore.fcrplayer2.player.ChannelPlayer;
import org.freecore.fcrplayer2.player.Player;
import org.freecore.fcrplayer2.utils.GuiUtils;
import org.freecore.fcrplayer2.utils.Utils;
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
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("FieldCanBeLocal")
public class MainFrame extends JFrame implements GuiFrame {

    private final int width = 450, height = 330;
    private final String laf = "Nimbus";
    private final int sliderMin = 0, sliderMax = 100, sliderCurr = 0;
    private String[] metas = new String[2];

    String mp3_rr = "http://air.radiorecord.ru:8101/rr_320";
    String aac_test2 = "http://ruhit3.imgradio.pro:80/RusHit48";

    String[] strs = {"hello", "123456789 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30", "YYYYYEAH!"};
    private final Logger logger = LoggerFactory.getLogger("MainFrame");
    private boolean pause = false;
    private Timer metaTimer;

    // Player
    private Player player = null;

    // Components
    private final SpectrumPanel soundVis;
    private final JComboBox<String> stationList;
    private final JSlider volumeSlider;
    private final JSlider balanceSlider;
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
            balanceSliderC = new GridBagConstraints(),
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
        playButtonC.gridheight = 2;
        playButtonC.weightx    = 0;
        playButtonC.weighty    = 0;
        playButtonC.anchor     = GridBagConstraints.NORTHEAST;
        playButtonC.fill       = GridBagConstraints.HORIZONTAL;
        playButtonC.insets = new Insets(1, 0, 0, 1);

        // StopButton
        stopButtonC.gridx      = 1;
        stopButtonC.gridy      = 4;
        stopButtonC.gridwidth  = 1;
        stopButtonC.gridheight = 2;
        stopButtonC.weightx    = 0;
        stopButtonC.weighty    = 0;
        stopButtonC.anchor     = GridBagConstraints.NORTHWEST;
        stopButtonC.fill       = GridBagConstraints.HORIZONTAL;
        stopButtonC.insets = new Insets(1, 1, 0, 1);

        // PauseButton
        pauseButtonC.gridx      = 2;
        pauseButtonC.gridy      = 4;
        pauseButtonC.gridwidth  = 1;
        pauseButtonC.gridheight = 2;
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

        // BalanceSlider
        balanceSliderC.gridx      = 3;
        balanceSliderC.gridy      = 5;
        balanceSliderC.gridwidth  = 2;
        balanceSliderC.gridheight = 1;
        balanceSliderC.weightx    = 0;
        balanceSliderC.weighty    = 0;
        balanceSliderC.anchor     = GridBagConstraints.NORTHWEST;
        balanceSliderC.fill       = GridBagConstraints.HORIZONTAL;
        balanceSliderC.insets = new Insets(1, 1, 1, 1);

        // ManagerButton
        managerButtonC.gridx      = 0;
        managerButtonC.gridy      = 6;
        managerButtonC.gridwidth  = 2;
        managerButtonC.gridheight = 2;
        managerButtonC.weightx    = 0;
        managerButtonC.weighty    = 0;
        managerButtonC.anchor     = GridBagConstraints.NORTHWEST;
        managerButtonC.fill       = GridBagConstraints.HORIZONTAL;
        managerButtonC.insets = new Insets(0, 0, 1, 1);

        // AboutButton
        aboutButtonC.gridx      = 2;
        aboutButtonC.gridy      = 6;
        aboutButtonC.gridwidth  = 1;
        aboutButtonC.gridheight = 2;
        aboutButtonC.weightx    = 0;
        aboutButtonC.weighty    = 0;
        aboutButtonC.anchor     = GridBagConstraints.NORTHWEST;
        aboutButtonC.fill       = GridBagConstraints.HORIZONTAL;
        aboutButtonC.insets = new Insets(0, 1, 1, 1);

        // MonitorCheck
        monitorCheckC.gridx      = 3;
        monitorCheckC.gridy      = 6;
        monitorCheckC.gridwidth  = 1;
        monitorCheckC.gridheight = 1;
        monitorCheckC.weightx    = 0;
        monitorCheckC.weighty    = 0;
        monitorCheckC.anchor     = GridBagConstraints.NORTHWEST;
        monitorCheckC.fill       = GridBagConstraints.HORIZONTAL;
        monitorCheckC.insets = new Insets(1, 0, 1, 1);

        // HeapMonitor
        heapMonitorC.gridx      = 4;
        heapMonitorC.gridy      = 6;
        heapMonitorC.gridwidth  = 1;
        heapMonitorC.gridheight = 3;
        heapMonitorC.weightx    = 0;
        heapMonitorC.weighty    = 0;
        heapMonitorC.anchor     = GridBagConstraints.EAST;
        heapMonitorC.fill       = GridBagConstraints.NONE;
        heapMonitorC.insets = new Insets(1, 1, 1, 1);

        // TrackField
        trackFieldC.gridx      = 0;
        trackFieldC.gridy      = 8;
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
        balanceSlider = new JSlider(-5, 5, 0);
        playButton = new JButton(new ImageIcon(Utils.getResource("/icons/play.png")));
        stopButton = new JButton(new ImageIcon(Utils.getResource("/icons/stop.png")));
        pauseButton = new JButton(new ImageIcon(Utils.getResource("/icons/pause.png")));
        managerButton = new JButton(new ImageIcon(Utils.getResource("/icons/radio.png")));
        aboutButton = new JButton(new ImageIcon(Utils.getResource("/icons/about.png")));
        monitorCheck = new JCheckBox("Show monitor?", true);
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

        // BalanceSlider
        balanceSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                player.setBalance(balanceSlider.getValue() * 0.1f);
                balanceSlider.setToolTipText("Balance: " + balanceSlider.getValue());
            }
        });

        // PlayButton
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                player.play(mp3_rr);
                player.start();
                player.setDefaultVolume(volumeSlider.getValue() * 0.01f);
                player.setDefaultBalance(balanceSlider.getValue() * 0.1f);
                if (metaTimer != null) metaTimer.cancel();
                metaTimer();
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
                    pause = true;
                } else {
                    player.start();
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

        // MonitorCheck
        monitorCheck.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (monitorCheck.isSelected())
                    activeHeapMon(true);
                else
                    activeHeapMon(false);
            }
        });

        // Other options

        volumeSlider.setToolTipText("Volume: " + volumeSlider.getValue());
        balanceSlider.setToolTipText("Balance: " + balanceSlider.getValue());
        trackField.setToolTipText("Click to copy text to clipboard");
        trackField.setEditable(false);

        // LAF

        GuiUtils.setlaf(laf);
        GuiUtils.updateComponentsUi(this, soundVis, stationList, playButton,
                stopButton, pauseButton, volumeSlider, balanceSlider, managerButton, aboutButton,
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

    private void activeHeapMon(boolean act) {
        if (act) {
            heapMonitor.start();
            heapMonitor.setVisible(true);
        } else {
            heapMonitor.stop();
            heapMonitor.setVisible(false);
        }
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
                            String meta = player.getMeta();
                            metas[0] = metas[1];
                            metas[1] = meta;
                            if (!Objects.equals(metas[0], metas[1])) {
                                trackField.setText(meta);
                            }
                        }
                    });
                } catch (InvocationTargetException | InterruptedException e) {
                    logger.error("MetaTimer exeption!", e);
                }
            }
        }, 200, 1500);
    }

    @Override
    public void init() {
        this.add(soundVis, soundVisC);
        this.add(stationList, stationListC);
        this.add(playButton, playButtonC);
        this.add(stopButton, stopButtonC);
        this.add(pauseButton, pauseButtonC);
        this.add(volumeSlider, volumeSliderC);
        this.add(balanceSlider, balanceSliderC);
        this.add(managerButton, managerButtonC);
        this.add(aboutButton, aboutButtonC);
        this.add(monitorCheck, monitorCheckC);
        this.add(heapMonitor, heapMonitorC);
        this.add(trackField, trackFieldC);

        player = new ChannelPlayer(soundVis);
        activeHeapMon(true);
        metaTimer();
    }
}
