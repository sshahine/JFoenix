package com.aquafx_project.controls.skin.styles;

/**
 * ButtonType values are used to define different looks (types) of Buttons by setting those values
 * as StyleClassDefinition to the Button for CSS styling.
 * <p>
 * There are different types of Buttons available on Mac OS X, which have a more rounded border, are
 * completely rounded or a set of pills.
 * 
 * @author claudinezillmann
 * 
 */
public enum ButtonType implements StyleDefinition {
    /**
     * REGULAR indicates a regular Button.
     */
    REGULAR,
    /**
     * HELP indicates a round Button (which is usually labeled with the String "?".
     */
    HELP,
    /**
     * ROUND_RECT indicates a Button with more rounded (higher radius) borders than a regular
     * Button.
     */
    ROUND_RECT,
    /**
     * LEFT_PILL indicates a Button, that is the most left Button of a set of Buttons (pills)
     */
    LEFT_PILL,
    /**
     * CENTER_PILL indicates a Button, that is an inner Button of a set of Buttons (pills)
     */
    CENTER_PILL,
    /**
     * RIGHT_PILL indicates a Button, that is the most right Button of a set of Buttons (pills)
     */
    RIGHT_PILL;

    /**
     * Constructs a String as name for the StyleClass.
     * 
     * @return the name for the ButtonType
     */
    @Override public String getStyleName() {
        String prefix = "button-type";
        if (this.equals(HELP)) {
            return prefix + "-" + "help";
        }
        if (this.equals(ROUND_RECT)) {
            return prefix + "-" + "round-rect";
        }
        if (this.equals(LEFT_PILL)) {
            return "left-pill";
        }
        if (this.equals(CENTER_PILL)) {
            return "center-pill";
        }
        if (this.equals(RIGHT_PILL)) {
            return "right-pill";
        }
        return null;
    }

}
