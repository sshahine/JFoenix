package com.aquafx_project.controls.skin.styles;

/**
 * The IllegalStyleCombinationException indicates, that incompatible Styles, which cannot be
 * combined, were applied to the same Control. This can be used by a Styler, to prohibit certain
 * combinations.
 * 
 * @author claudinezillmann
 * 
 */
public class IllegalStyleCombinationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param s
     *            The detail message.
     */
    public IllegalStyleCombinationException(String s) {
        super(s);
    }

    /**
     * Constructs a new exception with a default detail message.
     */
    public IllegalStyleCombinationException() {
        this("The applied StyleDefinitions cannot be combined");
    }
}
