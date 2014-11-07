package com.aquafx_project.controls.skin;

import java.util.List;

import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;

import com.aquafx_project.controls.skin.effects.FocusBorder;
import com.aquafx_project.controls.skin.effects.Shadow;
import com.aquafx_project.controls.skin.styles.ButtonType;
import com.sun.javafx.scene.control.skin.ToggleButtonSkin;


public class AquaToggleButtonSkin extends ToggleButtonSkin implements AquaSkin{

    public AquaToggleButtonSkin(ToggleButton button) {
        super(button);

        registerChangeListener(button.focusedProperty(), "FOCUSED");
        registerChangeListener(button.selectedProperty(), "SELECTED");

        if (getSkinnable().isFocused()) {
            setFocusBorder();
        } else {
            setDropShadow();
        }

        if (getSkinnable().isSelected()) {
            adjustToggleGroupBorders();
        }

    }

    private void setFocusBorder() {
        getSkinnable().setEffect(new FocusBorder());
    }

    private void setDropShadow() {
        boolean isPill = false;
        if (getSkinnable().getStyleClass().contains(ButtonType.LEFT_PILL.getStyleName()) || getSkinnable().getStyleClass().contains(
                ButtonType.CENTER_PILL.getStyleName()) || getSkinnable().getStyleClass().contains(
                ButtonType.RIGHT_PILL.getStyleName())) {
            isPill = true;
        }
        getSkinnable().setEffect(new Shadow(isPill));
    }

    @Override protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if (p == "FOCUSED") {
            if (getSkinnable().isFocused()) {
                setFocusBorder();
            } else if (!getSkinnable().isFocused() || getSkinnable().isDisable()) {
                setDropShadow();
            }
        }
        if (p == "SELECTED") {
            adjustToggleGroupBorders();
        }
    }

    private void adjustToggleGroupBorders() {
        if (getSkinnable().isSelected() && getSkinnable().getToggleGroup() != null) {
            List<Toggle> toggles = getSkinnable().getToggleGroup().getToggles();
            int i = toggles.indexOf(getSkinnable().getToggleGroup().getSelectedToggle());
            if (toggles.size() > i + 1) {
                ToggleButton toggle = (ToggleButton) toggles.get(i + 1);
                toggle.getStyleClass().add("neighbor");
                for (int j = 0; toggles.size() > j; j++) {
                    if (j != i + 1) {
                        ((ToggleButton) toggles.get(j)).getStyleClass().remove("neighbor");
                    }
                }
            }
        } else if (!getSkinnable().isSelected() && getSkinnable().getToggleGroup() != null) {
            List<Toggle> toggles = getSkinnable().getToggleGroup().getToggles();
            int i = toggles.indexOf(getSkinnable());
            if (toggles.size() > i + 1) {
                ToggleButton toggle = (ToggleButton) toggles.get(i + 1);
                toggle.getStyleClass().remove("neighbor");
            }
        }
    }
}
