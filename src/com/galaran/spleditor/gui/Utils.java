package com.galaran.spleditor.gui;

import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JOptionPane;

public class Utils {
   
    static long calculateTime(int newTime, long oldTimeTicks) {
        return oldTimeTicks / 24000 + newTime;
    }
    
    public static <T, E> T getMapKeyByValue(Map<T, E> map, E value) {
        if (value == null) return null;
        
        for (Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        return null;
    }
    
    public static void printErrorMessageAndExit(String message) {
        JOptionPane.showMessageDialog(null, message);
        System.exit(1);
    }
}
