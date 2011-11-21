package com.galaran.spleditor.gui;

import java.io.IOException;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import com.galaran.spleditor.LevelListLoader;
import com.galaran.spleditor.GuiLevel;
import com.galaran.spleditor.GuiLevel.Dimension;
import com.galaran.spleditor.GuiLevel.GameMode;
import static com.galaran.spleditor.gui.Utils.*;

public class GuiController {
    
    private final LevelListLoader loader;
    private final Object worldDirsLock = new Object();
    private final SplGui gui;
        
    private DefaultComboBoxModel levelModel;
    private int prevSelectedIndex;
    
    public GuiController(LevelListLoader loader) {
        this.loader = loader;

        Object[] levels = loader.loadLevels().toArray();
        if (levels.length == 0) {
            Utils.printErrorMessageAndExit("You have no worlds in minecraft");
        }
        
        // init GUI
        gui = new SplGui();
        initControlMaps();
        bindActions();
        
        // set levels to GUI
        levelModel = new DefaultComboBoxModel(levels);
        gui.worldDirSelect.setModel(levelModel);
        prevSelectedIndex = levelModel.getIndexOf(levelModel.getSelectedItem());
        switchToSelectedLevel();
        
        registerListeners();
    }
    
    private void saveGuiStateToLevel(GuiLevel lev) {
        // dimension
        lev.dimension = butDimMap.get(gui.dimGroup.getSelection());
        
        // position
        lev.posX = gui.posX.getIntValue();
        lev.posY = gui.posY.getIntValue();
        lev.posZ = gui.posZ.getIntValue();
        
        // spawn
        lev.spawnX = gui.spawnX.getIntValue();
        lev.spawnY = gui.spawnY.getIntValue();
        lev.spawnZ = gui.spawnZ.getIntValue();
        
        // time
        ButtonModel selTime = gui.timeGroup.getSelection();
        if (selTime != null) {
            lev.time = calculateTime(butTimeMap.get(selTime), lev.time);
        }
        
        // weather
        lev.raining = gui.raining.isSelected();
        lev.rainTime = gui.getRainTimeTicks();
        if (lev.raining) {
            lev.thundering = gui.thundering.isSelected();
            lev.thunderTime = gui.getRainTimeTicks();
        }
        
        // hp & food & level
        lev.hp = gui.hp.getShortValue();
        lev.food = gui.food.getIntValue();
        lev.level = gui.level.getIntValue();
        
        // mode
        GuiLevel.GameMode mode = radioButModeMap.get(gui.modeGroup.getSelection());
        if (!gui.hardcore.isVisible()) { // 1.8 beta level
            // add 1.8 postfix
            mode = GuiLevel.GameMode.valueOf(mode.toString() + "_1_8");
        }
        lev.gameMode = mode;
    }
    
    private void switchToSelectedLevel() {
        GuiLevel lev = (GuiLevel) levelModel.getSelectedItem();

        gui.worldName.setText(lev.name);
        gui.seed.setText(String.valueOf(lev.seed));
        
        // dimension
        gui.dimGroup.clearSelection();
        gui.dimGroup.setSelected(getMapKeyByValue(butDimMap, lev.dimension), true);
        
        // position
        gui.posX.setValue(new Long(lev.posX));
        gui.posY.setValue(new Long(lev.posY));
        gui.posZ.setValue(new Long(lev.posZ));
        
        // spawn
        gui.spawnX.setValue(new Long(lev.spawnX));
        gui.spawnY.setValue(new Long(lev.spawnY));
        gui.spawnZ.setValue(new Long(lev.spawnZ));
        
        // time
        gui.timeGroup.clearSelection();
        ButtonModel activeTimeButton = getMapKeyByValue(butTimeMap, (int) (lev.time % 24000));
        if (activeTimeButton != null) {
            gui.timeGroup.setSelected(activeTimeButton, true);
        }
        
        // weather
        gui.raining.setSelected(lev.raining);
        if (lev.raining) {
            gui.showRainWillEndInLabel();
            gui.thundering.setEnabled(true);
            gui.thundering.setSelected(lev.thundering);
        } else {
            gui.showNextRainInLabel();
            gui.thundering.setEnabled(false);
            gui.thundering.setSelected(false);
        }
        gui.setRainTimeTicks(lev.rainTime);
        
        // hp, food, level
        gui.hp.setValue(new Long(lev.hp));
        gui.food.setValue(new Long(lev.food));
        gui.level.setValue(new Long(lev.level));
        
        // mode
        // set hardcore option visibility and heart image
        GameMode mode = lev.gameMode;
        if (mode.toString().contains("_1_8")) { // 1.8 level
            mode = GameMode.valueOf(mode.toString().replace("_1_8", "")); // set to normal version
            gui.hardcore.setVisible(false);
            gui.setHeartHardcore(false);
        } else {
            gui.hardcore.setVisible(true);
            gui.setHeartHardcore(mode == GameMode.HARDCORE);
        }
        gui.modeGroup.clearSelection();
        gui.modeGroup.setSelected(getMapKeyByValue(radioButModeMap, mode), true);
    }
    
    public void showGui() {
        gui.show();
    }
    
    private void registerListeners() {
        gui.worldDirSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int newSelected = gui.worldDirSelect.getSelectedIndex();
                if (newSelected == prevSelectedIndex) return; //selected same item
                
                saveGuiStateToLevel((GuiLevel) levelModel.getElementAt(prevSelectedIndex));
                prevSelectedIndex = newSelected;
                switchToSelectedLevel();
            }
        });
        
        gui.spawnHereButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (gui.dimGroup.getSelection() == gui.dimOverworld.getModel()) {
                    // selected overworld dimension
                    gui.spawnX.setValue(gui.posX.getValue());
                    gui.spawnY.setValue(gui.posY.getValue());
                    gui.spawnZ.setValue(gui.posZ.getValue());
                } else {
                    gui.showMessage("Spawn is possible only in the Overworld");
                }
            }
        });
        
        gui.goHomeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // select Overworld
                gui.dimGroup.clearSelection();
                gui.dimGroup.setSelected(gui.dimOverworld.getModel(), true);
                
                gui.posX.setValue(gui.spawnX.getValue());
                gui.posY.setValue(gui.spawnY.getValue());
                gui.posZ.setValue(gui.spawnZ.getValue());
            }
        });
        
        ActionListener hpFoodLevelButtonsListener = new HpFoodLevelButtonsListener();
        gui.hpFullButton.addActionListener(hpFoodLevelButtonsListener);
        gui.hpMaxButton.addActionListener(hpFoodLevelButtonsListener);
        gui.foodFullButton.addActionListener(hpFoodLevelButtonsListener);
        gui.foodMaxButton.addActionListener(hpFoodLevelButtonsListener);
        gui.levelHighButton.addActionListener(hpFoodLevelButtonsListener);

        gui.raining.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (gui.raining.isSelected()) {
                    gui.showRainWillEndInLabel();
                    gui.thundering.setEnabled(true);
                } else {
                    gui.showNextRainInLabel();
                    gui.thundering.setSelected(false);
                    gui.thundering.setEnabled(false);
                }
            }
        });
        
        ActionListener modeListener = new ModeListener();
        gui.hardcore.addActionListener(modeListener);
        gui.survival.addActionListener(modeListener);
        gui.creative.addActionListener(modeListener);
    }
    
    private void bindActions() {
        AbstractAction saveAction = new AbstractAction(SplGui.SAVE_BUTTON_TEXT) {
            public void actionPerformed(ActionEvent e) {
                new SaveWorker().execute();
            }
        };
        AbstractAction reloadAction = new AbstractAction(SplGui.RELOAD_BUTTON_TEXT) {
            public void actionPerformed(ActionEvent e) {
                new ReloadWorker().execute();
            }
        };
        gui.saveButton.setAction(saveAction);
        gui.reloadButton.setAction(reloadAction);
        
        // bind keys
        InputMap imap = gui.rootBox.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        imap.put(KeyStroke.getKeyStroke("ctrl S"), "action.save");
        imap.put(KeyStroke.getKeyStroke("ctrl R"), "action.reload");
        imap.put(KeyStroke.getKeyStroke("F5"), "action.reload");
        
        ActionMap amap = gui.rootBox.getActionMap();
        amap.put("action.save", saveAction);
        amap.put("action.reload", reloadAction);
    }
    
    private class HpFoodLevelButtonsListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JButton src = (JButton) e.getSource();
            
            if (src == gui.hpFullButton) {
                gui.setHpFull();
            } else if (src == gui.hpMaxButton) {
                gui.setHpMax();
            } else if (src == gui.foodFullButton) {
                gui.setFoodFull();
            } else if (src == gui.foodMaxButton) {
                gui.setFoodMax();
            } else if (src == gui.levelHighButton) {
                gui.setLevelHigh();
            } 
        }
    }

    private class ModeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (!gui.hardcore.isVisible()) return; // 1.8 beta
            
            gui.setHeartHardcore(gui.hardcore.isSelected());
        }
    }
    
    private class ReloadWorker extends SwingWorker<List<GuiLevel>, Object> {
        
        @Override
        protected List<GuiLevel> doInBackground() throws Exception {
            synchronized (worldDirsLock) {
                return loader.loadLevels();
            }
        }

        @Override
        protected void done() {
            try {
                if (get() == null) {
                    gui.showMessage("All Worlds has been deleted?");
                    return;
                }

                DefaultComboBoxModel newModel = new DefaultComboBoxModel(get().toArray());
                
                // find and set selected item from old model
                String selectedDirName = ((GuiLevel) levelModel.getElementAt(prevSelectedIndex)).dir.getName();
                int indexInNewModel = -1;
                for (int i = 0; i < newModel.getSize(); i++) {
                    GuiLevel cur = (GuiLevel) newModel.getElementAt(i);
                    if (cur.dir.getName().equals(selectedDirName)) {
                        indexInNewModel = i;
                        break;
                    }
                }
                
                // not found
                if (indexInNewModel == -1) {
                    indexInNewModel = 0;
                }
                
                newModel.setSelectedItem(newModel.getElementAt(indexInNewModel));
                levelModel = newModel;
                prevSelectedIndex = indexInNewModel;
                gui.worldDirSelect.setModel(levelModel);
                
                switchToSelectedLevel();
                
                gui.showReloadDoneMessage();
            } catch (Exception ex) { }
        }
        
    }
    
    private class SaveWorker extends SwingWorker<Boolean, Object> {
        
        @Override
        protected Boolean doInBackground() {
            synchronized (worldDirsLock) {
                try {
                    GuiLevel selectedLev = (GuiLevel) levelModel.getSelectedItem();
                    saveGuiStateToLevel(selectedLev);
                    selectedLev.save();
                } catch (IOException ex) {
                    gui.showMessage(ex.getMessage());
                    return false;
                }

                return true;
            }
        }

        @Override
        protected void done() {
            try {
                if (get().booleanValue())
                    gui.showSaveDoneMessage();
            } catch (Exception ex) { }
        }
    }
    
    private final Map<ButtonModel, GuiLevel.Dimension> butDimMap = new HashMap<ButtonModel, Dimension>();
    private final Map<ButtonModel, Integer> butTimeMap = new HashMap<ButtonModel, Integer>();
    private final Map<ButtonModel, GuiLevel.GameMode> radioButModeMap = new HashMap<ButtonModel, GuiLevel.GameMode>();
    
    private void initControlMaps() {
        butDimMap.put(gui.dimOverworld.getModel(), Dimension.OVERWORLD);
        butDimMap.put(gui.dimNether.getModel(), Dimension.NETHER);
        butDimMap.put(gui.dimTheEnd.getModel(), Dimension.THE_END);
        
        butTimeMap.put(gui.timeDay.getModel(), 0);
        butTimeMap.put(gui.timeSunset.getModel(), 12000 );
        butTimeMap.put(gui.timeNight.getModel(), 13800);
        butTimeMap.put(gui.timeSunrise.getModel(), 22200);
        
        radioButModeMap.put(gui.creative.getModel(), GameMode.CREATIVE);
        radioButModeMap.put(gui.survival.getModel(), GameMode.SURVIVAL);
        radioButModeMap.put(gui.hardcore.getModel(), GameMode.HARDCORE);
    }
    
}
