package com.aquafx_project.controls.skin;

import javafx.scene.Node;
import javafx.scene.control.Slider;

import com.aquafx_project.controls.skin.effects.FocusBorder;
import com.sun.javafx.scene.control.skin.SliderSkin;


public class AquaSliderSkin extends SliderSkin implements AquaSkin{

    public AquaSliderSkin(Slider slider) {
        super(slider);

        if (getSkinnable().isFocused()) {
            setFocusBorder();
        }
        
        registerChangeListener(slider.focusedProperty(), "FOCUSED");

        if (slider.isShowTickMarks()) {
            slider.getStyleClass().add("alternative-thumb");
        }
    }

    @Override protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);

        if (p == "FOCUSED") {
            if (getSkinnable().isFocused()) {
                setFocusBorder();
            } else {
                for (Node child : getChildren()) {
                    if (child.getStyleClass().get(0).equals("thumb")) {
                        child.setEffect(null);
                    }
                }
            }
        }
    }

    private void setFocusBorder() {
        for (Node child : getChildren()) {
            if (child.getStyleClass().get(0).equals("thumb")) {
                child.setEffect(new FocusBorder());
                getSkinnable().impl_reapplyCSS();
            }
        }
    }

}
