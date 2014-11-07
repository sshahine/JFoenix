package com.aquafx_project.controls.skin;

import javafx.scene.control.MenuButton;

import com.aquafx_project.controls.skin.effects.FocusBorder;
import com.aquafx_project.controls.skin.effects.Shadow;
import com.sun.javafx.scene.control.skin.MenuButtonSkin;


public class AquaMenuButtonSkin extends MenuButtonSkin implements AquaSkin{

    public AquaMenuButtonSkin(MenuButton menuButton) {
        super(menuButton);

        registerChangeListener(menuButton.focusedProperty(), "FOCUSED");
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
            if (getSkinnable().isFocused()) {
                setFocusBorder();
            } else {
                setDropShadow();
            }
        }
    }
}
