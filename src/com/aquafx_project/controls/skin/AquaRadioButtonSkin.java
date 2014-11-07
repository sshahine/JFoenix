package com.aquafx_project.controls.skin;

import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.StackPane;

import com.aquafx_project.controls.skin.effects.FocusBorder;
import com.aquafx_project.controls.skin.effects.Shadow;
import com.sun.javafx.scene.control.skin.RadioButtonSkin;


public class AquaRadioButtonSkin extends RadioButtonSkin implements AquaSkin{

    public AquaRadioButtonSkin(RadioButton radioButton) {
        super(radioButton);
        if (getSkinnable().isFocused()) {
            setFocusBorder();
        } else {
            setDropShadow();
        }
        registerChangeListener(radioButton.focusedProperty(), "FOCUSED");

    }

    private void setFocusBorder() {
        for (Node child : getChildren()) {
            if (child instanceof StackPane) {
                child.setEffect(new FocusBorder());
            }
        }
    }

    private void setDropShadow() {
        for (Node child : getChildren()) {
            if (child.getStyleClass().get(0).equals("radio")) {
                child.setEffect(new Shadow(false));
            }
        }
    }

    @Override protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if (p == "FOCUSED") {
            if (getSkinnable().isFocused()) {
                setFocusBorder();
            } else if (!getSkinnable().isFocused()) {
                setDropShadow();
            }
        }
    }
}
