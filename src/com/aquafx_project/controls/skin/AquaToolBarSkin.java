package com.aquafx_project.controls.skin;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;

import com.sun.javafx.scene.control.skin.ToolBarSkin;

public class AquaToolBarSkin extends ToolBarSkin implements AquaSkin{

    public AquaToolBarSkin(ToolBar toolbar) {
        super(toolbar);
       
        addInactiveState();
    }

     private void addInactiveState() {
        final ChangeListener<Boolean> windowFocusChangedListener = new ChangeListener<Boolean>() {

            @Override public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (newValue != null) {
                    if (newValue.booleanValue()) {
                        getSkinnable().getStyleClass().remove("inactive");
                    } else {
                        getSkinnable().getStyleClass().add("inactive");
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
}
