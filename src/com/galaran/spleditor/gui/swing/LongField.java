package com.galaran.spleditor.gui.swing;

import javax.swing.JFormattedTextField;

public class LongField extends JFormattedTextField implements LongEditableField {

    public LongField(long min, long max) {
        super(new Long(0)); // long field
        
        setColumns(Math.max(String.valueOf(min).length(), String.valueOf(max).length()) + 1);
        
        addFocusListener(new SelectTextOnFocusListener());
        addPropertyChangeListener(new LongFieldPropertyListener(min, max, this));
    }

    public void setLongValue(Long val) {
        setValue(val);
    }
    
    public long getLongValue() {
        return numberValue().longValue();
    }
    
    public int getIntValue() {
        return numberValue().intValue();
    }
    
    public short getShortValue() {
        return numberValue().shortValue();
    }
    
    private Number numberValue() {
        return (Number) getValue();
    }
    
}
