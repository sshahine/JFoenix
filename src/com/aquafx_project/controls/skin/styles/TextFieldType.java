package com.aquafx_project.controls.skin.styles;

public enum TextFieldType implements StyleDefinition {
    /**
     * REGULAR indicates a regular Button.
     */
    REGULAR,
    /**
     * SEARCH indicates a SearchField with a magnifying glass and more rounded corners.
     */
    SEARCH,
    /**
     * ROUND_RECT indicates a TextField with more rounded (higher radius) borders than a regular
     * Button.
     */
    ROUND_RECT;

    /**
     * Constructs a String as name for the StyleClass.
     * 
     * @return the name for the TextFieldType
     */
    @Override public String getStyleName() {
        String prefix = "textfield-type";
        if (this.equals(SEARCH)) {
            return prefix + "-" + "search";
        }
        if (this.equals(ROUND_RECT)) {
            return prefix + "-" + "round-rect";
        }
        return null;
    }

}
