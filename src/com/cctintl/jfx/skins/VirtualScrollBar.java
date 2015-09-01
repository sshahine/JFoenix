package com.cctintl.jfx.skins;

import javafx.scene.control.IndexedCell;
import javafx.scene.control.ScrollBar;

import com.sun.javafx.Utils;

/**
 * This custom ScrollBar is used to map the increment & decrement features
 * to pixel based scrolling rather than thumb/track based scrolling, if the
 * "virtual" attribute is true.
 */
public class VirtualScrollBar extends ScrollBar {
    private final VirtualFlow flow;

    private boolean virtual;
    
    private boolean adjusting;

    public VirtualScrollBar(final VirtualFlow flow) {
        this.flow = flow;
        
        super.valueProperty().addListener(valueModel -> {
            if (isVirtual()/* && oldValue != newValue*/) {
                if (adjusting) {
                    // no-op
                } else {
                    flow.setPosition(getValue());
                }
            }
        });
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    public boolean isVirtual() {
        return this.virtual;
    }

    @Override public void decrement() {
        if (isVirtual()) {
            flow.adjustPixels(-10);
        } else {
            super.decrement();
        }
    }

    @Override public void increment() {
        if (isVirtual()) {
            flow.adjustPixels(10);
        } else {
            super.increment();
        }
    }
    
//    private double lastAdjustValue = 0.0;

    // this method is called when the user clicks in the scrollbar track, so
    // we special-case it to allow for page-up and page-down clicking to work
    // as expected.
    @Override public void adjustValue(double pos) {
        if (isVirtual()) {
//            if (pos == lastAdjustValue) {
//                return;
//            }

            adjusting = true;
            double oldValue = flow.getPosition();
            
            double newValue = ((getMax() - getMin()) * Utils.clamp(0, pos, 1))+getMin();
            if (newValue < oldValue) {
                IndexedCell<?> cell = flow.getFirstVisibleCell();
                if (cell == null) return;
                flow.showAsLast(cell);
            } else if (newValue > oldValue) {
                IndexedCell<?> cell = flow.getLastVisibleCell();
                if (cell == null) return;
                flow.showAsFirst(cell);
            }
//            lastAdjustValue = pos;
            
            adjusting = false;
        } else {
            super.adjustValue(pos);
        }
    }
}