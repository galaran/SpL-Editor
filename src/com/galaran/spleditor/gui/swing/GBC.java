package com.galaran.spleditor.gui.swing;

import java.awt.GridBagConstraints;

public class GBC extends GridBagConstraints {

    /**
    Constructs a GBC with a given gridx and gridy position and
    all other grid bag constraint values set to the default.
    @param gridx the gridx position
    @param gridy the gridy position
     */
    
    public GBC(int gridx, int gridy) {
        this.gridx = gridx;
        this.gridy = gridy;
    }

    public GBC(int gridx, int gridy, int gridwidth, int gridheight) {
        this(gridx, gridy);
        
        this.gridwidth = gridwidth;
        this.gridheight = gridheight;
    }

    /**
    Sets the cell spans.
    @param gridwidth the cell span in x-direction
    @param gridheight the cell span in y-direction
    @return this object for further modification
     */
    public GBC span(int gridwidth, int gridheight) {
        this.gridwidth = gridwidth;
        this.gridheight = gridheight;
        return this;
    }

    /**
    Sets the anchor.
    @param anchor the anchor value
    @return this object for further modification
     */
    public GBC anchor(int anchor) {
        this.anchor = anchor;
        return this;
    }

    /**
    Sets the fill direction.
    @param fill the fill direction
    @return this object for further modification
     */
    public GBC fill(int fill) {
        this.fill = fill;
        return this;
    }

    /**
    Sets the cell weights.
    @param weightx the cell weight in x-direction
    @param weighty the cell weight in y-direction
    @return this object for further modification
     */
    public GBC weight(double weightx, double weighty) {
        this.weightx = weightx;
        this.weighty = weighty;
        return this;
    }

    /**
    Sets the insets of this cell.
    @param distance the spacing to use in all directions
    @return this object for further modification
     */
    public GBC allInsets(int distance) {
        this.insets = new java.awt.Insets(distance, distance, distance, distance);
        return this;
    }

    /**
    Sets the insets of this cell.
    @param top the spacing to use on top
    @param left the spacing to use to the left
    @param bottom the spacing to use on the bottom
    @param right the spacing to use to the right
    @return this object for further modification
     */
    public GBC insets(int top, int left, int bottom, int right) {
        this.insets = new java.awt.Insets(
                top, left, bottom, right);
        return this;
    }

    public GBC topInset(int top) {
        insets.top = top;
        return this;
    }
    public GBC leftInset(int left) {
        insets.left = left;
        return this;
    }
    public GBC bottomInset(int bottom) {
        insets.bottom = bottom;
        return this;
    }
    public GBC rightInset(int right) {
        insets.right = right;
        return this;
    }

    /**
    Sets the internal padding
    @param ipadx the internal padding in x-direction
    @param ipady the internal padding in y-direction
    @return this object for further modification
     */
    public GBC ipad(int ipadx, int ipady) {
        this.ipadx = ipadx;
        this.ipady = ipady;
        return this;
    }
}
