package com.aquafx_project.controls.skin;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;

import com.aquafx_project.controls.skin.effects.FocusBorder;
import com.aquafx_project.controls.skin.effects.Shadow;
import com.sun.javafx.scene.control.skin.ColorPickerSkin;


public class AquaColorPickerSkin extends ColorPickerSkin implements AquaSkin{

    public AquaColorPickerSkin(ColorPicker colorPicker) {
        super(colorPicker);

        registerChangeListener(colorPicker.focusedProperty(), "FOCUSED");
        if (getSkinnable().isFocused()) {
            setFocusBorder();
        } else {
            setDropShadow();
        }
    }

    private void setFocusBorder() {
        getSkinnable().setEffect(new FocusBorder());
    }
    
    private void setDropShadow() {
        getSkinnable().setEffect(new Shadow(false));
    }

    @Override protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);

        if (p == "FOCUSED") {
            if (!(getSkinnable().getParent() instanceof ComboBox)) {
                if (getSkinnable().isFocused()) {
                    setFocusBorder();
                } else {
                    setDropShadow();
                }
            }
        }
    }
}
