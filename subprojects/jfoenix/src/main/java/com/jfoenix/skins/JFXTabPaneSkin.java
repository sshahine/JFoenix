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
package com.jfoenix.skins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXRippler.RipplerMask;
import com.jfoenix.controls.JFXRippler.RipplerPos;
import com.jfoenix.effects.JFXDepthManager;
import com.sun.javafx.css.converters.EnumConverter;
import com.sun.javafx.scene.control.MultiplePropertyChangeListenerHandler;
import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

import de.jensd.fx.fontawesome.Icon;

public class JFXTabPaneSkin extends BehaviorSkinBase<TabPane, TabPaneBehavior> {

	private Color defaultColor = Color.valueOf("#00BCD4"), ripplerColor = Color.valueOf("FFFF8D"), selectedTabText = Color.WHITE, unSelectedTabText = Color.LIGHTGREY;

	private static enum TabAnimation {
		NONE, GROW
		// In future we could add FADE, ...
	}

	private ObjectProperty<TabAnimation> openTabAnimation = new StyleableObjectProperty<TabAnimation>(TabAnimation.GROW) {
		@Override
		public CssMetaData<TabPane, TabAnimation> getCssMetaData() {
			return StyleableProperties.OPEN_TAB_ANIMATION;
		}

		@Override
		public Object getBean() {
			return JFXTabPaneSkin.this;
		}

		@Override
		public String getName() {
			return "openTabAnimation";
		}
	};

	private ObjectProperty<TabAnimation> closeTabAnimation = new StyleableObjectProperty<TabAnimation>(TabAnimation.GROW) {
		@Override
		public CssMetaData<TabPane, TabAnimation> getCssMetaData() {
			return StyleableProperties.CLOSE_TAB_ANIMATION;
		}

		@Override
		public Object getBean() {
			return JFXTabPaneSkin.this;
		}

		@Override
		public String getName() {
			return "closeTabAnimation";
		}
	};

	private static int getRotation(Side pos) {
		switch (pos) {
		case TOP:
			return 0;
		case BOTTOM:
			return 180;
		case LEFT:
			return -90;
		case RIGHT:
			return 90;
		default:
			return 0;
		}
	}

	/**
	 * VERY HACKY - this lets us 'duplicate' Label and ImageView nodes to be used in a
	 * Tab and the tabs menu at the same time.
	 */
	private static Node clone(Node n) {
		if (n == null) {
			return null;
		}
		if (n instanceof ImageView) {
			ImageView iv = (ImageView) n;
			ImageView imageview = new ImageView();
			imageview.setImage(iv.getImage());
			return imageview;
		}
		if (n instanceof Label) {
			Label l = (Label) n;
			Label label = new Label(l.getText(), l.getGraphic());
			return label;
		}
		return null;
	}

	private static final double ANIMATION_SPEED = 150;
	private static final int SPACER = 10;

	private TabHeaderArea tabHeaderArea;
	private ObservableList<TabContentRegion> tabContentRegions;
	private Rectangle clipRect;
	private Rectangle tabHeaderAreaClipRect;
	private Tab selectedTab;
	private boolean isSelectingTab, isDragged;
	private double dragStart, offsetStart;

	public JFXTabPaneSkin(TabPane tabPane) {
		super(tabPane, new TabPaneBehavior(tabPane));

		clipRect = new Rectangle(tabPane.getWidth(), tabPane.getHeight());
		//getSkinnable().setClip(clipRect);
//		JFXDepthManager.setDepth(getSkinnable(), 2);

		tabContentRegions = FXCollections.<TabContentRegion> observableArrayList();
		tabsContainer = new AnchorPane();
		tabsContainerHolder = new AnchorPane();

		for (Tab tab : getSkinnable().getTabs()) {
			addTabContent(tab);
		}

		tabHeaderAreaClipRect = new Rectangle();
		tabHeaderArea = new TabHeaderArea();
		tabHeaderArea.setClip(tabHeaderAreaClipRect);
		getChildren().add(tabHeaderArea);
		JFXDepthManager.setDepth(tabHeaderArea, 1);

//		tabsContainer.setStyle("-fx-border-color:RED;");
		tabsContainerHolder.getChildren().add(tabsContainer);
		getChildren().add(tabsContainerHolder);

		if (getSkinnable().getTabs().size() == 0) {
			tabHeaderArea.setVisible(false);
		}

		initializeTabListener();

		registerChangeListener(tabPane.getSelectionModel().selectedItemProperty(), "SELECTED_TAB");
		registerChangeListener(tabPane.sideProperty(), "SIDE");
		registerChangeListener(tabPane.widthProperty(), "WIDTH");
		registerChangeListener(tabPane.heightProperty(), "HEIGHT");

		selectedTab = getSkinnable().getSelectionModel().getSelectedItem();
		// Could not find the selected tab try and get the selected tab using the selected index
		if (selectedTab == null && getSkinnable().getSelectionModel().getSelectedIndex() != -1) {
			getSkinnable().getSelectionModel().select(getSkinnable().getSelectionModel().getSelectedIndex());
			selectedTab = getSkinnable().getSelectionModel().getSelectedItem();
		}
		if (selectedTab == null) {
			// getSelectedItem and getSelectedIndex failed select the first.
			getSkinnable().getSelectionModel().selectFirst();
		}
		selectedTab = getSkinnable().getSelectionModel().getSelectedItem();
		isSelectingTab = false;

		initializeSwipeHandlers();

		tabHeaderArea.headersRegion.setOnMouseDragged(me -> {
			isDragged = true;
			tabHeaderArea.setScrollOffset(offsetStart + me.getSceneX() - dragStart);
		});

		getSkinnable().setOnMousePressed(me -> {
			dragStart = me.getSceneX();
			offsetStart = tabHeaderArea.getScrollOffset();
		});

		getSkinnable().setOnMouseReleased(me -> {
			//isDragged = false;
		});
	}

	public StackPane getSelectedTabContentRegion() {
		for (TabContentRegion contentRegion : tabContentRegions) {
			if (contentRegion.getTab().equals(selectedTab)) {
				return contentRegion;
			}
		}
		return null;
	}

	@Override
	protected void handleControlPropertyChanged(String property) {
		super.handleControlPropertyChanged(property);
		if ("SELECTED_TAB".equals(property)) {
			isSelectingTab = true;
			selectedTab = getSkinnable().getSelectionModel().getSelectedItem();
			getSkinnable().requestLayout();
		} else if ("SIDE".equals(property)) {
			updateTabPosition();
		} else if ("WIDTH".equals(property)) {
			clipRect.setWidth(getSkinnable().getWidth());
		} else if ("HEIGHT".equals(property)) {
			clipRect.setHeight(getSkinnable().getHeight());
		}
	}

	private void removeTabs(List<? extends Tab> removedList) {
		for (final Tab tab : removedList) {
			// Animate the tab removal
			final TabHeaderSkin tabRegion = tabHeaderArea.getTabHeaderSkin(tab);
			if (tabRegion != null) {
				tabRegion.isClosing = true;
				if (closeTabAnimation.get() == TabAnimation.GROW) {
					Timeline closedTabTimeline = createTimeline(tabRegion, Duration.millis(ANIMATION_SPEED), 0.0F, event -> {
						handleClosedTab(tab);
					});
					closedTabTimeline.play();
				} else {
					handleClosedTab(tab);
				}
			}
		}
	}

	private void handleClosedTab(Tab tab) {
		removeTab(tab);
		if (getSkinnable().getTabs().isEmpty()) {
			tabHeaderArea.setVisible(false);
		}
	}

	private void addTabs(List<? extends Tab> addedList, int from) {
		int i = 0;
		for (final Tab tab : addedList) {
			// A new tab was added - animate it out
			if (!tabHeaderArea.isVisible()) {
				tabHeaderArea.setVisible(true);
			}
			int index = from + i++;
			tabHeaderArea.addTab(tab, index, false);
			addTabContent(tab);
			final TabHeaderSkin tabRegion = tabHeaderArea.getTabHeaderSkin(tab);
			if (tabRegion != null) {
				if (openTabAnimation.get() == TabAnimation.GROW) {
					tabRegion.animationTransition.setValue(0.0);
					tabRegion.setVisible(true);
					createTimeline(tabRegion, Duration.millis(ANIMATION_SPEED), 1.0, event -> {
						tabRegion.inner.requestLayout();
					}).play();
				} else {
					tabRegion.setVisible(true);
					tabRegion.inner.requestLayout();
				}
			}
		}
	}

	private void initializeTabListener() {
		getSkinnable().getTabs().addListener((ListChangeListener<Tab>) c -> {
			List<Tab> tabsToRemove = new ArrayList<>();
			List<Tab> tabsToAdd = new ArrayList<>();
			int insertPos = -1;

			while (c.next()) {
				if (c.wasPermutated()) {
					TabPane tabPane = getSkinnable();
					List<Tab> tabs = tabPane.getTabs();

					// tabs sorted : create list of permutated tabs.
					// clear selection, set tab animation to NONE
					// remove permutated tabs, add them back in correct order.
					// restore old selection, and old tab animation states.
					int size = c.getTo() - c.getFrom();
					Tab selTab = tabPane.getSelectionModel().getSelectedItem();
					List<Tab> permutatedTabs = new ArrayList<Tab>(size);
					getSkinnable().getSelectionModel().clearSelection();

					// save and set tab animation to none - as it is not a good idea
					// to animate on the same data for open and close.
					TabAnimation prevOpenAnimation = openTabAnimation.get();
					TabAnimation prevCloseAnimation = closeTabAnimation.get();
					openTabAnimation.set(TabAnimation.NONE);
					closeTabAnimation.set(TabAnimation.NONE);
					for (int i = c.getFrom(); i < c.getTo(); i++) {
						permutatedTabs.add(tabs.get(i));
					}

					removeTabs(permutatedTabs);
					addTabs(permutatedTabs, c.getFrom());
					openTabAnimation.set(prevOpenAnimation);
					closeTabAnimation.set(prevCloseAnimation);
					getSkinnable().getSelectionModel().select(selTab);
				}

				if (c.wasRemoved()) {
					tabsToRemove.addAll(c.getRemoved());
				}

				if (c.wasAdded()) {
					tabsToAdd.addAll(c.getAddedSubList());
					insertPos = c.getFrom();
				}
			}

			// now only remove the tabs that are not in the tabsToAdd list
			tabsToRemove.removeAll(tabsToAdd);
			removeTabs(tabsToRemove);

			// and add in any new tabs (that we don't already have showing)
			if (!tabsToAdd.isEmpty()) {
				for (TabContentRegion tabContentRegion : tabContentRegions) {
					Tab tab = tabContentRegion.getTab();
					TabHeaderSkin tabHeader = tabHeaderArea.getTabHeaderSkin(tab);
					if (!tabHeader.isClosing && tabsToAdd.contains(tabContentRegion.getTab())) {
						tabsToAdd.remove(tabContentRegion.getTab());
					}
				}

				addTabs(tabsToAdd, insertPos == -1 ? tabContentRegions.size() : insertPos);
			}

			// Fix for RT-34692
			getSkinnable().requestLayout();
		}	);
	}

	private void addTabContent(Tab tab) {
		TabContentRegion tabContentRegion = new TabContentRegion(tab);
		tabContentRegion.setClip(new Rectangle());
		tabContentRegions.add(tabContentRegion);
		// We want the tab content to always sit below the tab headers
		tabsContainer.getChildren().add(0, tabContentRegion);
	}

	private void removeTabContent(Tab tab) {
		for (TabContentRegion contentRegion : tabContentRegions) {
			if (contentRegion.getTab().equals(tab)) {
				contentRegion.removeListeners(tab);
				getChildren().remove(contentRegion);
				tabContentRegions.remove(contentRegion);
				tabsContainer.getChildren().remove(contentRegion);
				break;
			}
		}
	}

	private void removeTab(Tab tab) {
		final TabHeaderSkin tabRegion = tabHeaderArea.getTabHeaderSkin(tab);

		if (tabRegion != null) {
			tabRegion.removeListeners(tab);
		}
		tabHeaderArea.removeTab(tab);
		removeTabContent(tab);
		tabHeaderArea.requestLayout();
	}

	private void updateTabPosition() {
		tabHeaderArea.setScrollOffset(0.0F);
		getSkinnable().applyCss();
		getSkinnable().requestLayout();
	}

	private Timeline createTimeline(final TabHeaderSkin tabRegion, final Duration duration, final double endValue, final EventHandler<ActionEvent> func) {
		Timeline timeline = new Timeline();
		timeline.setCycleCount(1);

		KeyValue keyValue = new KeyValue(tabRegion.animationTransition, endValue, Interpolator.LINEAR);

		timeline.getKeyFrames().clear();
		timeline.getKeyFrames().add(new KeyFrame(duration, func, keyValue));
		return timeline;
	}

	private boolean isHorizontal() {
		Side tabPosition = getSkinnable().getSide();
		return Side.TOP.equals(tabPosition) || Side.BOTTOM.equals(tabPosition);
	}

	private void initializeSwipeHandlers() {
		if (IS_TOUCH_SUPPORTED) {
			getSkinnable().addEventHandler(SwipeEvent.SWIPE_LEFT, t -> {
				getBehavior().selectNextTab();
			});

			getSkinnable().addEventHandler(SwipeEvent.SWIPE_RIGHT, t -> {
				getBehavior().selectPreviousTab();
			});
		}
	}

	//TODO need to cache this.
	private boolean isFloatingStyleClass() {
		return getSkinnable().getStyleClass().contains(TabPane.STYLE_CLASS_FLOATING);
	}

	private double maxw = 0.0d;

	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		// The TabPane can only be as wide as it widest content width.
		for (TabContentRegion contentRegion : tabContentRegions) {
			maxw = Math.max(maxw, snapSize(contentRegion.prefWidth(-1)));
		}

		final boolean isHorizontal = isHorizontal();
		final double tabHeaderAreaSize = snapSize(isHorizontal ? tabHeaderArea.prefWidth(-1) : tabHeaderArea.prefHeight(-1));

		double prefWidth = isHorizontal ? Math.max(maxw, tabHeaderAreaSize) : maxw + tabHeaderAreaSize;
		return snapSize(prefWidth) + rightInset + leftInset;
	}

	private double maxh = 0.0d;

	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		// The TabPane can only be as high as it highest content height.
		for (TabContentRegion contentRegion : tabContentRegions) {
			maxh = Math.max(maxh, snapSize(contentRegion.prefHeight(-1)));
		}

		final boolean isHorizontal = isHorizontal();
		final double tabHeaderAreaSize = snapSize(isHorizontal ? tabHeaderArea.prefHeight(-1) : tabHeaderArea.prefWidth(-1));

		double prefHeight = isHorizontal ? maxh + snapSize(tabHeaderAreaSize) : Math.max(maxh, tabHeaderAreaSize);
		return snapSize(prefHeight) + topInset + bottomInset;
	}

	@Override
	public double computeBaselineOffset(double topInset, double rightInset, double bottomInset, double leftInset) {
		Side tabPosition = getSkinnable().getSide();
		if (tabPosition == Side.TOP) {
			return tabHeaderArea.getBaselineOffset() + topInset;
		}
		return 0;
	}

	@Override
	protected void layoutChildren(final double x, final double y, final double w, final double h) {
		TabPane tabPane = getSkinnable();
		Side tabPosition = tabPane.getSide();

		double headerHeight = snapSize(tabHeaderArea.prefHeight(-1));
		double tabsStartX = tabPosition.equals(Side.RIGHT) ? x + w - headerHeight : x;
		double tabsStartY = tabPosition.equals(Side.BOTTOM) ? y + h - headerHeight : y;

		if (tabPosition == Side.TOP) {
			tabHeaderArea.resize(w, headerHeight);
			tabHeaderArea.relocate(tabsStartX, tabsStartY);
			tabHeaderArea.getTransforms().clear();
			tabHeaderArea.getTransforms().add(new Rotate(getRotation(Side.TOP)));
		} else if (tabPosition == Side.BOTTOM) {
			tabHeaderArea.resize(w, headerHeight);
			tabHeaderArea.relocate(w, tabsStartY - headerHeight);
			tabHeaderArea.getTransforms().clear();
			tabHeaderArea.getTransforms().add(new Rotate(getRotation(Side.BOTTOM), 0, headerHeight));
		} else if (tabPosition == Side.LEFT) {
			tabHeaderArea.resize(h, headerHeight);
			tabHeaderArea.relocate(tabsStartX + headerHeight, h - headerHeight);
			tabHeaderArea.getTransforms().clear();
			tabHeaderArea.getTransforms().add(new Rotate(getRotation(Side.LEFT), 0, headerHeight));
		} else if (tabPosition == Side.RIGHT) {
			tabHeaderArea.resize(h, headerHeight);
			tabHeaderArea.relocate(tabsStartX, y - headerHeight);
			tabHeaderArea.getTransforms().clear();
			tabHeaderArea.getTransforms().add(new Rotate(getRotation(Side.RIGHT), 0, headerHeight));
		}

		tabHeaderAreaClipRect.setX(0);
		tabHeaderAreaClipRect.setY(0);
		if (isHorizontal()) {
			tabHeaderAreaClipRect.setWidth(w);
		} else {
			tabHeaderAreaClipRect.setWidth(h);
		}
		tabHeaderAreaClipRect.setHeight(headerHeight + 7); // 7 is the height of effect

		// ==================================
		// position the tab content for the selected tab only
		// ==================================
		// if the tabs are on the left, the content needs to be indented
		double contentStartX = 0;
		double contentStartY = 0;

		if (tabPosition == Side.TOP) {
			contentStartX = x;
			contentStartY = y + headerHeight;
			if (isFloatingStyleClass()) {
				// This is to hide the top border content
				contentStartY -= 1;
			}
		} else if (tabPosition == Side.BOTTOM) {
			contentStartX = x;
			contentStartY = y;
			if (isFloatingStyleClass()) {
				// This is to hide the bottom border content
				contentStartY = 1;
			}
		} else if (tabPosition == Side.LEFT) {
			contentStartX = x + headerHeight;
			contentStartY = y;
			if (isFloatingStyleClass()) {
				// This is to hide the left border content
				contentStartX -= 1;
			}
		} else if (tabPosition == Side.RIGHT) {
			contentStartX = x;
			contentStartY = y;
			if (isFloatingStyleClass()) {
				// This is to hide the right border content
				contentStartX = 1;
			}
		}

		double contentWidth = w - (isHorizontal() ? 0 : headerHeight);
		double contentHeight = h - (isHorizontal() ? headerHeight : 0);

		Rectangle clip = new Rectangle(contentWidth,contentHeight);
		tabsContainerHolder.setClip(clip);
		tabsContainerHolder.resize(contentWidth, contentHeight);
		tabsContainerHolder.relocate(contentStartX, contentStartY);

		tabsContainer.resize(contentWidth * tabContentRegions.size(), contentHeight);

		for (int i = 0, max = tabContentRegions.size(); i < max; i++) {
			TabContentRegion tabContent = tabContentRegions.get(i);
			tabContent.setVisible(true);

			tabContent.setTranslateX(contentWidth*i);

			if (tabContent.getClip() != null) {
				((Rectangle) tabContent.getClip()).setWidth(contentWidth);
				((Rectangle) tabContent.getClip()).setHeight(contentHeight);

			}
			
			if(tabContent.getTab() == selectedTab){
				int index = getSkinnable().getTabs().indexOf(selectedTab);
				if(index != i) {
					tabsContainer.setTranslateX(-contentWidth*i);
					diffTabsIndices = i - index;
				}else{
					// fix X translation after changing the tabs
					if(diffTabsIndices!=0){
						tabsContainer.setTranslateX(tabsContainer.getTranslateX() + contentWidth*diffTabsIndices);	
						diffTabsIndices = 0;
					}
					// animate upon tab selection only otherwise just translate the selected tab 
					if(isSelectingTab) new Timeline(new KeyFrame(Duration.millis(320), new KeyValue(tabsContainer.translateXProperty(), -contentWidth*index, Interpolator.EASE_BOTH))).play();
					else tabsContainer.setTranslateX(-contentWidth*index);
				}
			}

			// we need to size all tabs, even if they aren't visible. For example,
			// see RT-29167
			tabContent.resize(contentWidth, contentHeight);
			//			tabContent.relocate(contentStartX, contentStartY);
		}
	}

	/*
	 *  keep track of indecies after changing the tabs, it used to fix 
	 *  tabs animation after changing the tabs (remove/add)
	 */
	private int diffTabsIndices = 0;
	
	/**
	 * Super-lazy instantiation pattern from Bill Pugh.
	 * @treatAsPrivate implementation detail
	 */
	private static class StyleableProperties {
		private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

		private final static CssMetaData<TabPane, TabAnimation> OPEN_TAB_ANIMATION = new CssMetaData<TabPane, JFXTabPaneSkin.TabAnimation>("-fx-open-tab-animation", new EnumConverter<TabAnimation>(
				TabAnimation.class), TabAnimation.GROW) {

			@Override
			public boolean isSettable(TabPane node) {
				return true;
			}

			@Override
			public StyleableProperty<TabAnimation> getStyleableProperty(TabPane node) {
				JFXTabPaneSkin skin = (JFXTabPaneSkin) node.getSkin();
				return (StyleableProperty<TabAnimation>) (WritableValue<TabAnimation>) skin.openTabAnimation;
			}
		};

		private final static CssMetaData<TabPane, TabAnimation> CLOSE_TAB_ANIMATION = new CssMetaData<TabPane, JFXTabPaneSkin.TabAnimation>("-fx-close-tab-animation", new EnumConverter<TabAnimation>(
				TabAnimation.class), TabAnimation.GROW) {

			@Override
			public boolean isSettable(TabPane node) {
				return true;
			}

			@Override
			public StyleableProperty<TabAnimation> getStyleableProperty(TabPane node) {
				JFXTabPaneSkin skin = (JFXTabPaneSkin) node.getSkin();
				return (StyleableProperty<TabAnimation>) (WritableValue<TabAnimation>) skin.closeTabAnimation;
			}
		};

		static {

			final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<CssMetaData<? extends Styleable, ?>>(SkinBase.getClassCssMetaData());
			styleables.add(OPEN_TAB_ANIMATION);
			styleables.add(CLOSE_TAB_ANIMATION);
			STYLEABLES = Collections.unmodifiableList(styleables);

		}
	}

	/**
	 * @return The CssMetaData associated with this class, which may include the
	 * CssMetaData of its super classes.
	 */
	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.STYLEABLES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		return getClassCssMetaData();
	}

	/**************************************************************************
	 *
	 * TabHeaderArea: Area responsible for painting all tabs
	 *
	 **************************************************************************/
	class TabHeaderArea extends StackPane {

		private Rectangle headerClip;
		private StackPane headersRegion;
		private StackPane headerBackground;
		private TabControlButtons rightControlButtons;
		private TabControlButtons leftControlButtons;
		private Line selectedTabLine;
		private boolean initialized;

		private boolean measureClosingTabs = false;

		private double scrollOffset, selectedTabLineOffset;

		public TabHeaderArea() {
			getStyleClass().setAll("tab-header-area");
			setManaged(false);
			final TabPane tabPane = getSkinnable();

			headerClip = new Rectangle();

			headersRegion = new StackPane() {
				@Override
				protected double computePrefWidth(double height) {
					double width = 0.0F;
					for (Node child : getChildren()) {
						if(child instanceof TabHeaderSkin){
							TabHeaderSkin tabHeaderSkin = (TabHeaderSkin) child;
							if (tabHeaderSkin.isVisible() && (measureClosingTabs || !tabHeaderSkin.isClosing)) {
								width += tabHeaderSkin.prefWidth(height);
							}
						}
					}
					return snapSize(width) + snappedLeftInset() + snappedRightInset();
				}

				@Override
				protected double computePrefHeight(double width) {
					double height = 0.0F;
					for (Node child : getChildren()) {
						if(child instanceof TabHeaderSkin){
							TabHeaderSkin tabHeaderSkin = (TabHeaderSkin) child;
							height = Math.max(height, tabHeaderSkin.prefHeight(width));
						}
					}
					return snapSize(height) + snappedTopInset() + snappedBottomInset();
				}

				@Override
				protected void layoutChildren() {
					if (tabsFit()) {
						setScrollOffset(0.0);
					} else {
						if (!removeTab.isEmpty()) {
							double offset = 0;
							double w = tabHeaderArea.getWidth() - snapSize(rightControlButtons.prefWidth(-1)) - snapSize(leftControlButtons.prefWidth(-1)) - firstTabIndent() - SPACER;
							Iterator<Node> i = getChildren().iterator();
							while (i.hasNext()) {
								Node temp = i.next();
								if(temp instanceof TabHeaderSkin){
									TabHeaderSkin tabHeader = (TabHeaderSkin) temp;
									double tabHeaderPrefWidth = snapSize(tabHeader.prefWidth(-1));
									if (removeTab.contains(tabHeader)) {
										if (offset < w) {
											isSelectingTab = true;
										}
										i.remove();
										removeTab.remove(tabHeader);
										if (removeTab.isEmpty()) {
											break;
										}
									}
									offset += tabHeaderPrefWidth;
								}
							}
						}
					}

					if (isSelectingTab) {
						ensureSelectedTabIsVisible();
						isSelectingTab = false;
					} else {
						validateScrollOffset();
					}

					Side tabPosition = getSkinnable().getSide();
					double tabBackgroundHeight = snapSize(prefHeight(-1));
					double tabX = (tabPosition.equals(Side.LEFT) || tabPosition.equals(Side.BOTTOM)) ? snapSize(getWidth()) - getScrollOffset() : getScrollOffset();

					updateHeaderClip();
					for (Node node : getChildren()) {
						if(node instanceof TabHeaderSkin){
							TabHeaderSkin tabHeader = (TabHeaderSkin) node;

							// size and position the header relative to the other headers
							double tabHeaderPrefWidth = snapSize(tabHeader.prefWidth(-1) * tabHeader.animationTransition.get());
							double tabHeaderPrefHeight = snapSize(tabHeader.prefHeight(-1));
							tabHeader.resize(tabHeaderPrefWidth, tabHeaderPrefHeight);

							// This ensures that the tabs are located in the correct position
							// when there are tabs of differing heights.
							double startY = tabPosition.equals(Side.BOTTOM) ? 0 : tabBackgroundHeight - tabHeaderPrefHeight - snappedBottomInset();
							if (tabPosition.equals(Side.LEFT) || tabPosition.equals(Side.BOTTOM)) {
								// build from the right
								tabX -= tabHeaderPrefWidth;
								tabHeader.relocate(tabX, startY);
							} else {
								// build from the left
								tabHeader.relocate(tabX, startY);
								tabX += tabHeaderPrefWidth;
							}

						}
					}
				}

			};
			headersRegion.getStyleClass().setAll("headers-region");
			headersRegion.setClip(headerClip);


			headerBackground = new StackPane();
			headerBackground.setBackground(new Background(new BackgroundFill(defaultColor, CornerRadii.EMPTY, Insets.EMPTY)));
			headerBackground.getStyleClass().setAll("tab-header-background");

			selectedTabLine = new Line();
			selectedTabLine.getStyleClass().add("tab-selected-line");
			selectedTabLine.setStrokeWidth(2);
			selectedTabLine.setStroke(ripplerColor);
			headersRegion.getChildren().add(selectedTabLine);
			selectedTabLine.translateYProperty().bind(Bindings.createDoubleBinding(()-> headersRegion.getHeight() - selectedTabLine.getStrokeWidth() + 1, headersRegion.heightProperty(), selectedTabLine.strokeWidthProperty()));
			

			int i = 0;
			for (Tab tab : tabPane.getTabs()) {
				addTab(tab, i++, true);
			}

			rightControlButtons = new TabControlButtons(ArrowPosition.RIGHT);
			leftControlButtons = new TabControlButtons(ArrowPosition.LEFT);

			rightControlButtons.setVisible(false);
			leftControlButtons.setVisible(false);
			
			//controlButtons.setVisible(false);
//			if (rightControlButtons.isVisible()) {
//				rightControlButtons.setVisible(true);
//			}

			rightControlButtons.inner.prefHeightProperty().bind(headersRegion.heightProperty());
			leftControlButtons.inner.prefHeightProperty().bind(headersRegion.heightProperty());

			getChildren().addAll(headerBackground, headersRegion, leftControlButtons, rightControlButtons);

			// support for mouse scroll of header area (for when the tabs exceed
			// the available space)
			addEventHandler(ScrollEvent.SCROLL, (ScrollEvent e) -> {
				Side side = getSkinnable().getSide();
				side = side == null ? Side.TOP : side;
				switch (side) {
				default:
				case TOP:
				case BOTTOM:
					setScrollOffset(scrollOffset - e.getDeltaY());
					break;
				case LEFT:
				case RIGHT:
					setScrollOffset(scrollOffset + e.getDeltaY());
					break;
				}

			});
		}

		private void updateHeaderClip() {
			Side tabPosition = getSkinnable().getSide();

			double x = 0;
			double y = 0;
			double clipWidth = 0;
			double clipHeight = 0;
			double maxWidth = 0;
			double shadowRadius = 0;
			double clipOffset = firstTabIndent();
			double controlButtonPrefWidth = 2 * snapSize(rightControlButtons.prefWidth(-1));

			measureClosingTabs = true;
			double headersPrefWidth = snapSize(headersRegion.prefWidth(-1));
			measureClosingTabs = false;

			double headersPrefHeight = snapSize(headersRegion.prefHeight(-1));

			// Add the spacer if isShowTabsMenu is true.
			if (controlButtonPrefWidth > 0) {
				controlButtonPrefWidth = controlButtonPrefWidth + SPACER;
			}

			if (headersRegion.getEffect() instanceof DropShadow) {
				DropShadow shadow = (DropShadow) headersRegion.getEffect();
				shadowRadius = shadow.getRadius();
			}

			maxWidth = snapSize(getWidth()) - controlButtonPrefWidth - clipOffset;
			if (tabPosition.equals(Side.LEFT) || tabPosition.equals(Side.BOTTOM)) {
				if (headersPrefWidth < maxWidth) {
					clipWidth = headersPrefWidth + shadowRadius;
				} else {
					x = headersPrefWidth - maxWidth;
					clipWidth = maxWidth + shadowRadius;
				}
				clipHeight = headersPrefHeight;
			} else {
				// If x = 0 the header region's drop shadow is clipped.
				x = -shadowRadius;
				clipWidth = (headersPrefWidth < maxWidth ? headersPrefWidth : maxWidth) + shadowRadius;
				clipHeight = headersPrefHeight;
			}

			headerClip.setX(x);
			headerClip.setY(y);
			headerClip.setWidth(clipWidth + 10);
			headerClip.setHeight(clipHeight);
		}

		private void addTab(Tab tab, int addToIndex, boolean visible) {
			TabHeaderSkin tabHeaderSkin = new TabHeaderSkin(tab);
			tabHeaderSkin.setVisible(visible);
			headersRegion.getChildren().add(addToIndex, tabHeaderSkin);
		}

		private List<TabHeaderSkin> removeTab = new ArrayList<>();

		private void removeTab(Tab tab) {
			TabHeaderSkin tabHeaderSkin = getTabHeaderSkin(tab);
			if (tabHeaderSkin != null) {
				if (tabsFit()) {
					headersRegion.getChildren().remove(tabHeaderSkin);
				} else {
					// The tab will be removed during layout because
					// we need its width to compute the scroll offset.
					removeTab.add(tabHeaderSkin);
					tabHeaderSkin.removeListeners(tab);
				}
			}
		}

		private TabHeaderSkin getTabHeaderSkin(Tab tab) {
			for (Node child : headersRegion.getChildren()) {
				if(child instanceof TabHeaderSkin){
					TabHeaderSkin tabHeaderSkin = (TabHeaderSkin) child;
					if (tabHeaderSkin.getTab().equals(tab)) {
						return tabHeaderSkin;
					}
				}
			}
			return null;
		}

		private boolean tabsFit() {
			double headerPrefWidth = snapSize(headersRegion.prefWidth(-1));
			double controlTabWidth = 2 * snapSize(rightControlButtons.prefWidth(-1));
			double visibleWidth = headerPrefWidth + controlTabWidth + firstTabIndent() + SPACER;
			return visibleWidth < getWidth();
		}

		private void ensureSelectedTabIsVisible() {
			double offset = 0.0;
			double selectedTabOffset = 0.0;
			double selectedTabWidth = 0.0;
			for (Node node : headersRegion.getChildren()) {
				if(node instanceof TabHeaderSkin){
					TabHeaderSkin tabHeader = (TabHeaderSkin) node;
					double tabHeaderPrefWidth = snapSize(tabHeader.prefWidth(-1));
					if (selectedTab != null && selectedTab.equals(tabHeader.getTab())) {
						selectedTabOffset = offset;
						selectedTabWidth = tabHeaderPrefWidth;
					}
					offset += tabHeaderPrefWidth;	
				}
			}
			runTimeline(selectedTabOffset + 1, selectedTabWidth - 2);
		}

		private void runTimeline(double newTransX, double newWidth) {
			double oldWidth = selectedTabLine.getEndX();
			double oldTransX = selectedTabLineOffset;

			selectedTabLineOffset = newTransX;
			double transDiff = newTransX - oldTransX;
			Timeline timeline;
			if(transDiff > 0) {
				timeline = new Timeline(
						new KeyFrame(
								Duration.ZERO,
								new KeyValue(selectedTabLine.endXProperty(), oldWidth, Interpolator.EASE_BOTH),
								new KeyValue(selectedTabLine.translateXProperty(), oldTransX + offsetStart, Interpolator.EASE_BOTH)),
								new KeyFrame(
										Duration.seconds(0.15),
										new KeyValue(selectedTabLine.endXProperty(), transDiff, Interpolator.EASE_BOTH),
										new KeyValue(selectedTabLine.translateXProperty(), oldTransX + offsetStart, Interpolator.EASE_BOTH)),
										new KeyFrame(
												Duration.seconds(0.33),
												new KeyValue(selectedTabLine.endXProperty(), newWidth, Interpolator.EASE_BOTH),
												new KeyValue(selectedTabLine.translateXProperty(), newTransX + offsetStart, Interpolator.EASE_BOTH))
						);
			} else {
				timeline = new Timeline(
						new KeyFrame(
								Duration.ZERO,
								new KeyValue(selectedTabLine.endXProperty(), oldWidth, Interpolator.EASE_BOTH),
								new KeyValue(selectedTabLine.translateXProperty(), oldTransX + offsetStart, Interpolator.EASE_BOTH)),
								new KeyFrame(
										Duration.seconds(0.15),
										new KeyValue(selectedTabLine.endXProperty(), -transDiff, Interpolator.EASE_BOTH),
										new KeyValue(selectedTabLine.translateXProperty(), newTransX + offsetStart, Interpolator.EASE_BOTH)),
										new KeyFrame(
												Duration.seconds(0.33),
												new KeyValue(selectedTabLine.endXProperty(), newWidth, Interpolator.EASE_BOTH),
												new KeyValue(selectedTabLine.translateXProperty(), newTransX + offsetStart, Interpolator.EASE_BOTH))
						);
			}
			timeline.play();
		}

		public double getScrollOffset() {
			return scrollOffset;
		}

		private void validateScrollOffset() {
			setScrollOffset(getScrollOffset());
		}

		public void setScrollOffset(double newScrollOffset) {
			// work out the visible width of the tab header
			double tabPaneWidth = snapSize(getSkinnable().getWidth());
			double controlTabWidth = 2 * snapSize(rightControlButtons.getWidth());
			double visibleWidth = tabPaneWidth - controlTabWidth - firstTabIndent() - SPACER;

			// measure the width of all tabs
			double offset = 0.0;
			for (Node node : headersRegion.getChildren()) {
				if(node instanceof TabHeaderSkin){
					TabHeaderSkin tabHeader = (TabHeaderSkin) node;
					double tabHeaderPrefWidth = snapSize(tabHeader.prefWidth(-1));
					offset += tabHeaderPrefWidth;
				}
			}

			double actualNewScrollOffset;

			if ((visibleWidth - newScrollOffset) > offset && newScrollOffset < 0) {
				// need to make sure the right-most tab is attached to the
				// right-hand side of the tab header (e.g. if the tab header area width
				// is expanded), and if it isn't modify the scroll offset to bring
				// it into line. See RT-35194 for a test case.
				actualNewScrollOffset = visibleWidth - offset;
			} else if (newScrollOffset > 0) {
				// need to prevent the left-most tab from becoming detached
				// from the left-hand side of the tab header.
				actualNewScrollOffset = 0;
			} else {
				actualNewScrollOffset = newScrollOffset;
			}

			if (actualNewScrollOffset != scrollOffset) {
				scrollOffset = actualNewScrollOffset;
				headersRegion.requestLayout();
				selectedTabLine.setTranslateX(selectedTabLineOffset + scrollOffset);
			}
		}

		private double firstTabIndent() {
			switch (getSkinnable().getSide()) {
			case TOP:
			case BOTTOM:
				return snappedLeftInset();
			case RIGHT:
			case LEFT:
				return snappedTopInset();
			default:
				return 0;
			}
		}

		@Override
		protected double computePrefWidth(double height) {
			double padding = isHorizontal() ? snappedLeftInset() + snappedRightInset() : snappedTopInset() + snappedBottomInset();
			return snapSize(headersRegion.prefWidth(height)) + 2 * rightControlButtons.prefWidth(height) + firstTabIndent() + SPACER + padding;
		}

		@Override
		protected double computePrefHeight(double width) {
			double padding = isHorizontal() ? snappedTopInset() + snappedBottomInset() : snappedLeftInset() + snappedRightInset();
			return snapSize(headersRegion.prefHeight(-1)) + padding;
		}

		@Override
		public double getBaselineOffset() {
			if (getSkinnable().getSide() == Side.TOP) {
				return headersRegion.getBaselineOffset() + snappedTopInset();
			}
			return 0;
		}

		@Override
		protected void layoutChildren() {
			final double leftInset = snappedLeftInset();
			final double rightInset = snappedRightInset();
			final double topInset = snappedTopInset();
			final double bottomInset = snappedBottomInset();
			double w = snapSize(getWidth()) - (isHorizontal() ? leftInset + rightInset : topInset + bottomInset);
			double h = snapSize(getHeight()) - (isHorizontal() ? topInset + bottomInset : leftInset + rightInset);
			double tabBackgroundHeight = snapSize(prefHeight(-1));
			double headersPrefWidth = snapSize(headersRegion.prefWidth(-1));
			double headersPrefHeight = snapSize(headersRegion.prefHeight(-1));

			rightControlButtons.showTabsMenu(!tabsFit());
			leftControlButtons.showTabsMenu(!tabsFit());

			updateHeaderClip();
			headersRegion.requestLayout();

			// RESIZE CONTROL BUTTONS
			double btnWidth = snapSize(rightControlButtons.prefWidth(-1));
			final double btnHeight = rightControlButtons.prefHeight(btnWidth);
			rightControlButtons.resize(btnWidth, btnHeight);
			leftControlButtons.resize(btnWidth, btnHeight);

			// POSITION TABS
			headersRegion.resize(headersPrefWidth, headersPrefHeight);

			if (isFloatingStyleClass()) {
				headerBackground.setVisible(false);
			} else {
				headerBackground.resize(snapSize(getWidth()), snapSize(getHeight()));
				headerBackground.setVisible(true);
			}

			double startX = 0;
			double startY = 0;
			double controlStartX = 0;
			double controlStartY = 0;
			Side tabPosition = getSkinnable().getSide();



			if (tabPosition.equals(Side.TOP)) {
				startX = leftInset;
				startY = tabBackgroundHeight - headersPrefHeight - bottomInset;
				controlStartX = w - btnWidth + leftInset;
				controlStartY = snapSize(getHeight()) - btnHeight - bottomInset;
			} else if (tabPosition.equals(Side.RIGHT)) {
				startX = topInset;
				startY = tabBackgroundHeight - headersPrefHeight - leftInset;
				controlStartX = w - btnWidth + topInset;
				controlStartY = snapSize(getHeight()) - btnHeight - leftInset;
			} else if (tabPosition.equals(Side.BOTTOM)) {
				startX = snapSize(getWidth()) - headersPrefWidth - leftInset;
				startY = tabBackgroundHeight - headersPrefHeight - topInset;
				controlStartX = rightInset;
				controlStartY = snapSize(getHeight()) - btnHeight - topInset;
			} else if (tabPosition.equals(Side.LEFT)) {
				startX = snapSize(getWidth()) - headersPrefWidth - topInset;
				startY = tabBackgroundHeight - headersPrefHeight - rightInset;
				controlStartX = leftInset;
				controlStartY = snapSize(getHeight()) - btnHeight - rightInset;
			}
			if (headerBackground.isVisible()) {
				positionInArea(headerBackground, 0, 0, snapSize(getWidth()), snapSize(getHeight()), 0, HPos.CENTER, VPos.CENTER);
			}
			positionInArea(headersRegion, startX + btnWidth, startY, w, h, 0, HPos.LEFT, VPos.CENTER);
			positionInArea(rightControlButtons, controlStartX, controlStartY, btnWidth, btnHeight, 0, HPos.CENTER, VPos.CENTER);
			positionInArea(leftControlButtons, 0, controlStartY, btnWidth, btnHeight, 0, HPos.CENTER, VPos.CENTER);

			if (!initialized) {
				double offset = 0.0;
				double selectedTabOffset = 0.0;
				double selectedTabWidth = 0.0;
				for (Node node : headersRegion.getChildren()) {
					if(node instanceof TabHeaderSkin){
						TabHeaderSkin tabHeader = (TabHeaderSkin) node;
						double tabHeaderPrefWidth = snapSize(tabHeader.prefWidth(-1));
						if (selectedTab != null && selectedTab.equals(tabHeader.getTab())) {
							selectedTabOffset = offset;
							selectedTabWidth = tabHeaderPrefWidth;
						}
						offset += tabHeaderPrefWidth;
					}
				}
				final double selectedTabStartX = selectedTabOffset;
				runTimeline(selectedTabStartX + 1, selectedTabWidth-2);
				initialized = true;
			}

		}
	} /* End TabHeaderArea */


	private static int CLOSE_LBL_SIZE = 12;

	/**************************************************************************
	 *
	 * TabHeaderSkin: skin for each tab
	 *
	 **************************************************************************/

	class TabHeaderSkin extends StackPane {
		private final Tab tab;

		public Tab getTab() {
			return tab;
		}

		private Label tabText;
//		private Label closeLabel;
		private BorderPane inner;
		private Tooltip oldTooltip;
		private Tooltip tooltip;
		private JFXRippler rippler;

		private boolean isClosing = false;

		private MultiplePropertyChangeListenerHandler listener = new MultiplePropertyChangeListenerHandler(param -> {
			handlePropertyChanged(param);
			return null;
		});

		private final ListChangeListener<String> styleClassListener = new ListChangeListener<String>() {
			@Override
			public void onChanged(Change<? extends String> c) {
				getStyleClass().setAll(tab.getStyleClass());
			}
		};

		private final WeakListChangeListener<String> weakStyleClassListener = new WeakListChangeListener<>(styleClassListener);

		public TabHeaderSkin(final Tab tab) {
			getStyleClass().setAll(tab.getStyleClass());
			setId(tab.getId());
			setStyle(tab.getStyle());

			this.tab = tab;

			tabText = new Label(tab.getText(), tab.getGraphic());
			tabText.setFont(Font.font("", FontWeight.BOLD, 16));			
//			tabText.setStyle("-fx-font-weight: BOLD");
			tabText.setPadding(new Insets(5, 10, 5, 10));
			tabText.getStyleClass().setAll("tab-label");

			// TODO: close label animation
//			closeLabel = new Label();
//			closeLabel.setPrefSize(CLOSE_LBL_SIZE, CLOSE_LBL_SIZE);
//			closeLabel.setText("X");
//			closeLabel.setTextFill(Color.WHITE);
//			closeLabel.getStyleClass().setAll("tab-close-button");
//			closeLabel.setOnMousePressed(new EventHandler<MouseEvent>() {
//				@Override
//				public void handle(MouseEvent me) {
//					Tab tab = getTab();
//					TabPaneBehavior behavior = getBehavior();
//					if (behavior.canCloseTab(tab)) {
//						behavior.closeTab(tab);
//						setOnMousePressed(null);
//					}
//				}
//			});

			updateGraphicRotation();

			inner = new BorderPane();
			inner.setCenter(tabText);
			inner.getStyleClass().add("tab-container");
			inner.setRotate(getSkinnable().getSide().equals(Side.BOTTOM) ? 180.0F : 0.0F);

			rippler = new JFXRippler(inner, RipplerPos.FRONT);
			rippler.setRipplerFill(ripplerColor);
//			rippler.getChildren().add(closeLabel);
//			StackPane.setAlignment(closeLabel, Pos.TOP_RIGHT);

			getChildren().addAll(rippler);

			tooltip = tab.getTooltip();
			if (tooltip != null) {
				Tooltip.install(this, tooltip);
				oldTooltip = tooltip;
			}

			if (tab.isSelected()) {
				tabText.setTextFill(selectedTabText);
			} else {
				tabText.setTextFill(unSelectedTabText);
			}
//			closeLabel.setVisible(tab.isSelected());

			tab.selectedProperty().addListener((o, oldVal, newVal) -> {
				if (newVal) {
					tabText.setTextFill(selectedTabText);
				} else {
					tabText.setTextFill(unSelectedTabText);
				}
//				closeLabel.setVisible(newVal);
			});

			listener.registerChangeListener(tab.closableProperty(), "CLOSABLE");
			listener.registerChangeListener(tab.selectedProperty(), "SELECTED");
			listener.registerChangeListener(tab.textProperty(), "TEXT");
			listener.registerChangeListener(tab.graphicProperty(), "GRAPHIC");
			listener.registerChangeListener(tab.contextMenuProperty(), "CONTEXT_MENU");
			listener.registerChangeListener(tab.tooltipProperty(), "TOOLTIP");
			listener.registerChangeListener(tab.disableProperty(), "DISABLE");
			listener.registerChangeListener(tab.styleProperty(), "STYLE");

			tab.getStyleClass().addListener(weakStyleClassListener);

			listener.registerChangeListener(getSkinnable().tabClosingPolicyProperty(), "TAB_CLOSING_POLICY");
			listener.registerChangeListener(getSkinnable().sideProperty(), "SIDE");
			listener.registerChangeListener(getSkinnable().rotateGraphicProperty(), "ROTATE_GRAPHIC");
			listener.registerChangeListener(getSkinnable().tabMinWidthProperty(), "TAB_MIN_WIDTH");
			listener.registerChangeListener(getSkinnable().tabMaxWidthProperty(), "TAB_MAX_WIDTH");
			listener.registerChangeListener(getSkinnable().tabMinHeightProperty(), "TAB_MIN_HEIGHT");
			listener.registerChangeListener(getSkinnable().tabMaxHeightProperty(), "TAB_MAX_HEIGHT");

			getProperties().put(Tab.class, tab);
			getProperties().put(ContextMenu.class, tab.getContextMenu());

			setOnContextMenuRequested((ContextMenuEvent me) -> {
				if (getTab().getContextMenu() != null) {
					getTab().getContextMenu().show(inner, me.getScreenX(), me.getScreenY());
					me.consume();
				}
			});
			setOnMouseReleased(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent me) {
					if (isDragged) {
						isDragged = false;
						return;
					}
					if (getTab().isDisable()) {
						return;
					}
					if (me.getButton().equals(MouseButton.MIDDLE)) {
						if (showCloseButton()) {
							Tab tab = getTab();
							TabPaneBehavior behavior = getBehavior();
							if (behavior.canCloseTab(tab)) {
								removeListeners(tab);
								behavior.closeTab(tab);
							}
						}
					} else if (me.getButton().equals(MouseButton.PRIMARY)) {
						setOpacity(1);
						getBehavior().selectTab(getTab());
					}
				}
			});

			setOnMouseEntered((o) -> {
//				closeLabel.setVisible(true);
				if (!tab.isSelected()) {
					setOpacity(0.7);
				}
			});

			setOnMouseExited((o) -> {
//				if (!tab.isSelected()) {
//					closeLabel.setVisible(false);
//				}
				setOpacity(1);
			});

			// initialize pseudo-class state
			pseudoClassStateChanged(SELECTED_PSEUDOCLASS_STATE, tab.isSelected());
			pseudoClassStateChanged(DISABLED_PSEUDOCLASS_STATE, tab.isDisable());
			final Side side = getSkinnable().getSide();
			pseudoClassStateChanged(TOP_PSEUDOCLASS_STATE, (side == Side.TOP));
			pseudoClassStateChanged(RIGHT_PSEUDOCLASS_STATE, (side == Side.RIGHT));
			pseudoClassStateChanged(BOTTOM_PSEUDOCLASS_STATE, (side == Side.BOTTOM));
			pseudoClassStateChanged(LEFT_PSEUDOCLASS_STATE, (side == Side.LEFT));
		}

		private void handlePropertyChanged(final String p) {
			// --- Tab properties
			if ("CLOSABLE".equals(p)) {
				inner.requestLayout();
				requestLayout();
			} else if ("SELECTED".equals(p)) {
				pseudoClassStateChanged(SELECTED_PSEUDOCLASS_STATE, tab.isSelected());
				// Need to request a layout pass for inner because if the width
				// and height didn't not change the label or close button may have
				// changed.
				inner.requestLayout();
				requestLayout();
			} else if ("TEXT".equals(p)) {
				tabText.setText(getTab().getText());
			} else if ("GRAPHIC".equals(p)) {
				tabText.setGraphic(getTab().getGraphic());
			} else if ("CONTEXT_MENU".equals(p)) {
				// todo
			} else if ("TOOLTIP".equals(p)) {
				// uninstall the old tooltip
				if (oldTooltip != null) {
					Tooltip.uninstall(this, oldTooltip);
				}
				tooltip = tab.getTooltip();
				if (tooltip != null) {
					// install new tooltip and save as old tooltip.
					Tooltip.install(this, tooltip);
					oldTooltip = tooltip;
				}
			} else if ("DISABLE".equals(p)) {
				pseudoClassStateChanged(DISABLED_PSEUDOCLASS_STATE, tab.isDisable());
				inner.requestLayout();
				requestLayout();
			} else if ("STYLE".equals(p)) {
				setStyle(tab.getStyle());
			}

			// --- Skinnable properties
			else if ("TAB_CLOSING_POLICY".equals(p)) {
				inner.requestLayout();
				requestLayout();
			} else if ("SIDE".equals(p)) {
				final Side side = getSkinnable().getSide();
				pseudoClassStateChanged(TOP_PSEUDOCLASS_STATE, (side == Side.TOP));
				pseudoClassStateChanged(RIGHT_PSEUDOCLASS_STATE, (side == Side.RIGHT));
				pseudoClassStateChanged(BOTTOM_PSEUDOCLASS_STATE, (side == Side.BOTTOM));
				pseudoClassStateChanged(LEFT_PSEUDOCLASS_STATE, (side == Side.LEFT));
				inner.setRotate(side == Side.BOTTOM ? 180.0F : 0.0F);
				if (getSkinnable().isRotateGraphic()) {
					updateGraphicRotation();
				}
			} else if ("ROTATE_GRAPHIC".equals(p)) {
				updateGraphicRotation();
			} else if ("TAB_MIN_WIDTH".equals(p)) {
				requestLayout();
				getSkinnable().requestLayout();
			} else if ("TAB_MAX_WIDTH".equals(p)) {
				requestLayout();
				getSkinnable().requestLayout();
			} else if ("TAB_MIN_HEIGHT".equals(p)) {
				requestLayout();
				getSkinnable().requestLayout();
			} else if ("TAB_MAX_HEIGHT".equals(p)) {
				requestLayout();
				getSkinnable().requestLayout();
			}
		}

		private void updateGraphicRotation() {
			if (tabText.getGraphic() != null) {
				tabText.getGraphic().setRotate(getSkinnable().isRotateGraphic() ? 0.0F : (getSkinnable().getSide().equals(Side.RIGHT) ? -90.0F : (getSkinnable().getSide().equals(Side.LEFT) ? 90.0F : 0.0F)));
			}
		}

		private boolean showCloseButton() {
			return tab.isClosable() && (getSkinnable().getTabClosingPolicy().equals(TabClosingPolicy.ALL_TABS) || getSkinnable().getTabClosingPolicy().equals(TabClosingPolicy.SELECTED_TAB));
		}

		private final DoubleProperty animationTransition = new SimpleDoubleProperty(this, "animationTransition", 1.0) {
			@Override
			protected void invalidated() {
				requestLayout();
			}
		};

		private void removeListeners(Tab tab) {
			listener.dispose();
			ContextMenu menu = tab.getContextMenu();
			if (menu != null) {
				menu.getItems().clear();
			}
			inner.getChildren().clear();
			getChildren().clear();
		}

		@Override
		protected double computePrefWidth(double height) {
			double minWidth = snapSize(getSkinnable().getTabMinWidth());
			double maxWidth = snapSize(getSkinnable().getTabMaxWidth());
			double paddingRight = snappedRightInset();
			double paddingLeft = snappedLeftInset();
			double tmpPrefWidth = snapSize(tabText.prefWidth(-1));

			// only include the close button width if it is relevant
//			if (showCloseButton()) {
//				tmpPrefWidth += snapSize(closeLabel.prefWidth(-1));
//			}

			if (tmpPrefWidth > maxWidth) {
				tmpPrefWidth = maxWidth;
			} else if (tmpPrefWidth < minWidth) {
				tmpPrefWidth = minWidth;
			}
			tmpPrefWidth += paddingRight + paddingLeft;
			return tmpPrefWidth;
		}

		@Override
		protected double computePrefHeight(double width) {
			double minHeight = snapSize(getSkinnable().getTabMinHeight());
			double maxHeight = snapSize(getSkinnable().getTabMaxHeight());
			double paddingTop = snappedTopInset();
			double paddingBottom = snappedBottomInset();
			double tmpPrefHeight = snapSize(tabText.prefHeight(width));

			if (tmpPrefHeight > maxHeight) {
				tmpPrefHeight = maxHeight;
			} else if (tmpPrefHeight < minHeight) {
				tmpPrefHeight = minHeight;
			}
			tmpPrefHeight += paddingTop + paddingBottom;
			return tmpPrefHeight;
		}

		@Override
		protected void layoutChildren() {
			double w = (snapSize(getWidth()) - snappedRightInset() - snappedLeftInset()) * animationTransition.getValue();
			rippler.resize(w, snapSize(getHeight()) - snappedTopInset() - snappedBottomInset());
			rippler.relocate(snappedLeftInset(), snappedTopInset());
		}

		@Override
		protected void setWidth(double value) {
			super.setWidth(value);
		}

		@Override
		protected void setHeight(double value) {
			super.setHeight(value);
		}

	} /* End TabHeaderSkin */

	private static final PseudoClass SELECTED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("selected");
	private static final PseudoClass TOP_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("top");
	private static final PseudoClass BOTTOM_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("bottom");
	private static final PseudoClass LEFT_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("left");
	private static final PseudoClass RIGHT_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("right");
	private static final PseudoClass DISABLED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("disabled");

	private AnchorPane tabsContainer;

	private AnchorPane tabsContainerHolder;

	/**************************************************************************
	 *
	 * TabContentRegion: each tab has one to contain the tab's content node
	 *
	 **************************************************************************/
	class TabContentRegion extends StackPane {

		private Tab tab;

		private InvalidationListener tabContentListener = valueModel -> {
			updateContent();
		};
		private InvalidationListener tabSelectedListener = new InvalidationListener() {
			@Override
			public void invalidated(Observable valueModel) {
				setVisible(tab.isSelected());
			}
		};

		private WeakInvalidationListener weakTabContentListener = new WeakInvalidationListener(tabContentListener);
		private WeakInvalidationListener weakTabSelectedListener = new WeakInvalidationListener(tabSelectedListener);

		public Tab getTab() {
			return tab;
		}

		public TabContentRegion(Tab tab) {
			getStyleClass().setAll("tab-content-area");
			setManaged(false);
			this.tab = tab;
			updateContent();
			setVisible(tab.isSelected());

			tab.selectedProperty().addListener(weakTabSelectedListener);
			tab.contentProperty().addListener(weakTabContentListener);
		}

		private void updateContent() {
			Node newContent = getTab().getContent();
			if (newContent == null) {
				getChildren().clear();
			} else {
				getChildren().setAll(newContent);
			}
		}

		private void removeListeners(Tab tab) {
			tab.selectedProperty().removeListener(weakTabSelectedListener);
			tab.contentProperty().removeListener(weakTabContentListener);
		}

	} /* End TabContentRegion */

	private enum ArrowPosition {
		RIGHT, LEFT;
	}

	/**************************************************************************
	 *
	 * TabControlButtons: controls to manipulate tab interaction
	 *
	 **************************************************************************/
	class TabControlButtons extends StackPane {
		private StackPane inner;
		private Icon downArrowBtn;
		private boolean showControlButtons, isLeftArrow;
		private Timeline arrowAnimation;
		
		public TabControlButtons(ArrowPosition pos) {

			getStyleClass().setAll("control-buttons-tab");			

			isLeftArrow = pos == ArrowPosition.LEFT;

			downArrowBtn = new Icon(isLeftArrow ? "CHEVRON_LEFT" : "CHEVRON_RIGHT","1.5em",";","");
			downArrowBtn.setPadding(new Insets(7));
			downArrowBtn.getStyleClass().setAll("tab-down-button");
			downArrowBtn.setVisible(isShowTabsMenu());
			downArrowBtn.setCursor(Cursor.HAND);
			downArrowBtn.setTextFill(selectedTabText);
			
			DoubleProperty offsetProperty = new SimpleDoubleProperty(0);
			offsetProperty.addListener((o,oldVal,newVal)-> tabHeaderArea.setScrollOffset(newVal.doubleValue()));
			
			downArrowBtn.setOnMousePressed(press ->{
				offsetProperty.set(tabHeaderArea.getScrollOffset());	
				double offset = isLeftArrow ? tabHeaderArea.getScrollOffset() + tabHeaderArea.headersRegion.getWidth() : tabHeaderArea.getScrollOffset() - tabHeaderArea.headersRegion.getWidth();
				arrowAnimation = new Timeline(new KeyFrame(Duration.seconds(1), new KeyValue(offsetProperty, offset , Interpolator.LINEAR)));
				arrowAnimation.play();
			});
			
			downArrowBtn.setOnMouseReleased(release -> arrowAnimation.stop());
			

			JFXRippler arrowRippler = new JFXRippler(downArrowBtn,RipplerMask.CIRCLE,RipplerPos.BACK);
			arrowRippler.ripplerFillProperty().bind(downArrowBtn.textFillProperty());
			StackPane.setMargin(downArrowBtn, new Insets(0,0,0,isLeftArrow?-4:4));


			inner = new StackPane() {
				@Override
				protected double computePrefWidth(double height) {
					double pw;
					double maxArrowWidth = !isShowTabsMenu() ? 0 : snapSize(arrowRippler.prefWidth(getHeight()));
					pw = 0.0F;
					if (isShowTabsMenu()) {
						pw += maxArrowWidth;
					}
					if (pw > 0) {
						pw += snappedLeftInset() + snappedRightInset();
					}
					return pw;
				}

				@Override
				protected double computePrefHeight(double width) {
					double height = 0.0F;
					if (isShowTabsMenu()) {
						height = Math.max(height, snapSize(arrowRippler.prefHeight(width)));
					}
					if (height > 0) {
						height += snappedTopInset() + snappedBottomInset();
					}
					return height;
				}

				@Override
				protected void layoutChildren() {
					if (isShowTabsMenu()) {
						double x = 0;
						double y = snappedTopInset();
						double w = snapSize(getWidth()) - x + snappedLeftInset();
						double h = snapSize(getHeight()) - y + snappedBottomInset();
						positionArrow(arrowRippler, x, y, w, h);
					}
				}

				private void positionArrow(JFXRippler btn, double x, double y, double width, double height) {
					btn.resize(width, height);
					positionInArea(btn, x, y, width, height, 0, HPos.CENTER, VPos.CENTER);
				}
			};
			inner.getStyleClass().add("container");
			inner.getChildren().add(arrowRippler);
			StackPane.setMargin(arrowRippler, new Insets(0,5,0,5));
			getChildren().add(inner);

			showControlButtons = false;
			if (isShowTabsMenu()) {
				showControlButtons = true;
				requestLayout();
			}
		}

		private boolean showTabsMenu = false;

		private void showTabsMenu(boolean value) {
			final boolean wasTabsMenuShowing = isShowTabsMenu();
			this.showTabsMenu = value;

			if (showTabsMenu && !wasTabsMenuShowing) {
				downArrowBtn.setVisible(true);
				showControlButtons = true;
				inner.requestLayout();
				tabHeaderArea.requestLayout();
			} else if (!showTabsMenu && wasTabsMenuShowing) {
				hideControlButtons();
			}
		}

		private boolean isShowTabsMenu() {
			return showTabsMenu;
		}

		@Override
		protected double computePrefWidth(double height) {
			double pw = snapSize(inner.prefWidth(height));
			if (pw > 0) {
				pw += snappedLeftInset() + snappedRightInset();
			}
			return pw;
		}

		@Override
		protected double computePrefHeight(double width) {
			return Math.max(getSkinnable().getTabMinHeight(), snapSize(inner.prefHeight(width))) + snappedTopInset() + snappedBottomInset();
		}

		@Override
		protected void layoutChildren() {
			double x = snappedLeftInset();
			double y = snappedTopInset();
			double w = snapSize(getWidth()) - x + snappedRightInset();
			double h = snapSize(getHeight()) - y + snappedBottomInset();

			if (showControlButtons) {
				showControlButtons();
				showControlButtons = false;
			}

			inner.resize(w, h);
			positionInArea(inner, x, y, w, h, 0, HPos.CENTER, VPos.BOTTOM);
		}

		private void showControlButtons() {
			setVisible(true);
		}

		private void hideControlButtons() {
			// If the scroll arrows or tab menu is still visible we don't want
			// to hide it animate it back it.
			if (isShowTabsMenu()) {
				showControlButtons = true;
			} else {
				setVisible(false);
			}

			// This needs to be called when we are in the left tabPosition
			// to allow for the clip offset to move properly (otherwise
			// it jumps too early - before the animation is done).
			requestLayout();
		}

	} /* End TabControlButtons*/

	class TabMenuItem extends RadioMenuItem {
		Tab tab;

		private InvalidationListener disableListener = new InvalidationListener() {
			@Override
			public void invalidated(Observable o) {
				setDisable(tab.isDisable());
			}
		};

		private WeakInvalidationListener weakDisableListener = new WeakInvalidationListener(disableListener);

		public TabMenuItem(final Tab tab) {
			super(tab.getText(), JFXTabPaneSkin.clone(tab.getGraphic()));
			this.tab = tab;
			setDisable(tab.isDisable());
			tab.disableProperty().addListener(weakDisableListener);
		}

		public Tab getTab() {
			return tab;
		}

		public void dispose() {
			tab.disableProperty().removeListener(weakDisableListener);
		}
	}

}
