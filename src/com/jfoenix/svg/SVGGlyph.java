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
package com.jfoenix.svg;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

/**
 * @author Shadi Shaheen
 *
 */
public class SVGGlyph extends Pane {
	private final int glyphId;
	private final String name;

	private static final int DEFAULT_PREF_SIZE = 64;
	private static final String DEFAULT_STYLE_CLASS = "jfx-svg-glyph";

	private ObjectProperty<Paint> fill = new SimpleObjectProperty<>();

	public SVGGlyph(int glyphId, String name, String svgPathContent, Paint fill) {
		this.glyphId = glyphId;
		this.name = name;

		SVGPath shape = new SVGPath();
		shape.setContent(svgPathContent);

		getStyleClass().add(DEFAULT_STYLE_CLASS);
		
		this.fill.addListener((observable, oldValue, newValue) -> setBackground(new Background(new BackgroundFill(newValue, null, null))));
		setShape(shape);
		setFill(fill);
		
		setPrefSize(DEFAULT_PREF_SIZE, DEFAULT_PREF_SIZE);
	}

	public int getGlyphId() {
		return glyphId;
	}

	public String getName() {
		return name;
	}

	public void setFill(Paint fill) {
		this.fill.setValue(fill);
	}

	public ObjectProperty<Paint> fillProperty() {
		return fill;
	}

	public Paint getFill() {
		return fill.getValue();
	}
	
	public void setSize(double width, double height){
		this.setMinSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);
		this.setPrefSize(width, height);		
		this.setMaxSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);
	}
}