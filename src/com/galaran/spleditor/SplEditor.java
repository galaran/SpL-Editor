package com.galaran.spleditor;

import java.io.File;
import java.io.PrintWriter;
import javax.swing.UIManager;

import com.galaran.spleditor.gui.GuiController;
import com.galaran.spleditor.gui.Utils;
import static com.galaran.spleditor.gui.Utils.*;


public class SplEditor {
    
    public static void main(String[] args) {
        try {
            
            // set native LAF
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // check, whether app jar is in minecraft bin dir
            File savesDir = checkMcDirAndGetSavesDir(new File("./.."));
            if (savesDir == null) {
                // try to find in standart path
                savesDir = getStandartSavesDir();
                if (savesDir == null) {
                    Utils.printErrorMessageAndExit("Error locating minecraft.\n"
                            + "Try to put this jar to the minecraft/bin folder");
                }
            }

            LevelListLoader loader = new LevelListLoader(savesDir);
            GuiController guiController = new GuiController(loader);
            guiController.showGui();
            
        } catch (Throwable t) {
            try {
                File errLog = new File("_crash.log");
                errLog.createNewFile();
                PrintWriter pw = new PrintWriter(errLog);
                t.printStackTrace(pw);
                pw.close();
            } catch (Exception ex) { }
            printErrorMessageAndExit("An unexpected error. Please see _crash.log file for details");
        }
    }
    
    private static File getStandartSavesDir() {
        String mcPath = null;
        String osName = System.getProperty("os.name").toLowerCase();
        if(osName.contains("windows")) {
            mcPath = System.getenv("APPDATA") + "/.minecraft";
        } else if(osName.contains("linux")) {
            mcPath = System.getProperty("user.home") + "\\.minecraft";
        } else if(osName.contains("mac")) {
            mcPath = System.getProperty("user.home") + "/Library/Application Support/minecraft";
        } else {
            return null; // BSD? Solaris?
        }
        
        File mcDir = new File(mcPath);
        
        return checkMcDirAndGetSavesDir(mcDir);
    }
    
    private static File checkMcDirAndGetSavesDir(File mcDir) {
        if (!mcDir.exists() && !mcDir.isDirectory())
            return null;
        if (!new File(mcDir, "bin/minecraft.jar").exists())
            return null;
        if (!new File(mcDir, "resources").exists())
            return null;
        
        File savesDir = new File(mcDir, "saves");
        if (!savesDir.exists() || !savesDir.isDirectory())
            return null;
        
        return savesDir;
    }
}
