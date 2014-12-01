package com.cctintl.c3dfx.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.scene.control.Control;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import com.cctintl.c3dfx.skins.C3DProgressBarSkin;
import com.sun.javafx.css.converters.PaintConverter;
import com.sun.javafx.css.converters.SizeConverter;

public class C3DProgressBar extends ProgressBar {

	public C3DProgressBar() {
		super();
		initialize();
	}
	
	public C3DProgressBar(double progress) {
		super(progress);
		initialize();
	}
	
	private void initialize() {
		this.getStyleClass().add("c3d-progress-bar");        
	}
	
	@Override
	protected Skin<?> createDefaultSkin()	{
		return new C3DProgressBarSkin(this);		
	}
	

	/**
	 *  styleable properties 
	 */
	private StyleableObjectProperty<Paint> trackColor = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.TRACK_COLOR, C3DProgressBar.this, "trackColor", Color.valueOf("#C8C8C8"));

	public Paint getTrackColor(){
		return trackColor == null ? Color.valueOf("#C8C8C8") : trackColor.get();
	}
	public StyleableObjectProperty<Paint> trackColorProperty(){		
		return this.trackColor;
	}
	public void setTrackColor(Paint color){
		this.trackColor.set(color);
	}


	private StyleableObjectProperty<Paint> progressColor = new SimpleStyleableObjectProperty<Paint>(StyleableProperties.PROGRESS_COLOR, C3DProgressBar.this, "progressColor", Color.valueOf("#0F9D58"));

	public Paint getProgressColor(){
		return progressColor == null ? Color.valueOf("#0F9D58") : progressColor.get();
	}
	public StyleableObjectProperty<Paint> progressColorProperty(){		
		return this.progressColor;
	}
	public void setProgressColor(Paint color){
		this.progressColor.set(color);
	}

	private StyleableObjectProperty<Number> strokeWidth = new SimpleStyleableObjectProperty<Number>(StyleableProperties.BAR_STROKE_WIDTH, C3DProgressBar.this, "strokeWidth", 4);

	public Number getStrokeWidth(){
		return strokeWidth == null ? 4 : strokeWidth.get();
	}
	public StyleableObjectProperty<Number> strokeWidthProperty(){		
		return this.strokeWidth;
	}
	public void setStrokeWidth(Number width){
		this.strokeWidth.set(width);
	}
	

	private static class StyleableProperties {
		private static final CssMetaData< C3DProgressBar, Paint> TRACK_COLOR =
				new CssMetaData< C3DProgressBar, Paint>("-fx-track-color",
						PaintConverter.getInstance(), Color.valueOf("#C8C8C8")) {
			@Override
			public boolean isSettable(C3DProgressBar control) {
				return control.trackColor == null || !control.trackColor.isBound();
			}
			@Override
			public StyleableProperty<Paint> getStyleableProperty(C3DProgressBar control) {
				return control.trackColorProperty();
			}
		};
		private static final CssMetaData< C3DProgressBar, Paint> PROGRESS_COLOR =
				new CssMetaData< C3DProgressBar, Paint>("-fx-progress-color",
						PaintConverter.getInstance(),  Color.valueOf("#0F9D58")) {
			@Override
			public boolean isSettable(C3DProgressBar control) {
				return control.progressColor == null || !control.progressColor.isBound();
			}
			@Override
			public StyleableProperty<Paint> getStyleableProperty(C3DProgressBar control) {
				return control.progressColorProperty();
			}
		};
		private static final CssMetaData< C3DProgressBar, Number> BAR_STROKE_WIDTH =
				new CssMetaData< C3DProgressBar, Number>("-fx-stroke-width",
						SizeConverter.getInstance(),  4) {
			@Override
			public boolean isSettable(C3DProgressBar control) {
				return control.strokeWidth == null || !control.strokeWidth.isBound();
			}
			@Override
			public StyleableProperty<Number> getStyleableProperty(C3DProgressBar control) {
				return control.strokeWidthProperty();
			}
		};
		
		private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			Collections.addAll(styleables,
					TRACK_COLOR,
					PROGRESS_COLOR,
					BAR_STROKE_WIDTH
					);
			CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
		}
	}

	// inherit the styleable properties from parent
	private List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		if(STYLEABLES == null){
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
			styleables.addAll(getClassCssMetaData());
			styleables.addAll(super.getClassCssMetaData());
			STYLEABLES = Collections.unmodifiableList(styleables);
		}
		return STYLEABLES;
	}
	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.CHILD_STYLEABLES;
	}



	
	
}
