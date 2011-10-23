package com.galaran.spleditor.gui.swing;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class DocumentListenerAdapter implements DocumentListener {
    
    public abstract void onChange();

    public void insertUpdate(DocumentEvent e) {
        onChange();
    }

    public void removeUpdate(DocumentEvent e) {
        onChange();
    }

    public void changedUpdate(DocumentEvent e) {
        onChange();
    }
}
