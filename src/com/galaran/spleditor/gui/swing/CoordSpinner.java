package com.galaran.spleditor.gui.swing;

import java.awt.Dimension;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.ParseException;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class CoordSpinner extends JSpinner implements MouseWheelListener, LongEditableField {
    
    private static final int STEP = 25;
    private static final int BIG_STEP = 100;
    private static final int TINY_STEP = 10;
    
    public static final long WORLD_MAX_COORD = 30000000;
    
    private final SpinnerModel coordModel = new SpinnerNumberModel(
            new Long(0), new Long(Long.MIN_VALUE), new Long(Long.MAX_VALUE), new Long(STEP));
    
    @SuppressWarnings("LeakingThisInConstructor")
    public CoordSpinner() {
        setModel(coordModel);
        
        // size
        Dimension psize = getPreferredSize();
        psize.width = 40;
        setPreferredSize(psize);

        // listeners
        JTextField editor = ((JSpinner.NumberEditor) getEditor()).getTextField();
        addMouseWheelListener(this);
        editor.addFocusListener(new SelectTextOnFocusListener());
        editor.addPropertyChangeListener(new LongFieldPropertyListener(-WORLD_MAX_COORD, WORLD_MAX_COORD, this));
        
        editor.setHorizontalAlignment(JTextField.LEFT);
    }
    
    public void mouseWheelMoved(MouseWheelEvent e) {
        try {
            commitEdit();
        } catch (ParseException ex) { }

        int step = STEP;
        if (e.isShiftDown()) {
            step = BIG_STEP;
        } else if (e.isAltDown()) {
            step = TINY_STEP;
        }

        long curVal = (Long) getValue();
        setValue(curVal - e.getWheelRotation() * step);
    }
    
    public long getCoord() {
        return (Long) coordModel.getValue();
    }

    public void setLongValue(Long val) {
        coordModel.setValue(val);
    }
    
    public long getLongValue() {
        return numberValue().longValue();
    }
    
    public int getIntValue() {
        return numberValue().intValue();
    }
    
    private Number numberValue() {
        return (Number) getValue();
    }
}
