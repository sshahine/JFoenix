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
package com.jfoenix.validation;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.validation.base.ValidatorBase;
import javafx.beans.DefaultProperty;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;

/**
 * An example of required field validtaion, that is applied on text input controls
 * such as {@link TextField} and {@link TextArea}
 * 
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2016-03-09
 */
@DefaultProperty(value="icon")
public class RequiredFieldValidator extends ValidatorBase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void eval() {
		if(srcControl.get() instanceof TextInputControl)
			evalTextInputField();
		if(srcControl.get() instanceof JFXComboBox<?>)
			evalComboBoxField();
	}
	
	private void evalTextInputField(){
		TextInputControl textField = (TextInputControl) srcControl.get();
		if (textField.getText() == null || textField.getText().equals("")) hasErrors.set(true);
		else hasErrors.set(false);
	}
	
	private void evalComboBoxField(){
		JFXComboBox<?> comboField = (JFXComboBox<?>) srcControl.get();
		boolean valid = comboField.getValue()!=null;
		valid |= comboField.isEditable() && comboField.getEditor().getText()!=null && !comboField.getEditor().getText().isEmpty();
		if (valid ) hasErrors.set(false);
		else hasErrors.set(true);
	}
}
