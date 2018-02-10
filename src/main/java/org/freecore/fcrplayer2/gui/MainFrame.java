package org.freecore.fcrplayer2.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.freecore.fcrplayer2.Launcher;
import org.freecore.fcrplayer2.components.MemoryMonitor;
import org.freecore.fcrplayer2.components.SpectrumPanel;
import org.freecore.fcrplayer2.player.ChannelPlayer;
import org.freecore.fcrplayer2.player.Player;
import org.freecore.fcrplayer2.utils.GuiUtils;
import org.freecore.fcrplayer2.utils.Utils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("FieldCanBeLocal")
public class MainFrame extends JFrame implements GuiFrame {

    private final int width = 450, height = 333;
    private final String laf = "Nimbus";
    private final String title = "FCRPlayer2 ";
    private String[] metas = new String[2];

    private final Logger logger = LogManager.getLogger("MainFrame");
    private boolean pause = false, stop = false;
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
            monitorCheckC = new GridBagConstraints(),
            heapMonitorC = new GridBagConstraints(),
            trackFieldC = new GridBagConstraints();

    // Flags
    boolean managerFrameOpened = false;
    public boolean mainFrameIconified = false;
    private boolean noMeta = false;

    // Constraints initialization

    {
        // SoundVis
        soundVisC.gridx      = 0; // Положение по X
        soundVisC.gridy      = 0; // Положение по Y
        soundVisC.gridwidth  = 5; // Сколько занимает клеток по X
        soundVisC.gridheight = 3; // Сколько занимает клеток по Y
        soundVisC.weightx    = 0; // На сколько % может растягиваться по X
        soundVisC.weighty    = 0; // На сколько % может растягиваться по Y
        soundVisC.ipadx      = 0; // Сколько пикселей добавлять по X
        soundVisC.ipady      = 0; // Сколько пикселей добавлять по Y
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
        playButtonC.insets = new Insets(1, 1, 0, 0);

        // StopButton
        stopButtonC.gridx      = 1;
        stopButtonC.gridy      = 4;
        stopButtonC.gridwidth  = 1;
        stopButtonC.gridheight = 2;
        stopButtonC.weightx    = 0;
        stopButtonC.weighty    = 0;
        stopButtonC.anchor     = GridBagConstraints.NORTHWEST;
        stopButtonC.fill       = GridBagConstraints.HORIZONTAL;
        stopButtonC.insets = new Insets(1, 0, 0, 0);

        // PauseButton
        pauseButtonC.gridx      = 2;
        pauseButtonC.gridy      = 4;
        pauseButtonC.gridwidth  = 1;
        pauseButtonC.gridheight = 2;
        pauseButtonC.weightx    = 0;
        pauseButtonC.weighty    = 0;
        pauseButtonC.anchor     = GridBagConstraints.NORTHWEST;
        pauseButtonC.fill       = GridBagConstraints.HORIZONTAL;
        pauseButtonC.insets = new Insets(1, 0, 0, 1);

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
        managerButtonC.gridwidth  = 3;
        managerButtonC.gridheight = 2;
        managerButtonC.weightx    = 0;
        managerButtonC.weighty    = 0;
        managerButtonC.anchor     = GridBagConstraints.NORTHWEST;
        managerButtonC.fill       = GridBagConstraints.HORIZONTAL;
        managerButtonC.insets = new Insets(0, 0, 1, 1);

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
        trackFieldC.insets = new Insets(0, 0, 1, 1);
    }

    // Components initialization

    {
        soundVis = new SpectrumPanel(width, 150);
        stationList = new JComboBox<>();
        volumeSlider = new JSlider(0, 100, 0);
        balanceSlider = new JSlider(-5, 5, 0);
        playButton = new JButton(new ImageIcon(Utils.getResource("/icons/play.png")));
        stopButton = new JButton(new ImageIcon(Utils.getResource("/icons/stop.png")));
        pauseButton = new JButton(new ImageIcon(Utils.getResource("/icons/pause.png")));
        managerButton = new JButton(new ImageIcon(Utils.getResource("/icons/radio.png")));
        monitorCheck = new JCheckBox("Show monitor?", true);
        heapMonitor = new MemoryMonitor(130, 70, 700, GuiUtils.getFont("fonts/Hack-Regular.ttf", Font.PLAIN, 11));
        trackField = new JTextField();

        // Frame icon
        this.setIconImage(new ImageIcon(Utils.getResource("/icons/fcrplayer2.png")).getImage());


        // Actions

        // VolumeSlider
        volumeSlider.setValue(Launcher.getConfig().getVolume());
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                int val = volumeSlider.getValue();
                player.setVolume(val * 0.01f);
                volumeSlider.setToolTipText("Volume: " + val);
                Launcher.getConfig().setVolume(val);
            }
        });

        // BalanceSlider
        balanceSlider.setValue(Launcher.getConfig().getBalance());
        balanceSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                int val = balanceSlider.getValue();
                player.setBalance(val * 0.1f);
                balanceSlider.setToolTipText("Balance: " + val);
                Launcher.getConfig().setBalance(val);
            }
        });

        // StationList
        updateStationList();

        // PlayButton
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                player.start();
                if (pause) {
                    pause = false;
                    return;
                }
                if (stop) stop = false;
                player.play(Launcher.getConfig().getStation((String) stationList.getSelectedItem()));
                player.setDefaultVolume(volumeSlider.getValue() * 0.01f);
                player.setDefaultBalance(balanceSlider.getValue() * 0.1f);
                if (metaTimer != null) metaTimer.cancel();
                metaTimer();
                MainFrame.super.setTitle(title + "(Playing: " + stationList.getSelectedItem() + ")");
            }
        });

        // StopButton
        stopButton.addActionListener(actionEvent -> {
            if (pause) {
                player.start();
                pause = false;
            }
            player.stop();
            super.setTitle(title);
            stop = true;
        });

        // PauseButton
        pauseButton.addActionListener(actionEvent -> {
            if (stop) return;
            if (!pause) {
                player.pause();
                pause = true;
            }
        });

        // ManagerButton
        managerButton.addActionListener(actionEvent -> {
            if (managerFrameOpened) return;
            ManagerFrame managerFrame = new ManagerFrame(this);
            managerFrame.frameShow();
        });

        // TrackField
        trackField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (noMeta) return;
                String txt = trackField.getText();
                Utils.copyToClipboard(txt);
                logger.debug("String '" + txt + "' copied to clipboard!");
            }
        });

        // MonitorCheck
        monitorCheck.setSelected(Launcher.getConfig().isMonitor());
        monitorCheck.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                boolean val = monitorCheck.isSelected();
                activeHeapMon(val);
                Launcher.getConfig().setMonitor(val);
            }
        });

        // MainFrame
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent windowEvent) { }

            @Override
            public void windowClosing(WindowEvent windowEvent) { }

            @Override
            public void windowClosed(WindowEvent windowEvent) { }

            @Override
            public void windowIconified(WindowEvent windowEvent) {
                mainFrameIconified = true;
            }

            @Override
            public void windowDeiconified(WindowEvent windowEvent) {
                mainFrameIconified = false;
            }

            @Override
            public void windowActivated(WindowEvent windowEvent) { }

            @Override
            public void windowDeactivated(WindowEvent windowEvent) { }
        });

        // Other options
        trackField.setEditable(false);

        // Tooltips
        volumeSlider.setToolTipText("Volume: " + volumeSlider.getValue());
        balanceSlider.setToolTipText("Balance: " + balanceSlider.getValue());
        trackField.setToolTipText("Click to copy text to clipboard");
        managerButton.setToolTipText("Radio manager");
        playButton.setToolTipText("Play");
        stopButton.setToolTipText("Stop");
        pauseButton.setToolTipText("Pause");
        heapMonitor.setToolTipText("Click to stop or resume");

        // Fonts
        stationList.setFont(GuiUtils.getFont("fonts/Helvetica.otf", Font.PLAIN, 13));
        monitorCheck.setFont(GuiUtils.getFont("fonts/Helvetica.otf", Font.PLAIN, 13));
        trackField.setFont(GuiUtils.getFont("fonts/AvenirNextCyr-Medium.ttf", Font.PLAIN, 13));

        // LAF
        GuiUtils.setlaf(laf);
        GuiUtils.updateComponentsUi(this, soundVis, stationList, playButton,
                stopButton, pauseButton, volumeSlider, balanceSlider, managerButton,
                monitorCheck, heapMonitor, trackField);
    }

    public MainFrame() {
        this.setSize(width, height);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridBagLayout());
        this.setTitle(title);
        this.setVisible(true);
    }

    @Override
    public void frameShow() {
        this.add(soundVis, soundVisC);
        this.add(stationList, stationListC);
        this.add(playButton, playButtonC);
        this.add(stopButton, stopButtonC);
        this.add(pauseButton, pauseButtonC);
        this.add(volumeSlider, volumeSliderC);
        this.add(balanceSlider, balanceSliderC);
        this.add(managerButton, managerButtonC);
        this.add(monitorCheck, monitorCheckC);
        this.add(heapMonitor, heapMonitorC);
        this.add(trackField, trackFieldC);

        player = new ChannelPlayer(soundVis, this);
        player.setVolume(volumeSlider.getValue() * 0.01f);
        player.setBalance(balanceSlider.getValue() * 0.1f);
        activeHeapMon(monitorCheck.isSelected());
        metaTimer();
    }

    @Override
    public void frameClose() {
        this.setVisible(false);
        this.dispose();
    }

    // Other methods

    private void activeHeapMon(boolean act) {
        if (act) {
            heapMonitor.start();
            heapMonitor.setVisible(true);
        } else {
            heapMonitor.stop();
            heapMonitor.setVisible(false);
        }
    }

    public void updateStationList() {
        stationList.removeAllItems();
        for (String s : Launcher.getConfig().getStations()) {
            stationList.addItem(s);
        }
    }

    private void metaTimer() {
        metaTimer = new Timer();
        metaTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    SwingUtilities.invokeAndWait(() -> {
                        String meta = player.getMeta();
                        metas[0] = metas[1];
                        metas[1] = meta;
                        if (!Objects.equals(metas[0], metas[1])) {
                            try {
                                if (meta.equals("")) {
                                    trackField.setText("[NO METADATA FOUND]");
                                    trackField.setEnabled(false);
                                    noMeta = true;
                                    return;
                                }
                            } catch (Exception ignored) { }
                            if (!trackField.isEnabled()) trackField.setEnabled(true);
                            noMeta = false;
                            trackField.setText(meta);
                        }
                    });
                } catch (InvocationTargetException | InterruptedException e) {
                    logger.error("Error in metadata timer!", e);
                }
            }
        }, 200, 1500);
    }
}
