package com.galaran.spleditor.gui.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Range limiter for LongField
 * @author Galaran
 */
public class LongFieldPropertyListener implements PropertyChangeListener {
    
    private final LongEditableField field;
    private final long min;
    private final long max;

    public LongFieldPropertyListener(long min, long max, LongEditableField field) {
        this.field = field;
        this.min = min;
        this.max = max;
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (!(evt.getNewValue() instanceof Long)) {
            return;
        }

        long newVal = (Long) evt.getNewValue();
        if (newVal > max) {
            newVal = max;
        } else if (newVal < min) {
            newVal = min;
        }

        field.setLongValue(new Long(newVal));
    }
}
