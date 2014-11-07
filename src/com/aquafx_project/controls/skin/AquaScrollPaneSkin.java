package com.aquafx_project.controls.skin;

import javafx.scene.control.ScrollPane;

import com.sun.javafx.scene.control.skin.ScrollPaneSkin;

public class AquaScrollPaneSkin extends ScrollPaneSkin implements AquaSkin{

    public AquaScrollPaneSkin(ScrollPane scrollpane) {
        super(scrollpane);
    }
    
    @Override protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);
        
//        hsb.relocate(hsb.getLayoutX(), hsb.getLayoutY() - hsb.getHeight());
//        hsb.toFront();
    }
}
