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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
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
 * @author sshahine
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