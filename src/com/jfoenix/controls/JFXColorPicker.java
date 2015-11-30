package com.jfoenix.controls;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;

import com.jfoenix.skins.JFXColorPickerSkin;

public class JFXColorPicker extends ColorPicker {
	
	private static final String DEFAULT_STYLE_CLASS = "jfx-color-picker";
	
    public JFXColorPicker() {
        super();
        initialize();
    }
    
    public JFXColorPicker(Color color) {
    	super(color);
    	initialize();
    }
	
    @Override protected Skin<?> createDefaultSkin() {
        return new JFXColorPickerSkin(this);
    }
    
    private void initialize() {
    	this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    }
    
}
