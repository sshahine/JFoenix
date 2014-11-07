package com.aquafx_project.controls.skin;

import javafx.scene.control.TextArea;

import com.aquafx_project.controls.skin.effects.FocusBorder;
import com.sun.javafx.scene.control.skin.TextAreaSkin;


public class AquaTextAreaSkin extends TextAreaSkin implements AquaSkin{

    public AquaTextAreaSkin(TextArea textarea) {
        super(textarea);

        if (getSkinnable().isFocused()) {
            setFocusBorder();
        }
        
        registerChangeListener(textarea.focusedProperty(), "FOCUSED");
    }

    private void setFocusBorder() {
        getSkinnable().setEffect(new FocusBorder());
    }

    @Override protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);

        if (p == "FOCUSED") {
            if (getSkinnable().isFocused()) {
                setFocusBorder();
            } else {
                getSkinnable().setEffect(null);
            }
        }
    }
}
