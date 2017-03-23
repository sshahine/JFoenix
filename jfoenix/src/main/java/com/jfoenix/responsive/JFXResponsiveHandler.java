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
package com.jfoenix.responsive;

import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Responsive handler will scan all nodes in the scene and add a certain 
 * pseudo class (style class) to them according to the device ( screen size )
 * 
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2016-03-09
 */
public class JFXResponsiveHandler {
	
	public static final PseudoClass PSEUDO_CLASS_EX_SMALL = PseudoClass.getPseudoClass("extreme-small-device");
	public static final PseudoClass PSEUDO_CLASS_SMALL = PseudoClass.getPseudoClass("small-device");
	public static final PseudoClass PSEUDO_CLASS_MEDIUM = PseudoClass.getPseudoClass("medium-device");
	public static final PseudoClass PSEUDO_CLASS_LARGE = PseudoClass.getPseudoClass("large-device");
	
	/**
	 * Construct a responsive handler for a specified Stage and css class.
	 * <p>
	 * Device css classes can be one of the following:
	 * <ul>
	 * 	<li>{@link JFXResponsiveHandler#PSEUDO_CLASS_EX_SMALL}</li>
	 * 	<li>{@link JFXResponsiveHandler#PSEUDO_CLASS_LARGE}</li>
	 * 	<li>{@link JFXResponsiveHandler#PSEUDO_CLASS_MEDIUM}</li>
	 * 	<li>{@link JFXResponsiveHandler#PSEUDO_CLASS_SMALL}</li>
	 * </ul>
	 * 
	 * <b>Note:</b> the css class must be chosen by the user according to a device
	 * detection methodology
	 * 
	 * @param stage the JavaFX Application stage
	 * @param pseudoClass css class for certain device
	 */
	public JFXResponsiveHandler(Stage stage, PseudoClass pseudoClass) {
		scanAllNodes(stage.getScene().getRoot(), PSEUDO_CLASS_LARGE);		
	}
	
	/**
	 * scans all nodes in the scene and apply the css pseduoClass to them.
	 * 
	 * @param parent stage parent node
	 * @param pseudoClass css class for certain device
	 */
	private void scanAllNodes(Parent parent, PseudoClass pseudoClass){		
		parent.getChildrenUnmodifiable().addListener(new ListChangeListener<Node>(){
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> c) {
				while (c.next())
					if (!c.wasPermutated() && !c.wasUpdated()) 
	                     for (Node addedNode : c.getAddedSubList()) 
	                    	 if(addedNode instanceof Parent)
	                    		 scanAllNodes((Parent) addedNode,pseudoClass);
			}
    	});		
		for (Node component : parent.getChildrenUnmodifiable()) {
	        if (component instanceof Pane) {
	        	((Pane)component).getChildren().addListener(new ListChangeListener<Node>(){
					@Override
					public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> c) {
						while (c.next()) {
							if (!c.wasPermutated() && !c.wasUpdated()) {
			                     for (Node addedNode : c.getAddedSubList()) {
			                    	 if(addedNode instanceof Parent)
			                    		 scanAllNodes((Parent) addedNode,pseudoClass);
			                     }
			                 }
						}
					}
	        	});
	            //if the component is a container, scan its children
	        	scanAllNodes((Pane) component, pseudoClass);
	        } else if (component instanceof ScrollPane){
	        	((ScrollPane)component).contentProperty().addListener((o,oldVal,newVal)-> {
	        		scanAllNodes((Parent) newVal,pseudoClass);	
	        	});
	            //if the component is a container, scan its children
	        	if(((ScrollPane)component).getContent() instanceof Parent){
	        		
	        		scanAllNodes((Parent) ((ScrollPane)component).getContent(), pseudoClass);
	        	}
	        } else if (component instanceof Control) {
	            //if the component is an instance of IInputControl, add to list	        	
	        	((Control)component).pseudoClassStateChanged(PSEUDO_CLASS_EX_SMALL, pseudoClass == PSEUDO_CLASS_EX_SMALL);
	        	((Control)component).pseudoClassStateChanged(PSEUDO_CLASS_SMALL, pseudoClass == PSEUDO_CLASS_SMALL);
	        	((Control)component).pseudoClassStateChanged(PSEUDO_CLASS_MEDIUM, pseudoClass == PSEUDO_CLASS_MEDIUM);
	        	((Control)component).pseudoClassStateChanged(PSEUDO_CLASS_LARGE, pseudoClass == PSEUDO_CLASS_LARGE);
	        }
	    }
	}
	
	
}
