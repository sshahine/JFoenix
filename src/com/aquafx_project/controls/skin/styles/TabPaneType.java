package com.aquafx_project.controls.skin.styles;

public enum TabPaneType implements StyleDefinition {
    /**
     * REGULAR indicates a regular TabPane with PillButtons as Tabs.
     */
    REGULAR,
    /**
     * ICON_BUTTONS indicates a TabPane with icon Buttons as Tabs.
     */
    ICON_BUTTONS,
    /**
     * SMALL_ICON_BUTTONS indicates a TabPane with small icon Buttons as Tabs.
     */
    SMALL_ICON_BUTTONS;

    /**
     * Constructs a String as name for the StyleClass.
     * 
     * @return the name for the TextFieldType
     */
    @Override public String getStyleName() {
        String prefix = "tabpane-type";
        if (this.equals(ICON_BUTTONS)) {
            return prefix + "-" + "icon-buttons";
        }
        if (this.equals(SMALL_ICON_BUTTONS)) {
            return prefix + "-" + "small-icon-buttons";
        }
        return null;
    }
}
