package com.cctintl.c3dfx.svg;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

public class SVGGlyph extends Pane {
	private final int glyphId;
	private final String name;

	private static final int DEFAULT_PREF_SIZE = 64;
	private static final String DEFAULT_STYLE_CLASS = "c3d-svg-glyph";

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