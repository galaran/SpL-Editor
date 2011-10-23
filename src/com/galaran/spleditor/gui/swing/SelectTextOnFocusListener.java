package com.galaran.spleditor.gui.swing;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SelectTextOnFocusListener implements FocusListener {

    public void focusGained(final FocusEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JTextField tf = (JTextField) e.getSource();
                tf.selectAll();
            }
        });
    }

    public void focusLost(FocusEvent e) { }
    
}
