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
package com.jfoenix.skins;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialog.DialogTransition;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.behavior.JFXDatePickerBehavior;
import com.jfoenix.svg.SVGGlyph;
import com.sun.javafx.scene.control.skin.ComboBoxPopupControl;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

/**
 * <h1>Material Design Date Picker Skin</h1>
 *
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2016-03-09
 */
public class JFXDatePickerSkin extends ComboBoxPopupControl<LocalDate> {

	/**
	 * TODO:
	 * 1. Handle different Chronology  
	 */
    private JFXDatePicker jfxDatePicker;
    private JFXTextField editorNode;
    
    // displayNode is the same as editorNode
    private TextField displayNode;
    private JFXDatePickerContent jfxDatePickerContent;
    private JFXTimePickerContent timeContent;
    
    private JFXDialog dialog;

    public JFXDatePickerSkin(final JFXDatePicker datePicker) {
        super(datePicker, new JFXDatePickerBehavior(datePicker));        
        this.jfxDatePicker = datePicker;       
        editorNode = new JFXTextField();
        editorNode.focusColorProperty().bind(datePicker.defaultColorProperty());
        
        // create calender or clock button 
        if(!((JFXDatePicker)getSkinnable()).isShowTime()) arrow = new SVGGlyph(0, "calendar", "M320 384h128v128h-128zM512 384h128v128h-128zM704 384h128v128h-128zM128 768h128v128h-128zM320 768h128v128h-128zM512 768h128v128h-128zM320 576h128v128h-128zM512 576h128v128h-128zM704 576h128v128h-128zM128 576h128v128h-128zM832 0v64h-128v-64h-448v64h-128v-64h-128v1024h960v-1024h-128zM896 960h-832v-704h832v704z", Color.BLACK);            
        else arrow = new SVGGlyph(0, "clock", "M512 310.857v256q0 8-5.143 13.143t-13.143 5.143h-182.857q-8 0-13.143-5.143t-5.143-13.143v-36.571q0-8 5.143-13.143t13.143-5.143h128v-201.143q0-8 5.143-13.143t13.143-5.143h36.571q8 0 13.143 5.143t5.143 13.143zM749.714 512q0-84.571-41.714-156t-113.143-113.143-156-41.714-156 41.714-113.143 113.143-41.714 156 41.714 156 113.143 113.143 156 41.714 156-41.714 113.143-113.143 41.714-156zM877.714 512q0 119.429-58.857 220.286t-159.714 159.714-220.286 58.857-220.286-58.857-159.714-159.714-58.857-220.286 58.857-220.286 159.714-159.714 220.286-58.857 220.286 58.857 159.714 159.714 58.857 220.286z", Color.BLACK);
        
        ((SVGGlyph)arrow).fillProperty().bind(jfxDatePicker.defaultColorProperty());
        ((SVGGlyph)arrow).setSize(20, 20);
        arrowButton.getChildren().setAll(arrow);
		arrowButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
		
		//dialog = new JFXDialog(null, content, transitionType, overlayClose)
		
        registerChangeListener(datePicker.converterProperty(), "CONVERTER");
        registerChangeListener(datePicker.dayCellFactoryProperty(), "DAY_CELL_FACTORY");
        registerChangeListener(datePicker.showWeekNumbersProperty(), "SHOW_WEEK_NUMBERS");
        registerChangeListener(datePicker.valueProperty(), "VALUE");
        registerChangeListener(datePicker.timeProperty(), "TIME");
        
    }

    @Override public Node getPopupContent() {
    	if(jfxDatePicker.isShowTime()){
    		if (timeContent == null) {
                timeContent = new JFXTimePickerContent(jfxDatePicker);
            }
            return timeContent;	
    	}
    	else{
    		if (jfxDatePickerContent == null) {
            	// different chronologies are not supported yet
                jfxDatePickerContent = new JFXDatePickerContent(jfxDatePicker);
            }
            return jfxDatePickerContent;
    	}
    }

    @Override public void show() {
    	if(!((JFXDatePicker)getSkinnable()).isOverLay()) super.show();
        if(jfxDatePickerContent!=null){
        	jfxDatePickerContent.init();
        	jfxDatePickerContent.clearFocus();
        }
        
        if(((JFXDatePicker)getSkinnable()).isOverLay()){
			if(dialog == null){
				StackPane dialogParent = jfxDatePicker.getDialogParent();
				if(dialogParent == null	) dialogParent = (StackPane) getSkinnable().getScene().getRoot();
				dialog = new JFXDialog(dialogParent, (Region) getPopupContent(), DialogTransition.CENTER, true);
				arrowButton.setOnMouseClicked((click)->{
					if(((JFXDatePicker)getSkinnable()).isOverLay()){
						StackPane parent = jfxDatePicker.getDialogParent();
						if(parent == null ) parent = (StackPane) getSkinnable().getScene().getRoot();
						dialog.show(parent);		
					}
				});	
			}
		}
    }

    @Override protected void handleControlPropertyChanged(String p) {
        if ("DAY_CELL_FACTORY".equals(p)) {
            updateDisplayNode();
            jfxDatePickerContent = null;
            popup = null;
        } else if ("CONVERTER".equals(p)) {
            updateDisplayNode();
        } else if ("EDITOR".equals(p)) {
            getEditableInputNode();
        } else if ("SHOWING".equals(p)) {
            if (jfxDatePicker.isShowing()) {
                if (jfxDatePickerContent != null) {
                    LocalDate date = jfxDatePicker.getValue();
                    // set the current date / now when showing the date picker content
                    jfxDatePickerContent.displayedYearMonthProperty().set((date != null) ? YearMonth.from(date) : YearMonth.now());
                    jfxDatePickerContent.updateValues();
                }
                show();
            } else {
                hide();
            }
        } else if ("SHOW_WEEK_NUMBERS".equals(p)) {
            if (jfxDatePickerContent != null) {
            	// update the content grid to show week numbers
                jfxDatePickerContent.updateContentGrid();
                jfxDatePickerContent.updateWeekNumberDateCells();
            }
        } else if ("VALUE".equals(p)) {
            updateDisplayNode();
            if (jfxDatePickerContent != null) {
                LocalDate date = jfxDatePicker.getValue();
                jfxDatePickerContent.displayedYearMonthProperty().set((date != null) ? YearMonth.from(date) : YearMonth.now());
                jfxDatePickerContent.updateValues();
            }
            jfxDatePicker.fireEvent(new ActionEvent());
        } else if("TIME".equals(p)){
        	updateTimeDisplayNode();
        } else {
            super.handleControlPropertyChanged(p);
        }
    }

    private void updateTimeDisplayNode(){
    	if(jfxDatePicker.isShowTime() && jfxDatePicker.getTime()!=null){
	    	DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
	    	displayNode.setText(fmt.format(jfxDatePicker.getTime()));
    	}
    }
    
    // these methods are called from the super constructor
    @Override protected TextField getEditor() {
    	StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
    	/*
    	 *  added to fix android issue as the stack trace on adroid is 
    	 *  not the same as desktop  
    	 */
    	if(caller.getClassName().equals(this.getClass().getName()))
    		caller = Thread.currentThread().getStackTrace()[3];
		boolean parentListenerCall = caller.getMethodName().contains("lambda") && caller.getClassName().equals(this.getClass().getSuperclass().getName());
		if(parentListenerCall) return null;
        return editorNode;
    }

    @Override protected StringConverter<LocalDate> getConverter() {
        return ((DatePicker)getSkinnable()).getConverter();
    }

    @Override public Node getDisplayNode() {
        if (displayNode == null) {
            displayNode = getEditableInputNode();
            displayNode.getStyleClass().add("date-picker-display-node");
            updateDisplayNode();
            updateTimeDisplayNode();
        }
        displayNode.setEditable(jfxDatePicker.isEditable());
        return displayNode;
    }

    /*
     * this method is called from the behavior class to make sure
     * DatePicker button is in sync after the popup is being dismissed 
     */
    public void syncWithAutoUpdate() {
        if (!getPopup().isShowing() && jfxDatePicker.isShowing()) jfxDatePicker.hide();
    }
}

