/*
 * Copyright (c) 2015, JFoenix and/or its affiliates. All rights reserved.
 * JFoenix PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.jfoenix.validation;

import javafx.beans.DefaultProperty;
import javafx.scene.control.TextInputControl;

import com.jfoenix.validation.base.ValidatorBase;

@DefaultProperty(value="awsomeIcon")
public class RequiredFieldValidator extends ValidatorBase {

	@Override
	protected void eval() {
		if(srcControl.get() instanceof TextInputControl)
			evalTextInputField();
	}
	
	protected void evalTextInputField(){
		TextInputControl textField = (TextInputControl) srcControl.get();
		if (textField.getText() == null || textField.getText().equals(""))
			hasErrors.set(true);
		else
			hasErrors.set(false);
	}
}
