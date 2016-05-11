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
 * JFXRadioButton is the material design implementation of a radio button. 
 * 
 * @author  Bashir Elias & Shadi Shaheen
 * @version 1.0
 * @since   2016-03-09
 */
public class JFXRadioButton extends RadioButton {

	/**
	 * {@inheritDoc}
	 */
	public JFXRadioButton(String text) {
		super(text);
		initialize();
	}

	/**
	 * {@inheritDoc}
	 */
	public JFXRadioButton() {
		super();
		initialize();
		// init in scene builder workaround ( TODO : remove when JFoenix is well integrated in scenebuilder by gluon )		
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		for(int i = 0 ; i < stackTraceElements.length && i < 15; i++){
			if(stackTraceElements[i].getClassName().toLowerCase().contains(".scenebuilder.kit.fxom.")){
				this.setText("RadioButton");
				break;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Skin<?> createDefaultSkin() {
		return new JFXRadioButtonSkin(this);
	}

	private void initialize() {
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);    	
	}

	/**
	 * Initialize the style class to 'jfx-radio-button'.
	 *
	 * This is the selector class from which CSS can be used to style
	 * this control.
	 */
	private static final String DEFAULT_STYLE_CLASS = "jfx-radio-button";
}
