package com.galaran.spleditor;

import java.io.File;
import java.io.PrintWriter;
import javax.swing.UIManager;

import com.galaran.spleditor.gui.GuiController;
import static com.galaran.spleditor.gui.Utils.*;


public class SplEditor {
    
    public static void main(String[] args) {
        try {
            
            
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                printErrorMessageAndExit("Error while setup look and feel: " + ex.getMessage());
            }

            File savesDir = new File("../saves");
            if (args.length > 0) {
                savesDir = new File(args[0]); // debug
            }

            if (!savesDir.exists() || !savesDir.isDirectory()) {
                printErrorMessageAndExit("Put this jar to <minecraft folder>/bin");
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
}
