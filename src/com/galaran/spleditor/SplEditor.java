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
            File savesDir = checkIsMcDirAndGetSavesPath(new File("./.."));
            if (savesDir == null) {
                // try to find minecraft in the default path
                savesDir = getDefaultSavesPath();
                if (savesDir == null) {
                    Utils.printErrorMessageAndExit("Minecraft not found in the default path.\n"
                            + "Put this jar/exe to the minecraft/bin folder");
                }
            }

            LevelLoader loader = new LevelLoader(savesDir);
            GuiController guiController = new GuiController(loader);
            guiController.showGui();
            
        } catch (Throwable t) {
            try {
                File errLog = new File("spl_crash.log");
                errLog.createNewFile();
                PrintWriter errWriter = new PrintWriter(errLog);
                t.printStackTrace(errWriter);
                errWriter.close();
            } catch (Exception ex) { }
            printErrorMessageAndExit("An unexpected error. Please see spl_crash.log file for details");
        }
    }
    
    private static File getDefaultSavesPath() {
        String mcPath = null;
        String osName = System.getProperty("os.name").toLowerCase();
        if(osName.contains("windows")) {
            mcPath = System.getenv("APPDATA") + "/.minecraft";
        } else if(osName.contains("linux")) {
            mcPath = System.getProperty("user.home") + "/.minecraft";
        } else if(osName.contains("mac")) {
            mcPath = System.getProperty("user.home") + "/Library/Application Support/minecraft";
        } else {
            return null; // BSD? Solaris?
        }
        
        File mcDir = new File(mcPath);
        
        return checkIsMcDirAndGetSavesPath(mcDir);
    }
    
    private static File checkIsMcDirAndGetSavesPath(File mcDir) {
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
