package com.aquafx_project.controls.skin.styles;

/**
 * MacOSDefaultIcons values are used to define different Icons that can be applied to Controls, as
 * e.g. Buttons.
 * <p>
 * There are different types of Icons available on Mac OS X.
 * 
 * @author claudinezillmann
 * 
 */
public enum MacOSDefaultIcons implements StyleDefinition {
    /**
     * LEFT indicates an Icon that is an arrow to the left.
     */
    LEFT,
    /**
     * RIGHT indicates an Icon that is an arrow to the right.
     */
    RIGHT,
    /**
     * SHARE indicates an Icon that is the Mac OS share icon.
     */
    SHARE,
    /**
     * SEARCH indicates an Icon that is the Mac OS search icon (magnifying glass).
     */
    SEARCH;

    /**
     * Constructs a String as name for the StyleClass.
     * 
     * @return the name for the MacOSDefaultIcon
     */
    @Override public String getStyleName() {
        String prefix = "icon";
        if (this.equals(LEFT)) {
            return prefix + "-" + "left";
        }
        if (this.equals(RIGHT)) {
            return prefix + "-" + "right";
        }
        if (this.equals(SHARE)) {
            return prefix + "-" + "share";
        }
        if (this.equals(SEARCH)) {
            return prefix + "-" + "search";
        }
        return null;
    }
}
