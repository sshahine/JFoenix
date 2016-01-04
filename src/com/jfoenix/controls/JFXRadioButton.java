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

import javafx.scene.control.RadioButton;
import javafx.scene.control.Skin;

import com.jfoenix.skins.JFXRadioButtonSkin;

/**
 * @author Bashir Elias & Shadi Shaheen
 *
 */
public class JFXRadioButton extends RadioButton {

	private static final String DEFAULT_STYLE_CLASS = "jfx-radio-button";

	public JFXRadioButton(String text) {
		super(text);
		initialize();
	}

	public JFXRadioButton() {
		super();
		initialize();
	}

	private void initialize() {
    	this.getStyleClass().add(DEFAULT_STYLE_CLASS);    	
//    	if(this.getText().isEmpty()) this.setText("RadioButton");
    }
	
	@Override
	protected Skin<?> createDefaultSkin() {
		return new JFXRadioButtonSkin(this);
	}

}
