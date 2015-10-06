/*
 * JFoenix
 * Copyright (c) 2015, JFoenix and/or its affiliates., All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package com.jfoenix.validation.base;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.Parent;

public abstract class ValidatorBase extends Parent {
	public static final String DEFAULT_ERROR_STYLE_CLASS = "error";

	public ValidatorBase(){
		parentProperty().addListener((o,oldVal,newVal)->parentChanged());
	}

	/***************************************************************************
	 *                                                                         *
	 * Methods                                                                 *
	 *                                                                         *
	 **************************************************************************/

	private void parentChanged() {
		updateSrcControl();
	}

	private void updateSrcControl(){
		Parent parent = getParent();
		if (parent != null) {
			Node control = parent.lookup(getSrc());
			srcControl.set(control);
		}
	}

	protected abstract void eval();

	public void validate(){
		eval();
		onEval();
	}

	protected void onEval(){
		Node control = getSrcControl();
		if (hasErrors.get()) {
			if (!control.getStyleClass().contains(errorStyleClass.get()))
				control.getStyleClass().add(errorStyleClass.get());
		} else{
			control.getStyleClass().remove(errorStyleClass.get());
		}
	}

	/***************************************************************************
	 *                                                                         *
	 * Properties                                                              *
	 *                                                                         *
	 **************************************************************************/

	/***** srcControl *****/
	protected SimpleObjectProperty<Node> srcControl = new SimpleObjectProperty<>();

	public void setSrcControl(Node srcControl){
		this.srcControl.set(srcControl);
	}

	public Node getSrcControl(){
		return this.srcControl.get();
	}

	public ObjectProperty<Node> srcControlProperty(){
		return this.srcControl;
	}


	/***** src *****/
	protected SimpleStringProperty src = new SimpleStringProperty(){
		@Override
		protected void invalidated() {
			updateSrcControl();
		}
	};

	public void setSrc(String src){
		this.src.set(src);
	}

	public String getSrc(){
		return this.src.get();
	}

	public StringProperty srcProperty(){
		return this.src;
	}


	/***** hasErrors *****/
	protected ReadOnlyBooleanWrapper hasErrors = new ReadOnlyBooleanWrapper(false);

	public boolean getHasErrors(){
		return hasErrors.get();
	}

	public ReadOnlyBooleanProperty hasErrorsProperty(){
		return hasErrors.getReadOnlyProperty();
	}

	/***** Message *****/
	protected SimpleStringProperty message = new SimpleStringProperty(){
		@Override
		protected void invalidated() {
			updateSrcControl();
		}
	};

	public void setMessage(String msg){
		this.message.set(msg);
	}

	public String getMessage(){
		return this.message.get();
	}

	public StringProperty messageProperty(){
		return this.message;
	}

	/***** Awsome Icon *****/
	protected SimpleObjectProperty<Node> awsomeIcon = new SimpleObjectProperty<Node>(){
		@Override
		protected void invalidated() {
			updateSrcControl();
		}
	};
	public void setAwsomeIcon(Node icon){
		icon.setStyle("-fx-font-family: FontAwesome;");
		icon.getStyleClass().add("errorIcon");
		this.awsomeIcon.set(icon);
	}
	public Node getAwsomeIcon(){
		return this.awsomeIcon.get();
	}
	public SimpleObjectProperty<Node> awsomeIconProperty(){
		return this.awsomeIcon;
	}

	
	
	/***** error style class *****/
	protected SimpleStringProperty errorStyleClass = new SimpleStringProperty(DEFAULT_ERROR_STYLE_CLASS);

	public void setErrorStyleClass(String styleClass){
		this.errorStyleClass.set(styleClass);
	}

	public String getErrorStyleClass(){
		return this.errorStyleClass.get();
	}

	public StringProperty errorStyleClassProperty(){
		return this.errorStyleClass;
	}

}
