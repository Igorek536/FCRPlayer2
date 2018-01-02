package org.freecore.fcrplayer2.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.freecore.fcrplayer2.Launcher;
import org.freecore.fcrplayer2.components.SimpleTableModel;
import org.freecore.fcrplayer2.utils.GuiUtils;
import org.freecore.fcrplayer2.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("FieldCanBeLocal")
public class ManagerFrame extends JFrame implements GuiFrame {

    private final int width = 300, height = 400;
    private final String laf = "Nimbus";
    private final String title = "Radio manager";


    // Components
    private final JTable stationsTable;
    private final JTextField nameField;
    private final JTextField urlField;
    private final JButton createButton;
    private final JButton removeButton;
    private final GridBagConstraints
            stationsTableC = new GridBagConstraints(),
            nameFieldC = new GridBagConstraints(),
            urlFieldC = new GridBagConstraints(),
            createButtonC = new GridBagConstraints(),
            removeButtonC = new GridBagConstraints();

    private final JScrollPane stationsTableS;

    private final SimpleTableModel stationsData = new SimpleTableModel();

    // Other variables
    private final Logger logger = LogManager.getLogger("ManagerFrame");
    private MainFrame mainFrame;

    // Constraits initialization

    {
        // StationsTable
        stationsTableC.gridx      = 0;   // Положение по X
        stationsTableC.gridy      = 0;   // Положение по Y
        stationsTableC.gridwidth  = 2;   // Сколько занимает клеток по X
        stationsTableC.gridheight = 2;   // Сколько занимает клеток по Y
        stationsTableC.weightx    = 0.1; // На сколько % может растягиваться по X
        stationsTableC.weighty    = 0.1; // На сколько % может растягиваться по Y
        stationsTableC.ipadx      = 0;   // Сколько пикселей добавлять по X
        stationsTableC.ipady      = 0;   // Сколько пикселей добавлять по Y
        stationsTableC.anchor     = GridBagConstraints.NORTH;    // Якорь
        stationsTableC.fill       = GridBagConstraints.VERTICAL; // Как будет заполняться?
        stationsTableC.insets = new Insets(0, 0, 1, 0); // top, left, bottom, right  - отступы

        // NameField
        nameFieldC.gridx      = 0;
        nameFieldC.gridy      = 3;
        nameFieldC.gridwidth  = 1;
        nameFieldC.gridheight = 1;
        nameFieldC.weightx    = 0.1;
        nameFieldC.weighty    = 0;
        nameFieldC.anchor     = GridBagConstraints.NORTH;
        nameFieldC.fill       = GridBagConstraints.HORIZONTAL;
        nameFieldC.insets = new Insets(0, 0, 0, 0);

        // UrlField
        urlFieldC.gridx      = 1;
        urlFieldC.gridy      = 3;
        urlFieldC.gridwidth  = 1;
        urlFieldC.gridheight = 1;
        urlFieldC.weightx    = 0.1;
        urlFieldC.weighty    = 0;
        urlFieldC.anchor     = GridBagConstraints.NORTH;
        urlFieldC.fill       = GridBagConstraints.HORIZONTAL;
        urlFieldC.insets = new Insets(0, 0, 0, 0);

        // CreateButton
        createButtonC.gridx      = 0;
        createButtonC.gridy      = 4;
        createButtonC.gridwidth  = 1;
        createButtonC.gridheight = 1;
        createButtonC.weightx    = 0;
        createButtonC.weighty    = 0;
        createButtonC.anchor     = GridBagConstraints.NORTH;
        createButtonC.fill       = GridBagConstraints.HORIZONTAL;
        createButtonC.insets = new Insets(0, 0, 0, 0);


        // RemoveButton
        removeButtonC.gridx      = 1;
        removeButtonC.gridy      = 4;
        removeButtonC.gridwidth  = 1;
        removeButtonC.gridheight = 1;
        removeButtonC.weightx    = 0;
        removeButtonC.weighty    = 0;
        removeButtonC.anchor     = GridBagConstraints.NORTH;
        removeButtonC.fill       = GridBagConstraints.HORIZONTAL;
        removeButtonC.insets = new Insets(0, 0, 0, 0);
    }

    // Component initialization

    {
        String[] cols = {
                "Name",
                "URL"
        };

        stationsData.setCols(cols);

        stationsTable = new JTable(stationsData);
        nameField = new JTextField();
        urlField = new JTextField();
        createButton = new JButton(new ImageIcon(Utils.getResource("/icons/add.png")));
        removeButton = new JButton(new ImageIcon(Utils.getResource("/icons/remove.png")));
        stationsTableS = new JScrollPane(stationsTable);
        stationsTableS.setMinimumSize(new Dimension(width, 200));

        // Actions

        // StationsTable
        stationsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) && !stationsTable.getSelectionModel().isSelectionEmpty()) {
                    StringBuilder rowBuilder = new StringBuilder();
                    rowBuilder.append(stationsData.getValueAt(stationsTable.getSelectedRow(), 0));
                    rowBuilder.append(" ");
                    rowBuilder.append(stationsData.getValueAt(stationsTable.getSelectedRow(), 1));
                    String result = String.valueOf(rowBuilder);
                    Utils.copyToClipboard(result);
                    logger.debug("String '" + result + "' copied to clipboard!");
                }
                super.mouseClicked(e);
            }
        });

        // CreateButton
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (nameField.getText().equals("") || urlField.getText().equals("")) {
                    return;
                }
                Launcher.getConfig().putStation(nameField.getText(), urlField.getText());
                nameField.setText("");
                urlField.setText("");
                updateTableData();
            }
        });

        // RemoveButton
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (stationsTable.getSelectionModel().isSelectionEmpty()) {
                    return;
                }
                String name = (String) stationsData.getValueAt(stationsTable.getSelectedRow(), 0);
                Launcher.getConfig().removeStation(name);
                updateTableData();
            }
        });

        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent windowEvent) {
                mainFrame.managerOpened = true;
            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {
                ManagerFrame.super.setTitle("Goodbye...");
            }

            @Override
            public void windowClosed(WindowEvent windowEvent) {
                mainFrame.managerOpened = false;
            }

            @Override
            public void windowIconified(WindowEvent windowEvent) { }

            @Override
            public void windowDeiconified(WindowEvent windowEvent) { }

            @Override
            public void windowActivated(WindowEvent windowEvent) { }

            @Override
            public void windowDeactivated(WindowEvent windowEvent) { }
        });

        // Tooltips
        stationsTable.setToolTipText("Right click to copy selected row to clipboard");
        createButton.setToolTipText("Add station");
        removeButton.setToolTipText("Remove station");
        nameField.setToolTipText("Station name");
        urlField.setToolTipText("Station url");

        // Fonts
        stationsTable.setFont(GuiUtils.getFont("fonts/Helvetica.otf", Font.PLAIN, 13));
        nameField.setFont(GuiUtils.getFont("fonts/Helvetica.otf", Font.PLAIN, 13));
        urlField.setFont(GuiUtils.getFont("fonts/Helvetica.otf", Font.PLAIN, 13));

        // LAF
        GuiUtils.setlaf(laf);
        GuiUtils.updateComponentsUi(this, stationsTable, nameField, urlField,
                createButton, removeButton);
    }

    ManagerFrame(MainFrame mainFrame) {
        this.setSize(width, height);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(mainFrame);
        this.setLayout(new GridBagLayout());
        this.setVisible(true);
        this.setTitle(title);
        this.mainFrame = mainFrame;
    }

    @Override
    public void init() {
        this.add(stationsTableS, stationsTableC);
        this.add(nameField, nameFieldC);
        this.add(urlField, urlFieldC);
        this.add(createButton, createButtonC);
        this.add(removeButton, removeButtonC);

        updateTableData();
    }

    private void updateTableData() {
        while (stationsData.getRowCount() > 0) {
            stationsData.removeRow(0);
        }
        for (String s : Launcher.getConfig().getStations()) {
            stationsData.addRow(s, Launcher.getConfig().getStation(s));
        }
        mainFrame.updateStationList();
    }
}
