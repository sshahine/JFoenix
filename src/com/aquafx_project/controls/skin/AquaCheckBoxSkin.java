package com.aquafx_project.controls.skin;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import com.aquafx_project.controls.skin.effects.Shadow;
import com.sun.javafx.scene.control.skin.CheckBoxSkin;


public class AquaCheckBoxSkin extends CheckBoxSkin implements AquaSkin{

    public AquaCheckBoxSkin(CheckBox checkbox) {
        super(checkbox);
        
        registerChangeListener(checkbox.focusedProperty(), "FOCUSED");
        registerChangeListener(checkbox.selectedProperty(), "SELECTED");

        if (getSkinnable().isFocused()) {
            setFocusBorder();
        } else {
            setDropShadow();
        }
        
        final ChangeListener<Boolean> windowFocusChangedListener = new ChangeListener<Boolean>() {

            @Override public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (newValue != null) {
                    if (!newValue.booleanValue()) {
                        if (getSkinnable().isSelected() | getSkinnable().isIndeterminate()) {

                            for (Node child : getChildren()) {
                                if (child.getStyleClass().get(0).equals("box")) {
                                    child.getStyleClass().add("unfocused");
                                }
                            }
                        }
                    } else {
                        for (Node child : getChildren()) {
                            if (child.getStyleClass().get(0).equals("box")) {
                                child.getStyleClass().remove("unfocused");
                            }
                        }
                    }
                }

            }
        };

        getSkinnable().sceneProperty().addListener(new ChangeListener<Scene>() {

            @Override public void changed(ObservableValue<? extends Scene> observableValue, Scene oldScene, Scene newScene) {
                if (oldScene != null && oldScene.getWindow() != null) {
                    oldScene.getWindow().focusedProperty().removeListener(windowFocusChangedListener);
                }
                if (newScene != null && newScene.getWindow() != null) {
                    newScene.getWindow().focusedProperty().addListener(windowFocusChangedListener);
                }

            }
        });

        if (getSkinnable().getScene() != null && getSkinnable().getScene().getWindow() != null) {
            getSkinnable().getScene().getWindow().focusedProperty().addListener(windowFocusChangedListener);
        }

    }

    private void setDropShadow() {
        for (Node child : getChildren()) {
            if (child.getStyleClass().get(0).equals("box")) {
                child.setEffect(new Shadow(false));
            }
        }
    }

    private void setSelectedFocusBorder() {
        InnerShadow innerFocus = new InnerShadow();
        innerFocus.setColor(Color.rgb(104, 155, 201, 0.7));
        innerFocus.setBlurType(BlurType.ONE_PASS_BOX);
        innerFocus.setRadius(6.5);
        innerFocus.setChoke(0.7);
        innerFocus.setOffsetX(0.0);
        innerFocus.setOffsetY(0.0);

        DropShadow outerFocus = new DropShadow();
        outerFocus.setColor(Color.rgb(104, 155, 201));
        outerFocus.setBlurType(BlurType.ONE_PASS_BOX);
        outerFocus.setRadius(7.0);
        outerFocus.setSpread(0.7);
        outerFocus.setOffsetX(0.0);
        outerFocus.setOffsetY(0.0);
        outerFocus.setInput(innerFocus);

        for (Node child : getChildren()) {
            if (child instanceof StackPane) {
                child.setEffect(outerFocus);
            }
        }
    }

    private void setFocusBorder() {
        InnerShadow innerFocus = new InnerShadow();
        innerFocus.setColor(Color.rgb(104, 155, 201));
        innerFocus.setBlurType(BlurType.ONE_PASS_BOX);
        innerFocus.setRadius(6.5);
        innerFocus.setChoke(0.7);
        innerFocus.setOffsetX(0.0);
        innerFocus.setOffsetY(0.0);

        DropShadow outerFocus = new DropShadow();
        outerFocus.setColor(Color.rgb(104, 155, 201));
        outerFocus.setBlurType(BlurType.ONE_PASS_BOX);
        outerFocus.setRadius(5.0);
        outerFocus.setSpread(0.6);
        outerFocus.setOffsetX(0.0);
        outerFocus.setOffsetY(0.0);
        outerFocus.setInput(innerFocus);

        for (Node child : getChildren()) {
            if (child instanceof StackPane) {
                child.setEffect(outerFocus);
            }
        }
    }

    @Override protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if (p == "SELECTED") {
            if (getSkinnable().isFocused()) {
                if (getSkinnable().isSelected()) {
                    setSelectedFocusBorder();
                } else {
                    setFocusBorder();
                }
            }
        }
        if (p == "FOCUSED") {
            if (getSkinnable().isFocused()) {
                if (getSkinnable().isSelected()) {
                    setSelectedFocusBorder();
                } else {
                    setFocusBorder();
                }
            } else if (!getSkinnable().isFocused()) {
                setDropShadow();
            }
        }
    }
}
