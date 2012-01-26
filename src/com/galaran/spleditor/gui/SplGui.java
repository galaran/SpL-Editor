package com.galaran.spleditor.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import javax.swing.*;

import com.galaran.spleditor.gui.swing.GBC;
import com.galaran.spleditor.gui.swing.CoordSpinner;
import com.galaran.spleditor.gui.swing.SelectTextOnFocusListener;
import com.galaran.spleditor.gui.swing.LongField;
import static com.galaran.spleditor.gui.swing.GBC.*;

class SplGui {
    
    private static final String VERSION = "1.3";
    
    private static final int FRAME_WIDTH = 310;
    private static final int LABEL_GAP = 5;
    private static final int ROW_GAP = 5;
    private static final int LARGE_FIELD_GAP = 10;
    private static final int CONTROL_GAP = 7;
    
    private static final String RAIN_WILL_END_IN = "Rain will end in:";
    private static final String NEXT_RAIN_IN = "Next rain in:";
    private static final String COPY_BUTTON_TEXT = "Copy";
    static final String SAVE_BUTTON_TEXT = "Save";
    static final String RELOAD_BUTTON_TEXT = "Reload";
    
    private static final int TICKS_IN_MINUTE = 1200;
    private static final int LEVEL_HIGH = 999;
    
    private final SplFrame frame;
    private final Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
    
    // Components
    Box rootBox;
    
    JComboBox worldDirSelect = new JComboBox();
    JTextField worldName = new JTextField();
    JTextField seed = new JTextField();
    JButton copySeedButton = new JButton(COPY_BUTTON_TEXT);

    ButtonGroup dimGroup = new ButtonGroup();
    JToggleButton dimOverworld = new JToggleButton("O");
    JToggleButton dimNether = new JToggleButton("N");
    JToggleButton dimTheEnd = new JToggleButton("E");
    CoordSpinner posX = new CoordSpinner();
    CoordSpinner posY = new CoordSpinner();
    CoordSpinner posZ = new CoordSpinner();
    JButton spawnHereButton = new JButton("Spawn here");
    LongField spawnX = buildCoordField();
    LongField spawnY = buildCoordField();
    LongField spawnZ = buildCoordField();
    JButton goHomeButton = new JButton("Go Home");

    ButtonGroup timeGroup = new ButtonGroup();
    JCheckBox raining = new JCheckBox("Raining");
    JCheckBox thundering = new JCheckBox("Thundering");
    private JLabel rainTimeLabel = new JLabel(RAIN_WILL_END_IN); 
    private LongField rainTime = new LongField(0, (long) Integer.MAX_VALUE / TICKS_IN_MINUTE);
    JToggleButton timeSunrise = new JToggleButton("Sunrise");
    JToggleButton timeSunset = new JToggleButton("Sunset");
    JToggleButton timeNight = new JToggleButton("Night");
    JToggleButton timeDay = new JToggleButton("Day");

    private JLabel hpLabel = new JLabel(heart);
    LongField hp = new LongField(0, Short.MAX_VALUE);
    JButton hpFullButton = new JButton("Full");
    JButton hpMaxButton = new JButton("Max");
    LongField food = new LongField(0, Integer.MAX_VALUE);
    JButton foodFullButton = new JButton("Full");
    JButton foodMaxButton = new JButton("Max");
    LongField level = new LongField(0, Integer.MAX_VALUE);
    JButton levelHighButton = new JButton(String.valueOf(LEVEL_HIGH));
    ButtonGroup modeGroup = new ButtonGroup();
    JRadioButton hardcore = new JRadioButton("Hardcore");
    JRadioButton survival = new JRadioButton("Survival");
    JRadioButton creative = new JRadioButton("Creative");

    // Controllable by actions
    JButton reloadButton = new JButton();
    JButton saveButton = new JButton();
    
    SplGui() {
        frame = new SplFrame();
    }
    
    void show() {
        frame.setVisible(true);
    }

    void showMessage(String mess) {
        JOptionPane.showMessageDialog(frame, mess);
    }

    private class SplFrame extends JFrame {

        SplFrame() {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setTitle("SpL Editor " + VERSION);
            setIconImage(loadIcon("mycelium.png").getImage());
        
            rootBox = Box.createVerticalBox();

            // world selection, name seed
            WorldPanel wp = new WorldPanel();
            rootBox.add(wp);
            rootBox.add(Box.createVerticalStrut(15));

            // position & spawn
            CoordPanel pos = new CoordPanel();
            rootBox.add(pos);
            rootBox.add(Box.createVerticalStrut(15));

            // time & weather
            TimeWeatherPanel timeWeather = new TimeWeatherPanel();
            rootBox.add(timeWeather);
            rootBox.add(Box.createVerticalStrut(15));

            // character info & mode
            CharacterModePanel charMode = new CharacterModePanel();
            rootBox.add(charMode);
            rootBox.add(Box.createVerticalStrut(25));

            // save & update buttons
            ButtonPanel bp = new ButtonPanel();
            rootBox.add(bp);


            rootBox.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            getContentPane().add(rootBox);

            pack();

            // locate window
            Dimension res = Toolkit.getDefaultToolkit().getScreenSize();
            int wPos = (res.width - getSize().width) * 2 / 3;
            int hPos = (res.height - getSize().height) / 2;
            setLocation(wPos, hPos);

            setResizable(false);
        }
        
    }

    private class ButtonPanel extends JPanel {

        ButtonPanel() {
            reloadButton.setToolTipText("Reload worlds");
            
            Box box = Box.createHorizontalBox();
            
            setComponentSizes(reloadButton, new Dimension(80, 35));
            setComponentSizes(saveButton, new Dimension(160, 35));
            
            box.add(reloadButton);
            box.add(Box.createGlue());
            box.add(saveButton);
            box.setPreferredSize(new Dimension(FRAME_WIDTH, 35));
            add(box);
        }
    }
    
    private class WorldPanel extends JPanel {
        
        WorldPanel() {
            setLayout(new GridBagLayout());
            
            // set properties
            worldName.setEditable(false);
            seed.setEditable(false);
            copySeedButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    StringSelection clipString = new StringSelection(seed.getText());
                    clip.setContents(clipString, clipString);

                    seed.requestFocusInWindow();
                    seed.selectAll();
                    
                    ButtonMessageShower.showMessage(copySeedButton, "done", COPY_BUTTON_TEXT, 1);
                }
            });
            
            // add to panel
            add(worldDirSelect, new GBC(0, 0, 3, 1).anchor(NORTH).fill(HORIZONTAL).weight(100, 100)
                    .bottomInset(LARGE_FIELD_GAP).ipad(0, 3));
            add(new JLabel("World name:"), new GBC(0, 1).anchor(WEST).fill(VERTICAL)
                    .rightInset(5).bottomInset(ROW_GAP));
            add(worldName, new GBC(1, 1, 2, 1).fill(HORIZONTAL).weight(100, 0).bottomInset(ROW_GAP));
            add(new JLabel("Seed:"), new GBC(0, 2).anchor(EAST).fill(VERTICAL).rightInset(5));
            add(seed, new GBC(1, 2).fill(HORIZONTAL).weight(100, 0));
            add(copySeedButton, new GBC(2, 2).anchor(EAST).fill(VERTICAL).leftInset(CONTROL_GAP));
        }
        
    }
    
    private class CoordPanel extends JPanel {

        CoordPanel() {
            setLayout(new GridBagLayout());
            
            // add coord panels to root panel
            add(buildPositionPanel(), new GBC(0, 0).fill(HORIZONTAL).weight(66, 0));
            add(buildSpawnPanel(), new GBC(1, 0).anchor(SOUTH).fill(HORIZONTAL).weight(100, 0));
        }
        
        private JPanel buildPositionPanel() {
            JPanel posPanel = new JPanel(new GridBagLayout());
            posPanel.setBorder(BorderFactory.createTitledBorder("Position"));
            
            // dimension buttons group
            dimGroup.add(dimOverworld);
            dimGroup.add(dimNether);
            dimGroup.add(dimTheEnd);
            dimOverworld.setToolTipText("The Overworld");
            dimNether.setToolTipText("The Nether");
            dimTheEnd.setToolTipText("The End");
            
            // player dimension panel
            Box dimBox = Box.createHorizontalBox();
            dimBox.add(dimOverworld);
            dimBox.add(dimNether);
            dimBox.add(dimTheEnd);
            posPanel.add(dimBox, new GBC(0, 0, 2, 1).anchor(CENTER).bottomInset(ROW_GAP));
            
            // add player position and spawn here button
            String[] posLabels = { "x:", "y:", "z:" };
            JComponent[] posComponents = { posX, posY, posZ, spawnHereButton };
            addCoordFieldsAndButton(posPanel, 1, posLabels, posComponents);
            
            return posPanel;
        }
        
        private JPanel buildSpawnPanel() {
            JPanel spawnPanel = new JPanel(new GridBagLayout());
            spawnPanel.setBorder(BorderFactory.createTitledBorder("Spawn"));
            
            // set components properties

            spawnX.addFocusListener(new SelectTextOnFocusListener());
            
            String[] spawnLabels = { "X:", "Y:", "Z:" };
            JComponent[] spawnComponents = { spawnX, spawnY, spawnZ, goHomeButton };
            addCoordFieldsAndButton(spawnPanel, 0, spawnLabels, spawnComponents);
            
            return spawnPanel;
        }
        
        /**
         * labels[0] coordComponents[0]
         * labels[1] coordComponents[1]
         * labels[2] coordComponents[2]
         * [     coordComponents[3]   ]
         */
        private void addCoordFieldsAndButton(JPanel target, int vStart, String[] labels, JComponent[] coordComponents) {
            // put to panel
            for (int i = 0; i < 3; i++) {
                target.add(new JLabel(labels[i]), new GBC(0, i + vStart).anchor(EAST).fill(VERTICAL).
                        rightInset(5).bottomInset(ROW_GAP));
                target.add(coordComponents[i], new GBC(1, i + vStart).fill(HORIZONTAL).weight(100, 0)
                        .bottomInset(ROW_GAP));
            }

            target.add(coordComponents[3], new GBC(0, vStart + 3, 2, 1).fill(HORIZONTAL).topInset(CONTROL_GAP));
        }
    }
    
    private class TimeWeatherPanel extends JPanel {

        TimeWeatherPanel() {
            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createTitledBorder("Time & Weather"));
            
            // time buttons group
            timeGroup.add(timeSunrise);
            timeGroup.add(timeSunset);
            timeGroup.add(timeDay);
            timeGroup.add(timeNight);
            
            String toolTip = "Real time minutes";
            rainTime.setToolTipText(toolTip);
            JLabel minLabel = new JLabel("mins");
            minLabel.setToolTipText(toolTip);

            // add components to panel
            add(raining, new GBC(0, 0).anchor(WEST).weight(100, 0));
            add(thundering, new GBC(0, 1).anchor(NORTHWEST).weight(100, 0).bottomInset(30));
            add(rainTimeLabel, new GBC(1, 0).anchor(EAST).rightInset(5));
            add(rainTime, new GBC(2, 0));
            add(minLabel, new GBC(3, 0).anchor(EAST).leftInset(5));
            add(buildTimePanel(), new GBC(0, 1, 4, 1).anchor(SOUTHEAST));
            
        }
        
        private JPanel buildTimePanel() {
            JPanel timePanel = new JPanel(new GridBagLayout());
            timePanel.add(timeSunset, new GBC(0, 0).anchor(SOUTHEAST));
            timePanel.add(timeSunrise, new GBC(1, 0).anchor(SOUTHEAST).leftInset(3));
            timePanel.add(timeNight, new GBC(2, 0).anchor(SOUTHEAST).leftInset(3).ipad(0, 10));
            timePanel.add(timeDay, new GBC(3, 0).anchor(SOUTHEAST).leftInset(3).ipad(0, 10));
            
            return timePanel;
        }
        
    }
    
    private class CharacterModePanel extends JPanel {
        
        CharacterModePanel() {
            setLayout(new GridBagLayout());
            
            JLabel levelLabel = new JLabel(expOrb);
            levelLabel.setToolTipText("Level");
            level.setToolTipText("Level");
            
            // set the same width as food and level fields
            hp.setColumns(String.valueOf(Integer.MAX_VALUE).length() + 1);
            
            // add to panel
            // hp
            add(hpLabel, new GBC(0, 0).anchor(WEST).weight(0, 0).rightInset(LABEL_GAP).bottomInset(ROW_GAP)
                    .leftInset(10));
            add(hp, new GBC(1, 0).anchor(WEST).weight(0, 0).bottomInset(ROW_GAP));
            add(hpFullButton, new GBC(2, 0).anchor(WEST).weight(0, 0).leftInset(CONTROL_GAP).bottomInset(ROW_GAP));
            add(hpMaxButton, new GBC(3, 0).anchor(WEST).weight(0, 0).leftInset(CONTROL_GAP).bottomInset(ROW_GAP));
            
            // hunger
            add(new JLabel(foodPoint), new GBC(0, 1).anchor(WEST).weight(0, 0).rightInset(LABEL_GAP).bottomInset(ROW_GAP)
                    .leftInset(10));
            add(food, new GBC(1, 1).anchor(WEST).weight(0, 0).bottomInset(ROW_GAP));
            add(foodFullButton, new GBC(2, 1).anchor(WEST).weight(0, 0).leftInset(CONTROL_GAP).bottomInset(ROW_GAP));
            add(foodMaxButton, new GBC(3, 1).anchor(WEST).weight(0, 0).leftInset(CONTROL_GAP).bottomInset(ROW_GAP));
            
            // level
            add(levelLabel, new GBC(0, 2).anchor(WEST).weight(0, 0).rightInset(LABEL_GAP)
                    .leftInset(10));
            add(level, new GBC(1, 2).anchor(WEST).weight(0, 0));
            add(levelHighButton, new GBC(2, 2).anchor(WEST).weight(0, 0).leftInset(CONTROL_GAP));
            
            add(buildModePanel(), new GBC(5, 0, 1, 3).anchor(SOUTHEAST).fill(BOTH).weight(100, 100));
        }
        
        private JPanel buildModePanel() {
            JPanel modePan = new JPanel(new GridBagLayout());
            
            modeGroup.add(hardcore);
            modeGroup.add(survival);
            modeGroup.add(creative);
            
            modePan.add(Box.createVerticalStrut(1), new GBC(0, 0).anchor(NORTH).weight(100, 100));
            modePan.add(hardcore, new GBC(0, 1).anchor(SOUTHWEST).weight(0, 0)
                    .leftInset(CONTROL_GAP * 2));
            modePan.add(survival, new GBC(0, 2).anchor(SOUTHWEST).weight(0, 0)
                    .leftInset(CONTROL_GAP * 2).topInset(ROW_GAP / 2));
            modePan.add(creative, new GBC(0, 3).anchor(SOUTHWEST).weight(0, 0)
                    .leftInset(CONTROL_GAP * 2).topInset(ROW_GAP / 2));
            
            return modePan;
        }
    }
    
    private LongField buildCoordField() {
        return new LongField(-CoordSpinner.WORLD_MAX_COORD, CoordSpinner.WORLD_MAX_COORD);
    }
    
    void setHeartHardcore(boolean hardcore) {
        hpLabel.setIcon(hardcore ? heartHardcore : heart);
    }
    
    void showNextRainInLabel() {
        rainTimeLabel.setText(NEXT_RAIN_IN);
    }
    
    void showRainWillEndInLabel() {
        rainTimeLabel.setText(RAIN_WILL_END_IN);
    }
    
    void setRainTimeTicks(int rainTimeTicks) {
        rainTime.setValue(new Long(rainTimeTicks / TICKS_IN_MINUTE));
    }
    
    int getRainTimeTicks() {
        return rainTime.getIntValue() * TICKS_IN_MINUTE;
    }
    
    void showSaveDoneMessage() {
        ButtonMessageShower.showMessage(saveButton, "Saved", SAVE_BUTTON_TEXT, 1);
    }
    
    void showReloadDoneMessage() {
        ButtonMessageShower.showMessage(reloadButton, "Done", RELOAD_BUTTON_TEXT, 1);
    }
    
    void setHpFull() {
        hp.setValue(20);
    }

    void setHpMax() {
        hp.setValue(Short.MAX_VALUE);
    }

    void setFoodFull() {
        food.setValue(20);
    }

    void setFoodMax() {
        food.setValue(Integer.MAX_VALUE);
    }

    void setLevelHigh() {
        level.setValue(LEVEL_HIGH);
    }
    
    private static void setComponentSizes(JComponent c, Dimension size) {
        c.setMinimumSize(size);
        c.setPreferredSize(size);
        c.setMaximumSize(size);
    }
    
    private static class ButtonMessageShower extends Thread {
        
        private final JButton but;
        private final String message;
        private final String normalText;
        private final int dur;
        
        public static void showMessage(JButton but, String message, String normalText, int durationSec) {
            new ButtonMessageShower(but, message, normalText, durationSec).start();
        }
        
        private ButtonMessageShower(JButton but, String message, String normalText, int durationSec) {
            this.but = but;
            this.message = message;
            this.normalText = normalText;
            this.dur = durationSec;
            
            // prevent button resizing
            setComponentSizes(but, but.getSize());
        }

        @Override
        public void run() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    but.setText("<html><center><font color=purple>" + message + "</font></center></html>");
                }
            });
            
            try {
                Thread.sleep(dur * 1000);
            } catch (InterruptedException ex) { }
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    but.setText(normalText);
                }
            });
            
        }
    }
    
    private static final ImageIcon heart = loadIcon("heart.png");
    private static final ImageIcon heartHardcore = loadIcon("heart_hardcore.png");
    private static final ImageIcon foodPoint = loadIcon("foodpoint.png");
    private static final ImageIcon expOrb = loadIcon("exp_orb.png");
    
    public static ImageIcon loadIcon(String fileName) {
        return new ImageIcon(SplGui.class.getClassLoader().getResource("res/" + fileName));
    }
}
