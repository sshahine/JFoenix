package com.aquafx_project.controls.skin.styles;

/**
 * ControlSizeVariant values are used to manipulate the Control size by setting those values as
 * StyleClassDefinition to the Control for CSS styling.
 * 
 * @author claudinezillmann
 * 
 */
public enum ControlSizeVariant implements StyleDefinition {
    /**
     * REGULAR indicates a regular size of Control (based on font size 13).
     */
    REGULAR,
    /**
     * SMALL indicates a small size of Control (based on font size 11).
     */
    SMALL,
    /**
     * MINI indicates a mini size of Control (based on font size 9).
     */
    MINI;

    /**
     * Constructs a String as name for the StyleClass.
     * 
     * @return the name for the ControlSizeVariant
     */
    @Override public String getStyleName() {
        String prefix = "size-variant";
        if (this.equals(SMALL)) {
            return prefix + "-" + "small";
        }
        if (this.equals(MINI)) {
            return prefix + "-" + "mini";
        }
        return null;
    }
}
