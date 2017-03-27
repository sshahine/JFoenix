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
 * Node that is used to show svg images
 * 
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2016-03-09
 */
public class SVGGlyph extends Pane {
	private static final String DEFAULT_STYLE_CLASS = "jfx-svg-glyph";

	private final int glyphId;
	private final String name;	
	private static final int DEFAULT_PREF_SIZE = 64;

	private ObjectProperty<Paint> fill = new SimpleObjectProperty<>();

	/**
	 * Constructs SVGGlyph node for a specified svg content and color
	 * <b>Note:</b> name and glyphId is not needed when creating a single SVG image,
	 * they have been used in {@link SVGGlyphLoader} to load icomoon svg font.
	 * 
	 * @param glyphId integer represents the glyph id
	 * @param name glyph name 
	 * @param svgPathContent svg content
	 * @param fill svg color
	 */
	public SVGGlyph(int glyphId, String name, String svgPathContent, Paint fill) {
		this.glyphId = glyphId;
		this.name = name;
		getStyleClass().add(DEFAULT_STYLE_CLASS);
		this.fill.addListener((observable, oldValue, newValue) -> setBackground(new Background(new BackgroundFill(newValue, null, null))));

		SVGPath shape = new SVGPath();
		shape.setContent(svgPathContent);
		setShape(shape);
		setFill(fill);
		
		setPrefSize(DEFAULT_PREF_SIZE, DEFAULT_PREF_SIZE);
	}

	/**
	 * @return current svg id
	 */
	public int getGlyphId() {
		return glyphId;
	}

	/**
	 * @return current svg name
	 */
	public String getName() {
		return name;
	}

	/**
	 * svg color property
	 */
	public void setFill(Paint fill) { this.fill.setValue(fill); }
	public ObjectProperty<Paint> fillProperty() { return fill; }
	public Paint getFill() { return fill.getValue(); }
	
	/**
	 * resize the svg to a certain width and height
	 * @param width
	 * @param height
	 */
	public void setSize(double width, double height){
		this.setMinSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);
		this.setPrefSize(width, height);		
		this.setMaxSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);
	}
}