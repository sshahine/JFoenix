/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.jfoenix.controls;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;

import com.jfoenix.skins.JFXColorPickerSkin;

/**
 * @author Shadi Shaheen
 * 
 */
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
