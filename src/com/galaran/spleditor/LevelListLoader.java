package com.galaran.spleditor;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LevelListLoader {
    
    private final File savesDir;

    LevelListLoader(File savesDir) {
        this.savesDir = savesDir;
    }

    public List<GuiLevel> loadLevels() {
        File[] worldDirs = savesDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        if (worldDirs.length == 0) {
            return null;
        }

        List<GuiLevel> levelList = new ArrayList<GuiLevel>();
        for (File curDir : worldDirs) {
            GuiLevel curLev = loadLevel(curDir);
            if (curLev != null) {
                levelList.add(curLev);
            }
        }

        if (levelList.isEmpty()) {
            return null;
        }

        Collections.sort(levelList, new GuiLevel.LastPlayedComparator());

        return levelList;
    }
    
    private GuiLevel loadLevel(File levelDir) {
        try {
            return new GuiLevel(levelDir);
        } catch (Exception ex) {
            System.err.println("Error loading level.dat for world \"" + levelDir.getName() + "\". This world has been skipped");
            return null;
        }
    }
}
