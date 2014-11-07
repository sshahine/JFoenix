package com.aquafx_project.controls.skin;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

import com.aquafx_project.controls.skin.effects.FocusBorder;
import com.aquafx_project.controls.skin.effects.Shadow;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;


public class AquaComboBoxListViewSkin<T> extends ComboBoxListViewSkin<T> implements AquaSkin {

    public AquaComboBoxListViewSkin(ComboBox<T> comboBox) {
        super(comboBox);
        registerChangeListener(comboBox.disabledProperty(), "DISABLED");

        if (getSkinnable().isFocused()) {
            setFocusBorder();
        } else if(!getSkinnable().isFocused() && !getSkinnable().isDisabled()) {
            setDropShadow();
        } 
        
        for (Object child : getChildren()) {
            ((Node) child).focusedProperty().addListener(focusListener);
        }
        if (comboBox.isEditable()) {
            getDisplayNode().focusedProperty().addListener(focusListener);
        }
        getSkinnable().focusedProperty().addListener(focusListener);
    }

    private ChangeListener<Boolean> focusListener = new ChangeListener<Boolean>() {

        @Override public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue) {
                setFocusBorder();
            } else {
                setDropShadow();
            }
        }
    };

    private void setFocusBorder() {
        getSkinnable().setEffect(new FocusBorder());
        getListView().setEffect(null);
    }

    private void setDropShadow() {
        getSkinnable().setEffect(new Shadow(false));
    }
    
    @Override protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if (p == "DISABLED") {
            if (getSkinnable().isDisabled()) {
                getSkinnable().setEffect(null);
            } else {
                setDropShadow();
            }
        }
    }
}
