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
package com.jfoenix.controls;

import javafx.animation.FadeTransition;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

@DefaultProperty(value = "control")
public class JFXBadge extends StackPane {

	private Group badge;
	protected Node control;
	private boolean enabled = true;

	public JFXBadge() {
		this(null);
	}

	public JFXBadge(Node control) {
		this(control, Pos.TOP_RIGHT);
	}

	public JFXBadge(Node control, Pos pos) {
		super();
		initialize();
		setPosition(pos);
		setControl(control);
		position.addListener((o,oldVal,newVal)-> StackPane.setAlignment(badge, newVal));
	}

	/***************************************************************************
	 * * Setters / Getters * *
	 **************************************************************************/

	public void setControl(Node control) {
		if (control != null) {
			this.control = control;
			this.badge = new Group();
			this.getChildren().add(control);
			this.getChildren().add(badge);

			// if the control got resized the badge must be rest
			if (control instanceof Region) {
				((Region) control).widthProperty().addListener((o, oldVal, newVal) -> refreshBadge());
				((Region) control).heightProperty().addListener((o, oldVal, newVal) -> refreshBadge());
			}			
			text.addListener((o, oldVal, newVal) -> refreshBadge());
		}
	}

	public Node getControl() {
		return this.control;
	}

	public void setEnabled(boolean enable) {
		this.enabled = enable;
	}

	public void refreshBadge() {
		badge.getChildren().clear();
		if (enabled) {

//			final double scaledWidth = control.getLayoutBounds().getWidth() / getBadgeScale().doubleValue();
//			final double scaledHeight = control.getLayoutBounds().getHeight() / getBadgeScale().doubleValue();

//			Shape background = new Rectangle(scaledWidth, scaledHeight);
//			Shape clip = new Rectangle(scaledWidth, scaledHeight);
//
//			if (maskType.get().equals(JFXBadge.BadgeMask.CIRCLE)) {
//				double radius = Math.min(scaledWidth / 2, scaledHeight / 2);
//				background = new Circle(radius);
//				clip = new Circle(radius);
//			}
//			
//
//			if (badgeFill.get() instanceof Color) {
//				Color circleColor = new Color(((Color) badgeFill.get()).getRed(), ((Color) badgeFill.get()).getGreen(),
//						((Color) badgeFill.get()).getBlue(), ((Color) badgeFill.get()).getOpacity());
//				background.setStroke(circleColor);
//				background.setFill(circleColor);
//			} else {
//				background.setStroke(badgeFill.get());
//				background.setFill(badgeFill.get());
//			}

			Label labelControl = new Label(text.getValue());
			
			StackPane badgePane = new StackPane();			
//			badgePane.getChildren().add(background);
			badgePane.getStyleClass().add("badge-pane");
			badgePane.getChildren().add(labelControl);
			//Adding a clip would avoid overlap but this does not work as intended
			//badgePane.setClip(clip); 
			badge.getChildren().add(badgePane);
			StackPane.setAlignment(badge, getPosition());

			FadeTransition ft = new FadeTransition(Duration.millis(666), badge);
			ft.setFromValue(0);
			ft.setToValue(1.0);
			ft.setCycleCount(1);
			ft.setAutoReverse(true);
			ft.play();

		}
	}

	/***************************************************************************
	 * * Stylesheet Handling * *
	 **************************************************************************/

	private static final String DEFAULT_STYLE_CLASS = "jfx-badge";

	private void initialize() {
		this.getStyleClass().add(DEFAULT_STYLE_CLASS);
	}

//	private StyleableObjectProperty<Paint> badgeFill = new SimpleStyleableObjectProperty<Paint>(
//			StyleableProperties.BADGE_FILL, JFXBadge.this, "badgeFill", Color.rgb(0, 200, 255));
//
//	public Paint getBadgeFill() {
//		return badgeFill == null ? Color.rgb(0, 200, 255) : badgeFill.get();
//	}
//
//	public StyleableObjectProperty<Paint> badgeFillProperty() {
//		return this.badgeFill;
//	}
//
//	public void setBadgeFill(Paint color) {
//		this.badgeFill.set(color);
//	}
//
//	private StyleableObjectProperty<Number> badgeScale = new SimpleStyleableObjectProperty<Number>(
//			StyleableProperties.BADGE_SCALE, JFXBadge.this, "badgeScale", 3d);
//
//	public Number getBadgeScale() {
//		return badgeScale == null ? 3d : badgeScale.get();
//	}
//
//	public StyleableObjectProperty<Number> badgeScaleProperty() {
//		return this.badgeScale;
//	}
//
//	public void setBadgeScale(Double scale) {
//		this.badgeScale.set(scale);
//	}
//
	protected ObjectProperty<Pos> position = new SimpleObjectProperty<Pos>();

	public Pos getPosition() {
		return position == null ? Pos.TOP_RIGHT : position.get();
	}

	public ObjectProperty<Pos> positionProperty() {
		return this.position;
	}
	
	public void setPosition(Pos position){
		this.position.set(position);
	}

	private SimpleStringProperty text = new SimpleStringProperty();

	public final String getText() {
		return text.get();
	}

	public final void setText(String value) {
		text.set(value);
	}

	public final StringProperty textProperty() {
		return text;
	}

//	private static class StyleableProperties {
//		private static final CssMetaData<JFXBadge, Paint> BADGE_FILL = new CssMetaData<JFXBadge, Paint>(
//				"-fx-badge-fill", PaintConverter.getInstance(), Color.rgb(0, 200, 255)) {
//			@Override
//			public boolean isSettable(JFXBadge control) {
//				return control.badgeFill == null || !control.badgeFill.isBound();
//			}
//
//			@Override
//			public StyleableProperty<Paint> getStyleableProperty(JFXBadge control) {
//				return control.badgeFillProperty();
//			}
//		};
//
//		private static final CssMetaData<JFXBadge, Number> BADGE_SCALE = new CssMetaData<JFXBadge, Number>(
//				"-fx-badge-scale", StyleConverter.getSizeConverter(), 3d) {
//			@Override
//			public boolean isSettable(JFXBadge control) {
//				return control.badgeScale == null || !control.badgeScale.isBound();
//			}
//
//			@Override
//			public StyleableProperty<Number> getStyleableProperty(JFXBadge control) {
//				return (StyleableProperty<Number>) control.badgeScaleProperty();
//			}
//		};
//
//
//		private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
//
//		static {
//			final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<CssMetaData<? extends Styleable, ?>>(
//					Parent.getClassCssMetaData());
//			Collections.addAll(styleables, BADGE_FILL, BADGE_SCALE);
//			STYLEABLES = Collections.unmodifiableList(styleables);
//		}
//	}
//
//	@Override
//	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
//		return getClassCssMetaData();
//	}
//
//	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
//		return StyleableProperties.STYLEABLES;
//	}

}
