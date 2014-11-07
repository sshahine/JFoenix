package com.aquafx_project.controls.skin.rt21682;

/*
 * Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */


import javafx.application.ConditionalFeature;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.StyleableProperty;
import javafx.css.StyleableObjectProperty;
import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.css.converters.EnumConverter;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Callback;
import javafx.util.Duration;

import com.sun.javafx.scene.control.MultiplePropertyChangeListenerHandler;
import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.TraversalEngine;
import com.sun.javafx.scene.traversal.TraverseListener;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.css.Styleable;
import javafx.scene.input.*;

public class TabPaneSkinHack extends BehaviorSkinBase<TabPane, TabPaneBehavior> {
    private static enum TabAnimation {
        NONE,
        GROW
        // In future we could add FADE, ...
    }
    
    private ObjectProperty<TabAnimation> openTabAnimation = new StyleableObjectProperty<TabAnimation>(TabAnimation.GROW) {
        @Override public CssMetaData<TabPane,TabAnimation> getCssMetaData() {
            return StyleableProperties.OPEN_TAB_ANIMATION;
        }
        
        @Override public Object getBean() {
            return TabPaneSkinHack.this;
        }

        @Override public String getName() {
            return "openTabAnimation";
        }
    };
    
    private ObjectProperty<TabAnimation> closeTabAnimation = new StyleableObjectProperty<TabAnimation>(TabAnimation.GROW) {
        @Override public CssMetaData<TabPane,TabAnimation> getCssMetaData() {
            return StyleableProperties.CLOSE_TAB_ANIMATION;
        }

        @Override public Object getBean() {
            return TabPaneSkinHack.this;
        }

        @Override public String getName() {
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
            Label l = (Label)n;
            Label label = new Label(l.getText(), l.getGraphic());
            return label;
        }
        return null;
    }
    private static final double ANIMATION_SPEED = 300;
    private static final int SPACER = 10;

    private TabHeaderArea tabHeaderArea;
    private ObservableList<TabContentRegion> tabContentRegions;
    private Rectangle clipRect;
    private Rectangle tabHeaderAreaClipRect;
    boolean focusTraversable = true;
    private Tab selectedTab;
    private Tab previousSelectedTab;
    private boolean isSelectingTab;

    public TabPaneSkinHack(TabPane tabPane) {
        super(tabPane, new TabPaneBehavior(tabPane));

        clipRect = new Rectangle(tabPane.getWidth(), tabPane.getHeight());
        getSkinnable().setClip(clipRect);

        tabContentRegions = FXCollections.<TabContentRegion>observableArrayList();

        for (Tab tab : getSkinnable().getTabs()) {
            addTabContent(tab);
        }

        tabHeaderAreaClipRect = new Rectangle();
        tabHeaderArea = new TabHeaderArea();
        tabHeaderArea.setClip(tabHeaderAreaClipRect);
        getChildren().add(tabHeaderArea);
        if (getSkinnable().getTabs().size() == 0) {
            tabHeaderArea.setVisible(false);
        }

        initializeTabListener();

        registerChangeListener(tabPane.getSelectionModel().selectedItemProperty(), "SELECTED_TAB");
        registerChangeListener(tabPane.sideProperty(), "SIDE");
        registerChangeListener(tabPane.widthProperty(), "WIDTH");
        registerChangeListener(tabPane.heightProperty(), "HEIGHT");

        previousSelectedTab = null;        
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
    }

    public StackPane getSelectedTabContentRegion() {
        for (TabContentRegion contentRegion : tabContentRegions) {
            if (contentRegion.getTab().equals(selectedTab)) {
                return contentRegion;
            }
        }
        return null;
    }

    @Override protected void handleControlPropertyChanged(String property) {
        super.handleControlPropertyChanged(property);
        if ("SELECTED_TAB".equals(property)) {            
            isSelectingTab = true;
            previousSelectedTab = selectedTab;
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
    private void removeTabs(List<? extends Tab> removedList, final Map<Tab, Timeline> closedTab) {
        for (final Tab tab : removedList) {
            // Animate the tab removal
            final TabHeaderSkin tabRegion = tabHeaderArea.getTabHeaderSkin(tab);
            Timeline closedTabTimeline = null;
            if (tabRegion != null) {
                if( closeTabAnimation.get() == TabAnimation.GROW ) {
                    tabRegion.animating = true;
                    closedTabTimeline = createTimeline(tabRegion, Duration.millis(ANIMATION_SPEED * 1.5F), 0.0F, new EventHandler<ActionEvent>() {
                        @Override public void handle(ActionEvent event) {      
                            handleClosedTab(tab, closedTab);
                        }
                    });
                    closedTabTimeline.play();    
                } else {
                    handleClosedTab(tab, closedTab);
                }
            }
            closedTab.put(tab, closedTabTimeline);
        }
    }
    
    private void handleClosedTab(Tab tab, Map<Tab, Timeline> closedTab) {
        removeTab(tab);
        closedTab.remove(tab);
        if (getSkinnable().getTabs().isEmpty()) {
            tabHeaderArea.setVisible(false);
        }
    }
    private void addTabs(List<? extends Tab> addedList, int from, boolean handle, final Map<Tab, Timeline> closedTab) {
        int i = 0;
        for (final Tab tab : addedList) {
            // Handle the case where we are removing and adding the same tab.
            Timeline closedTabTimeline = closedTab.get(tab);
            if (closedTabTimeline != null) {
                closedTabTimeline.stop();
                Iterator<Tab> keys = closedTab.keySet().iterator();
                while (keys.hasNext()) {
                    Tab key = keys.next();
                    if (tab.equals(key)) {
                        removeTab(key);
                        keys.remove();                                    
                    }
                }
            }
            // A new tab was added - animate it out
            if (!tabHeaderArea.isVisible()) {
                tabHeaderArea.setVisible(true);
            }
            int index = from + i++;
            tabHeaderArea.addTab(tab, index, false);
            addTabContent(tab);
            final TabHeaderSkin tabRegion = tabHeaderArea.getTabHeaderSkin(tab);
            if (tabRegion != null) {
                if( openTabAnimation.get() == TabAnimation.GROW ) {
                    tabRegion.animateNewTab = new Runnable() {
                        @Override public void run() {
                            final double w = snapSize(tabRegion.prefWidth(-1));
                            tabRegion.animating = true;
                            tabRegion.prefWidth.set(0.0);
                            tabRegion.setVisible(true);
                            createTimeline(tabRegion, Duration.millis(ANIMATION_SPEED), w, new EventHandler<ActionEvent>() {
                                @Override public void handle(ActionEvent event) {
                                    tabRegion.animating = false;
                                    tabRegion.inner.requestLayout();
                                }
                            }).play();
                        }
                    };    
                } else {
                    tabRegion.setVisible(true);
                    tabRegion.inner.requestLayout();
                }
            }
        }
    }
    
    private void initializeTabListener() {
        final Map<Tab, Timeline> closedTab = new HashMap<Tab, Timeline>();
        getSkinnable().getTabs().addListener(new ListChangeListener<Tab>() {
            @Override public void onChanged(final Change<? extends Tab> c) {      
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
                        removeTabs(permutatedTabs, closedTab);
                        addTabs(permutatedTabs, c.getFrom(), false, closedTab);
                        openTabAnimation.set(prevOpenAnimation);
                        closeTabAnimation.set(prevCloseAnimation);
                        getSkinnable().getSelectionModel().select(selTab);
                    }
                    if (c.getRemovedSize() > 0) {
                        removeTabs(c.getRemoved(), closedTab);
                    }
                    if (c.getAddedSize() > 0) {
                        addTabs(c.getAddedSubList(), c.getFrom(), true, closedTab);
                    }
                }
            }
        });
    }

    private void addTabContent(Tab tab) {
        TabContentRegion tabContentRegion = new TabContentRegion(tab);
        tabContentRegion.setClip(new Rectangle());
        tabContentRegions.add(tabContentRegion);
        // We want the tab content to always sit below the tab headers
        getChildren().add(0, tabContentRegion);
    }

    private void removeTabContent(Tab tab) {
        for (TabContentRegion contentRegion : tabContentRegions) {
            if (contentRegion.getTab().equals(tab)) {
                contentRegion.removeListeners(tab);
                getChildren().remove(contentRegion);
                tabContentRegions.remove(contentRegion);
                break;
            }
        }
    }

    private void removeTab(Tab tab) {
        final TabHeaderSkin tabRegion = tabHeaderArea.getTabHeaderSkin(tab);
        tabHeaderArea.removeTab(tab);
        removeTabContent(tab);
        tabRegion.animating = false;
        tabHeaderArea.requestLayout();
        tab = null;
    }

    private void updateTabPosition() {
        tabHeaderArea.setScrollOffset(0.0F);
        getSkinnable().impl_reapplyCSS();
        getSkinnable().requestLayout();
    }

    private Timeline createTimeline(final TabHeaderSkin tabRegion, final Duration duration, final double endValue, final EventHandler<ActionEvent> func) {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(1);

        KeyValue keyValue = new KeyValue(tabRegion.prefWidth, endValue, Interpolator.LINEAR);

        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().add(new KeyFrame(duration, func, keyValue));
        return timeline;
    }

    private boolean isHorizontal() {
        Side tabPosition = getSkinnable().getSide();
        return Side.TOP.equals(tabPosition) || Side.BOTTOM.equals(tabPosition);
    }

    private void initializeSwipeHandlers() {
        if (PlatformImpl.isSupported(ConditionalFeature.INPUT_TOUCH)) {
            getSkinnable().setOnSwipeLeft(new EventHandler<SwipeEvent>() {
                @Override public void handle(SwipeEvent t) {
                    getBehavior().selectNextTab();
                }
            });

            getSkinnable().setOnSwipeRight(new EventHandler<SwipeEvent>() {
                @Override public void handle(SwipeEvent t) {
                    getBehavior().selectPreviousTab();
                }
            });        
        }    
    }
    
    //TODO need to cache this.
    private boolean isFloatingStyleClass() {
        return getSkinnable().getStyleClass().contains(TabPane.STYLE_CLASS_FLOATING);
    }

//    @Override protected void setWidth(double value) {
//        super.setWidth(value);
//        clipRect.setWidth(value);
//    }
//
//    @Override protected void setHeight(double value) {
//        super.setHeight(value);
//        clipRect.setHeight(value);
//    }

    private double maxw = 0.0d;
    @Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        // The TabPane can only be as wide as it widest content width.
        for (TabContentRegion contentRegion: tabContentRegions) {
             maxw = Math.max(maxw, snapSize(contentRegion.prefWidth(-1)));
        }
        double prefwidth = isHorizontal() ?
            Math.max(maxw, snapSize(tabHeaderArea.prefWidth(-1))) : 
                maxw + snapSize(tabHeaderArea.prefWidth(-1));
        return snapSize(prefwidth) + rightInset + leftInset;
    }

    private double maxh = 0.0d;
    @Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        // The TabPane can only be as high as it highest content height.
        for (TabContentRegion contentRegion: tabContentRegions) {
             maxh = Math.max(maxh, snapSize(contentRegion.prefHeight(-1)));
        }
        double prefheight = isHorizontal()?
             maxh + snapSize(tabHeaderArea.prefHeight(-1)) : 
                Math.max(maxh, snapSize(tabHeaderArea.prefHeight(-1)));
        return snapSize(prefheight) + topInset + bottomInset;
    }

    @Override public double computeBaselineOffset(double topInset, double rightInset, double bottomInset, double leftInset) {
        return tabHeaderArea.getBaselineOffset() + tabHeaderArea.getLayoutY();
    }

    @Override protected void layoutChildren(final double x, final double y,
            final double w, final double h) {
        TabPane tabPane = getSkinnable();
        Side tabPosition = tabPane.getSide();

        double headerHeight = snapSize(tabHeaderArea.prefHeight(-1));
        double tabsStartX = tabPosition.equals(Side.RIGHT)? x + w - headerHeight : x;
        double tabsStartY = tabPosition.equals(Side.BOTTOM)? y + h - headerHeight : y;

        if (tabPosition.equals(tabPosition.TOP)) {
            tabHeaderArea.resize(w, headerHeight);
            tabHeaderArea.relocate(tabsStartX, tabsStartY);
            tabHeaderArea.getTransforms().clear();
            tabHeaderArea.getTransforms().add(new Rotate(getRotation(tabPosition.TOP)));
        } else if (tabPosition.equals(tabPosition.BOTTOM)) {
            tabHeaderArea.resize(w, headerHeight);
            tabHeaderArea.relocate(w, tabsStartY - headerHeight);
            tabHeaderArea.getTransforms().clear();
            tabHeaderArea.getTransforms().add(new Rotate(getRotation(tabPosition.BOTTOM), 0, headerHeight));
        } else if (tabPosition.equals(tabPosition.LEFT)) {
            tabHeaderArea.resize(h, headerHeight);
            tabHeaderArea.relocate(tabsStartX + headerHeight, h - headerHeight);
            tabHeaderArea.getTransforms().clear();
            tabHeaderArea.getTransforms().add(new Rotate(getRotation(tabPosition.LEFT), 0, headerHeight));
        } else if (tabPosition.equals(tabPosition.RIGHT)) {
            tabHeaderArea.resize(h, headerHeight);
            tabHeaderArea.relocate(tabsStartX, y - headerHeight);
            tabHeaderArea.getTransforms().clear();
            tabHeaderArea.getTransforms().add(new Rotate(getRotation(tabPosition.RIGHT), 0, headerHeight));
        }

        tabHeaderAreaClipRect.setX(0);
        tabHeaderAreaClipRect.setY(0);
        if (isHorizontal()) {
            tabHeaderAreaClipRect.setWidth(w);
        } else {
            tabHeaderAreaClipRect.setWidth(h);
        }
        tabHeaderAreaClipRect.setHeight(headerHeight);

        // ==================================
        // position the tab content for the selected tab only
        // ==================================
        // if the tabs are on the left, the content needs to be indented
        double contentStartX = 0;
        double contentStartY = 0;

        if (tabPosition.equals(tabPosition.TOP)) {
            contentStartX = x;
            contentStartY = y + headerHeight;
            if (isFloatingStyleClass()) {
                // This is to hide the top border content
                contentStartY -= 1;
            }
        } else if (tabPosition.equals(tabPosition.BOTTOM)) {
            contentStartX = x;
            contentStartY = y;
            if (isFloatingStyleClass()) {
                // This is to hide the bottom border content
                contentStartY = 1;
            }
        } else if (tabPosition.equals(tabPosition.LEFT)) {
            contentStartX = x + headerHeight;
            contentStartY = y;
            if (isFloatingStyleClass()) {
                // This is to hide the left border content
                contentStartX -= 1;
            }
        } else if (tabPosition.equals(tabPosition.RIGHT)) {
            contentStartX = x;
            contentStartY = y;
            if (isFloatingStyleClass()) {
                // This is to hide the right border content
                contentStartX = 1;
            }
        }

        double contentWidth = w - (isHorizontal() ? 0 : headerHeight);
        double contentHeight = h - (isHorizontal() ? headerHeight: 0);
        
        for (int i = 0, max = tabContentRegions.size(); i < max; i++) {
            TabContentRegion tabContent = tabContentRegions.get(i);
            
            tabContent.setAlignment(Pos.TOP_LEFT);
            if (tabContent.getClip() != null) {
                ((Rectangle)tabContent.getClip()).setWidth(contentWidth);
                ((Rectangle)tabContent.getClip()).setHeight(contentHeight);
            }
            
            // we need to size all tabs, even if they aren't visible. For example,
            // see RT-29167
            tabContent.resize(contentWidth, contentHeight);
            tabContent.relocate(contentStartX, contentStartY);
            
            Node content = tabContent.getTab().getContent();
            if (content != null) {
                content.setVisible(tabContent.getTab().equals(selectedTab));
            }
        }
    }
    
    
   /**
    * Super-lazy instantiation pattern from Bill Pugh.
    * @treatAsPrivate implementation detail
    */
   private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        
        private final static CssMetaData<TabPane,TabAnimation> OPEN_TAB_ANIMATION = 
                new CssMetaData<TabPane, TabPaneSkinHack.TabAnimation>("-fx-open-tab-animation", 
                    new EnumConverter<TabAnimation>(TabAnimation.class), TabAnimation.GROW) {

            @Override public boolean isSettable(TabPane node) {
                return true;
            }

            @Override public StyleableProperty<TabAnimation> getStyleableProperty(TabPane node) {
                TabPaneSkinHack skin = (TabPaneSkinHack) node.getSkin();
                return (StyleableProperty<TabAnimation>)skin.openTabAnimation;
            }
        };
        
        private final static CssMetaData<TabPane,TabAnimation> CLOSE_TAB_ANIMATION = 
                new CssMetaData<TabPane, TabPaneSkinHack.TabAnimation>("-fx-close-tab-animation", 
                    new EnumConverter<TabAnimation>(TabAnimation.class), TabAnimation.GROW) {

            @Override public boolean isSettable(TabPane node) {
                return true;
            }

            @Override public StyleableProperty<TabAnimation> getStyleableProperty(TabPane node) {
                TabPaneSkinHack skin = (TabPaneSkinHack) node.getSkin();
                return (StyleableProperty<TabAnimation>)skin.closeTabAnimation;
            }
        };
        
        static {

           final List<CssMetaData<? extends Styleable, ?>> styleables = 
               new ArrayList<CssMetaData<? extends Styleable, ?>>(SkinBase.getClassCssMetaData());
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
    @Override public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
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
        private TabControlButtons controlButtons;

        // -- drag support for tabs
        private double lastDragPos;

        // + headersRegion.padding.top + headersRegion.padding.bottom;
        private double scrollOffset;
        public double getScrollOffset() {
            return scrollOffset;
        }
        public void setScrollOffset(double value) {
            scrollOffset = value;
            headersRegion.requestLayout();
        }
        private Point2D dragAnchor;
        public TabHeaderArea() {
            getStyleClass().setAll("tab-header-area");
            setManaged(false);
            final TabPane tabPane = getSkinnable();

            headerClip = new Rectangle();
            
            headersRegion = new StackPane() {
                @Override protected double computePrefWidth(double height) {
                    double width = 0.0F;
                    for (Node child : getChildren()) {
                        TabHeaderSkin tabHeaderSkin = (TabHeaderSkin)child;
                        if (tabHeaderSkin.isVisible()) {
                            width += tabHeaderSkin.prefWidth(height);
                        }
                    }
                    return snapSize(width) + snappedLeftInset() + snappedRightInset();
                }

                @Override protected double computePrefHeight(double width) {
                    double height = 0.0F;
                    for (Node child : getChildren()) {
                        TabHeaderSkin tabHeaderSkin = (TabHeaderSkin)child;
                        height = Math.max(height, tabHeaderSkin.prefHeight(width));
                    }
                    return snapSize(height) + snappedTopInset() + snappedBottomInset();
                }

                @Override protected void layoutChildren() {
                    if (tabsFit()) {
                        setScrollOffset(0.0);
                    } else {
                        if (!removeTab.isEmpty()) {                            
                            double offset = 0;
                            double w = tabHeaderArea.getWidth() - snapSize(controlButtons.prefWidth(-1)) - firstTabIndent() - SPACER;
                            Iterator i = getChildren().iterator();
                            while (i.hasNext()) {
                                TabHeaderSkin tabHeader = (TabHeaderSkin)i.next();
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
                        } else {
                            isSelectingTab = true;
                        }
                    }

                    if (isSelectingTab) {
                        double offset = 0;
                        double selectedTabOffset = 0;
                        double selectedTabWidth = 0;
                        double previousSelectedTabOffset = 0;
                        double previousSelectedTabWidth = 0;
                        for (Node node: getChildren()) {
                            TabHeaderSkin tabHeader = (TabHeaderSkin)node;
                            // size and position the header relative to the other headers
                            double tabHeaderPrefWidth = snapSize(tabHeader.prefWidth(-1));
                            if (selectedTab != null && selectedTab.equals(tabHeader.getTab())) {
                                selectedTabOffset = offset;
                                selectedTabWidth = tabHeaderPrefWidth;
                            }
                            if (previousSelectedTab != null && previousSelectedTab.equals(tabHeader.getTab())) {
                                previousSelectedTabOffset = offset;
                                previousSelectedTabWidth = tabHeaderPrefWidth;
                            }
                            offset+=tabHeaderPrefWidth;
                        }
                        if (selectedTabOffset > previousSelectedTabOffset) {
                            scrollToSelectedTab(selectedTabOffset + selectedTabWidth, previousSelectedTabOffset);
                        } else {
                            scrollToSelectedTab(selectedTabOffset, previousSelectedTabOffset);
                        }
                        isSelectingTab = false;
                    }

                    Side tabPosition = getSkinnable().getSide();
                    double tabBackgroundHeight = snapSize(prefHeight(-1));
                    double tabX = (tabPosition.equals(Side.LEFT) || tabPosition.equals(Side.BOTTOM)) ?
                        snapSize(getWidth()) - getScrollOffset() : getScrollOffset();

                    updateHeaderClip();
                    for (Node node : getChildren()) {
                        TabHeaderSkin tabHeader = (TabHeaderSkin)node;
                        // size and position the header relative to the other headers
                        double tabHeaderPrefWidth = snapSize(tabHeader.prefWidth(-1));
                        double tabHeaderPrefHeight = snapSize(tabHeader.prefHeight(-1));
                        tabHeader.resize(tabHeaderPrefWidth, tabHeaderPrefHeight);
                        // This ensures that the tabs are located in the correct position
                        // when there are tabs of differing heights.
                        double startY = tabPosition.equals(Side.BOTTOM) ?
                            0 : tabBackgroundHeight - tabHeaderPrefHeight - snappedBottomInset();
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
            };
            headersRegion.getStyleClass().setAll("headers-region");
            headersRegion.setClip(headerClip);

            headerBackground = new StackPane();
            headerBackground.getStyleClass().setAll("tab-header-background");

            int i = 0;
            for (Tab tab: tabPane.getTabs()) {
                addTab(tab, i++, true);
            }

            controlButtons = new TabControlButtons();
            controlButtons.setVisible(false);
            if (controlButtons.isVisible()) {
                controlButtons.setVisible(true);
            }
            getChildren().addAll(headerBackground, headersRegion, controlButtons);

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
            double controlButtonPrefWidth = snapSize(controlButtons.prefWidth(-1));
            double headersPrefWidth = snapSize(headersRegion.prefWidth(-1));
            double headersPrefHeight = snapSize(headersRegion.prefHeight(-1));

            // Add the spacer if isShowTabsMenu is true.
            if (controlButtonPrefWidth > 0) {
                controlButtonPrefWidth = controlButtonPrefWidth + SPACER;
            }

            if (headersRegion.getEffect() instanceof DropShadow) {
                DropShadow shadow = (DropShadow)headersRegion.getEffect();
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
            headerClip.setWidth(clipWidth);
            headerClip.setHeight(clipHeight);
        }

        private void addTab(Tab tab, int addToIndex, boolean visible) {
            TabHeaderSkin tabHeaderSkin = new TabHeaderSkin(tab);
            tabHeaderSkin.setVisible(visible);
            headersRegion.getChildren().add(addToIndex, tabHeaderSkin);
        }

        private List<TabHeaderSkin> removeTab = new ArrayList();
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
            for (Node child: headersRegion.getChildren()) {
                TabHeaderSkin tabHeaderSkin = (TabHeaderSkin)child;
                if (tabHeaderSkin.getTab().equals(tab)) {
                    return tabHeaderSkin;
                }
            }
            return null;
        }

        // ----- Code for scrolling the tab header area based on the user clicking
        // the left/right arrows on the control buttons tab
        private Timeline scroller;

        private void createScrollTimeline(final double val) {
            scroll(val);
            scroller = new Timeline();
            scroller.setCycleCount(Timeline.INDEFINITE);
            scroller.getKeyFrames().add(new KeyFrame(Duration.millis(150), new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent event) {
                    scroll(val);
                }
            }));
        }

        // ----- End of control button scrolling support
        private void scroll(double d) {
            if (tabsFit()) {
                return;
            }
            Side tabPosition = getSkinnable().getSide();
            double headerPrefWidth = snapSize(headersRegion.prefWidth(-1));
            double controlTabWidth = snapSize(controlButtons.prefWidth(-1));
            double max = getWidth() - headerPrefWidth - controlTabWidth;
            double delta = tabPosition.equals(Side.LEFT) || tabPosition.equals(Side.BOTTOM) ? -d : d;
            double newOffset = getScrollOffset() + delta;
            setScrollOffset(newOffset >= 0 ? 0.0F : (newOffset <= max ? max : newOffset));
        }

        private boolean tabsFit() {
            double headerPrefWidth = snapSize(headersRegion.prefWidth(-1));
            double controlTabWidth = snapSize(controlButtons.prefWidth(-1));
            double visibleWidth = headerPrefWidth + controlTabWidth + firstTabIndent() + SPACER;
            return visibleWidth < getWidth();
        }

        private void scrollToSelectedTab(double selected, double previous) {
            if (selected > previous) {
                // needs to scroll to the left
                double distance = selected - previous;
                double offset = (previous + getScrollOffset()) + distance;
                double width = snapSize(getWidth()) - snapSize(controlButtons.prefWidth(-1)) - firstTabIndent() - SPACER;
                if (offset > width) {
                    setScrollOffset(getScrollOffset() -(offset - width));
                }
            } else {
                // needs to scroll to the right
                double offset = selected + getScrollOffset();
                if (offset < 0) {
                    setScrollOffset(getScrollOffset() - offset);
                }
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

        @Override protected double computePrefWidth(double height) {
            double padding = isHorizontal() ?
                snappedLeftInset() + snappedRightInset() :
                snappedTopInset() + snappedBottomInset();
            return snapSize(headersRegion.prefWidth(-1)) + controlButtons.prefWidth(-1) + 
                    firstTabIndent() + SPACER + padding;
        }

        @Override protected double computePrefHeight(double width) {
            double padding = isHorizontal() ?
                snappedTopInset() + snappedBottomInset() :
                snappedLeftInset() + snappedRightInset();
            return snapSize(headersRegion.prefHeight(-1)) + padding;
        }

        @Override public double getBaselineOffset() {
            return headersRegion.getBaselineOffset() + headersRegion.getLayoutY();
        }

        @Override protected void layoutChildren() {
            HPos alignment = HPos.CENTER; //TODO: read alignment-property

            final double leftInset = snappedLeftInset();
            final double rightInset = snappedRightInset();
            final double topInset = snappedTopInset();
            final double bottomInset = snappedBottomInset();
            double w = snapSize(getWidth()) - (isHorizontal() ?
                    leftInset + rightInset : topInset + bottomInset);
            double h = snapSize(getHeight()) - (isHorizontal() ?
                    topInset + bottomInset : leftInset + rightInset);
            double tabBackgroundHeight = snapSize(prefHeight(-1));
            double headersPrefWidth = snapSize(headersRegion.prefWidth(-1));
            double headersPrefHeight = snapSize(headersRegion.prefHeight(-1));
            
            if (tabsFit()) {
                controlButtons.showTabsMenu(false);
            } else {
                controlButtons.showTabsMenu(true);
            }
            
            updateHeaderClip();

            // RESIZE CONTROL BUTTONS
            double btnWidth = snapSize(controlButtons.prefWidth(-1));
            controlButtons.resize(btnWidth, controlButtons.getControlTabHeight());
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
                if (getWidth() > headersPrefWidth + leftInset || alignment == HPos.LEFT) {
                    startX = snapSize(leftInset);
                } else if (alignment == HPos.CENTER) {
                    startX = snapSize(leftInset * 2 + (headersPrefWidth - getWidth()) / 2);
                } else if (alignment == HPos.RIGHT) {
                    startX = snapSize(leftInset * 3 + (headersPrefWidth - getWidth()));
                }
                startY = tabBackgroundHeight - headersPrefHeight - snapSize(bottomInset);
                controlStartX = w - btnWidth + snapSize(leftInset);
                controlStartY = snapSize(getHeight()) - controlButtons.getControlTabHeight() - snapSize(bottomInset);
            } else if (tabPosition.equals(Side.RIGHT)) {
                if (getWidth() > headersPrefWidth + topInset || alignment == HPos.LEFT) {
                    startX = snapSize(topInset);
                } else if (alignment == HPos.RIGHT) {
                    startX = snapSize(topInset * 3 + (headersPrefWidth - getWidth()));
                } else if (alignment == HPos.CENTER) {
                    startX = snapSize(topInset * 2 + (headersPrefWidth - getWidth()) / 2);
                }
                startY = tabBackgroundHeight - headersPrefHeight - snapSize(leftInset);
                controlStartX = w - btnWidth + snapSize(topInset);
                controlStartY = snapSize(getHeight()) - controlButtons.getControlTabHeight() - snapSize(leftInset);
            } else if (tabPosition.equals(Side.BOTTOM)) {
                if (getWidth() > headersPrefWidth + leftInset || alignment == HPos.RIGHT) {
                    startX = snapSize(leftInset);
                } else if (alignment == HPos.CENTER) {
                    startX = snapSize(leftInset / 3 - (headersPrefWidth - getWidth()) / 2);
                } else if (alignment == HPos.LEFT) {
                    startX = snapSize(getWidth() - leftInset - headersPrefWidth);
                }
                startY = tabBackgroundHeight - headersPrefHeight - snapSize(topInset);
                controlStartX = snapSize(rightInset);
                controlStartY = snapSize(getHeight()) - controlButtons.getControlTabHeight() - snapSize(topInset);
            } else if (tabPosition.equals(Side.LEFT)) {
                if (getWidth() > headersPrefWidth + topInset || alignment == HPos.RIGHT) {
                    startX = snapSize(topInset);
                } else if(alignment == HPos.LEFT){
                    startX = snapSize(getWidth()) - headersPrefWidth - snapSize(getInsets().getTop());
                } else if(alignment == HPos.CENTER ) {
                    startX = snapSize(topInset / 3 - (headersPrefWidth - getWidth()) / 2);
                }
                startY = tabBackgroundHeight - headersPrefHeight - snapSize(rightInset);
                controlStartX = snapSize(topInset);
                controlStartY = snapSize(getHeight()) - controlButtons.getControlTabHeight() - snapSize(rightInset);
            }
            if (headerBackground.isVisible()) {
                positionInArea(headerBackground, 0, 0,
                        snapSize(getWidth()), snapSize(getHeight()), /*baseline ignored*/0, HPos.CENTER, VPos.CENTER);
            }
            positionInArea(headersRegion, startX, startY, w, h, /*baseline ignored*/0, alignment, VPos.CENTER);
            positionInArea(controlButtons, controlStartX, controlStartY, btnWidth, controlButtons.getControlTabHeight(),
                        /*baseline ignored*/0, HPos.CENTER, VPos.CENTER);
        }
    } /* End TabHeaderArea */

    static int CLOSE_BTN_SIZE = 16;

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
        private Label label;
        private StackPane closeBtn;
        private StackPane inner;
        private Tooltip oldTooltip;
        private Tooltip tooltip;
        private Rectangle clip;
        
        private MultiplePropertyChangeListenerHandler listener = 
                new MultiplePropertyChangeListenerHandler(new Callback<String, Void>() {
                    @Override public Void call(String param) {
                        handlePropertyChanged(param);
                        return null;
                    }
                });
        
        private final ListChangeListener<String> styleClassListener = new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                getStyleClass().setAll(tab.getStyleClass());
            }
        };
        
        private final WeakListChangeListener<String> weakStyleClassListener =
                new WeakListChangeListener<>(styleClassListener);

        public TabHeaderSkin(final Tab tab) {
            getStyleClass().setAll(tab.getStyleClass());
            setId(tab.getId());
            setStyle(tab.getStyle());

            this.tab = tab;
            clip = new Rectangle();
            setClip(clip);

            label = new Label(tab.getText(), tab.getGraphic());
            label.getStyleClass().setAll("tab-label");

            closeBtn = new StackPane() {
                @Override protected double computePrefWidth(double h) {
                    return CLOSE_BTN_SIZE;
                }
                @Override protected double computePrefHeight(double w) {
                    return CLOSE_BTN_SIZE;
                }
            };
            closeBtn.getStyleClass().setAll("tab-close-button");
            closeBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent me) {
                    Tab tab = getTab();
                    TabPaneBehavior behavior = getBehavior();
                    if (behavior.canCloseTab(tab)) {
                         removeListeners(tab);
                         behavior.closeTab(tab);
                         setOnMousePressed(null);    
                    }
                }
            });
            
            updateGraphicRotation();

            final int padding = 2;
            final Region focusIndicator = new Region();
            focusIndicator.setMouseTransparent(true);
            focusIndicator.getStyleClass().add("focus-indicator");
            
            inner = new StackPane() {
                @Override protected void layoutChildren() {
                    final TabPane skinnable = getSkinnable();
                    
                    final double paddingTop = snappedTopInset();
                    final double paddingRight = snappedRightInset();
                    final double paddingBottom = snappedBottomInset();
                    final double paddingLeft = snappedLeftInset();
                    final double w = getWidth() - (paddingLeft + paddingRight);
                    final double h = getHeight() - (paddingTop + paddingBottom);

                    final double prefLabelWidth = snapSize(label.prefWidth(-1));
                    final double prefLabelHeight = snapSize(label.prefHeight(-1));
                    
                    final double closeBtnWidth = showCloseButton() ? snapSize(closeBtn.prefWidth(-1)) : 0;
                    final double closeBtnHeight = showCloseButton() ? snapSize(closeBtn.prefHeight(-1)) : 0;
                    final double minWidth = snapSize(skinnable.getTabMinWidth());
                    final double maxWidth = snapSize(skinnable.getTabMaxWidth());
                    final double minHeight = snapSize(skinnable.getTabMinHeight());
                    final double maxHeight = snapSize(skinnable.getTabMaxHeight());

                    double labelAreaWidth = prefLabelWidth;
                    double labelAreaHeight = prefLabelHeight;
                    double labelWidth = prefLabelWidth;
                    double labelHeight = prefLabelHeight;
                    
                    final double childrenWidth = labelAreaWidth + closeBtnWidth;
                    final double childrenHeight = Math.max(labelAreaHeight, closeBtnHeight);
                    
                    if (childrenWidth > maxWidth && maxWidth != Double.MAX_VALUE) {
                        labelAreaWidth = maxWidth - closeBtnWidth;
                        labelWidth = maxWidth - closeBtnWidth;
                    } else if (childrenWidth < minWidth) {
                        labelAreaWidth = minWidth - closeBtnWidth;
                    }

                    if (childrenHeight > maxHeight && maxHeight != Double.MAX_VALUE) {
                        labelAreaHeight = maxHeight;
                        labelHeight = maxHeight;
                    } else if (childrenHeight < minHeight) {
                        labelAreaHeight = minHeight;
                    }

                    if (animating) {
                        if (prefWidth.getValue() < labelAreaWidth) {
                            labelAreaWidth = prefWidth.getValue();
                        }
                        closeBtn.setVisible(false);
                    } else {
                        closeBtn.setVisible(showCloseButton());
                    }
                    
                    
                    label.resize(labelWidth, labelHeight);
                    
                    
                    double labelStartX = paddingLeft;
                    
                    // If maxWidth is less than Double.MAX_VALUE, the user has 
                    // clamped the max width, but we should
                    // position the close button at the end of the tab, 
                    // which may not necessarily be the entire width of the
                    // provided max width.
                    double closeBtnStartX = (maxWidth < Double.MAX_VALUE ? Math.min(w, maxWidth) : w) - paddingRight - closeBtnWidth;
                    
                    positionInArea(label, labelStartX, paddingTop, labelAreaWidth, h,
                            /*baseline ignored*/0, HPos.CENTER, VPos.CENTER);

                    if (closeBtn.isVisible()) {
                        closeBtn.resize(closeBtnWidth, closeBtnHeight);
                        positionInArea(closeBtn, closeBtnStartX, paddingTop, closeBtnWidth, h,
                                /*baseline ignored*/0, HPos.CENTER, VPos.CENTER);
                    }
                    
                    focusIndicator.resizeRelocate(
                            label.getLayoutX() - padding, 
                            Math.min(label.getLayoutY(), closeBtn.isVisible() ? closeBtn.getLayoutY() : Double.MAX_VALUE) - padding, 
                            labelWidth + closeBtnWidth + padding * 2,
                            Math.max(labelHeight, closeBtnHeight) + padding*2);
                }
            };
            inner.getStyleClass().add("tab-container");
            inner.setRotate(getSkinnable().getSide().equals(Side.BOTTOM) ? 180.0F : 0.0F);
            inner.getChildren().addAll(label, closeBtn, focusIndicator);

            getChildren().addAll(inner);

            tooltip = tab.getTooltip();
            if (tooltip != null) {
                Tooltip.install(this, tooltip);
                oldTooltip = tooltip;
            }

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

            setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
                @Override public void handle(ContextMenuEvent me) {
                   if (getTab().getContextMenu() != null) {
                        getTab().getContextMenu().show(inner, me.getScreenX(), me.getScreenY());
                        me.consume();
                    }
                }
            });
            setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent me) {
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
                        getBehavior().selectTab(getTab());
                    }
                }
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
                label.setText(getTab().getText());
            } else if ("GRAPHIC".equals(p)) {
                label.setGraphic(getTab().getGraphic());
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
            if (label.getGraphic() != null) {
                label.getGraphic().setRotate(getSkinnable().isRotateGraphic() ? 0.0F :
                    (getSkinnable().getSide().equals(Side.RIGHT) ? -90.0F :
                        (getSkinnable().getSide().equals(Side.LEFT) ? 90.0F : 0.0F)));
            }
        }

        private boolean showCloseButton() {
            return tab.isClosable() &&
                    (getSkinnable().getTabClosingPolicy().equals(TabClosingPolicy.ALL_TABS) ||
                    getSkinnable().getTabClosingPolicy().equals(TabClosingPolicy.SELECTED_TAB) && tab.isSelected());
        }

        private final DoubleProperty prefWidth = new DoublePropertyBase() {
            @Override
            protected void invalidated() {
                requestLayout();
            }

            @Override
            public Object getBean() {
                return TabHeaderSkin.this;
            }

            @Override
            public String getName() {
                return "prefWidth";
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

        private boolean animating = false;

        @Override protected double computePrefWidth(double height) {
            if (animating) {
                return prefWidth.getValue();
            }
            double minWidth = snapSize(getSkinnable().getTabMinWidth());
            double maxWidth = snapSize(getSkinnable().getTabMaxWidth());
            double paddingRight = snappedRightInset();
            double paddingLeft = snappedLeftInset();
            double tmpPrefWidth = snapSize(label.prefWidth(-1));

            // only include the close button width if it is relevant
            if (showCloseButton()) {
                tmpPrefWidth += snapSize(closeBtn.prefWidth(-1));
            }

            if (tmpPrefWidth > maxWidth) {
                tmpPrefWidth = maxWidth;
            } else if (tmpPrefWidth < minWidth) {
                tmpPrefWidth = minWidth;
            }
            tmpPrefWidth += paddingRight + paddingLeft;
            prefWidth.setValue(tmpPrefWidth);
            return tmpPrefWidth;
        }

        @Override protected double computePrefHeight(double width) {
            double minHeight = snapSize(getSkinnable().getTabMinHeight());
            double maxHeight = snapSize(getSkinnable().getTabMaxHeight());
            double paddingTop = snappedTopInset();
            double paddingBottom = snappedBottomInset();
            double tmpPrefHeight = snapSize(label.prefHeight(width));

            if (tmpPrefHeight > maxHeight) {
                tmpPrefHeight = maxHeight;
            } else if (tmpPrefHeight < minHeight) {
                tmpPrefHeight = minHeight;
            }
            tmpPrefHeight += paddingTop + paddingBottom;
            return tmpPrefHeight;
        }

        private Runnable animateNewTab = null;

        @Override protected void layoutChildren() {
            inner.resize(snapSize(getWidth()) - snappedRightInset() - snappedLeftInset(),
                    snapSize(getHeight()) - snappedTopInset() - snappedBottomInset());
            inner.relocate(snappedLeftInset(), snappedTopInset());

            if (animateNewTab != null) {
                animateNewTab.run();
                animateNewTab = null;
            }
        }

        @Override protected void setWidth(double value) {
            super.setWidth(value);
            clip.setWidth(value);
        }

        @Override protected void setHeight(double value) {
            super.setHeight(value);
            clip.setHeight(value);
        }
    
    } /* End TabHeaderSkin */

    private static final PseudoClass SELECTED_PSEUDOCLASS_STATE =
            PseudoClass.getPseudoClass("selected");
    private static final PseudoClass TOP_PSEUDOCLASS_STATE =
            PseudoClass.getPseudoClass("top");
    private static final PseudoClass BOTTOM_PSEUDOCLASS_STATE =
            PseudoClass.getPseudoClass("bottom");
    private static final PseudoClass LEFT_PSEUDOCLASS_STATE =
            PseudoClass.getPseudoClass("left");
    private static final PseudoClass RIGHT_PSEUDOCLASS_STATE =
            PseudoClass.getPseudoClass("right");
    private static final PseudoClass DISABLED_PSEUDOCLASS_STATE =
            PseudoClass.getPseudoClass("disabled");    


    /**************************************************************************
     *
     * TabContentRegion: each tab has one to contain the tab's content node
     *
     **************************************************************************/
    class TabContentRegion extends StackPane implements TraverseListener {

        private TraversalEngine engine;
        private Direction direction = Direction.NEXT;
        //private Direction direction;
        private Tab tab;
        private InvalidationListener tabListener;

        public Tab getTab() {
            return tab;
        }

        public TabContentRegion(Tab tab) {
            getStyleClass().setAll("tab-content-area");
            setManaged(false);
            this.tab = tab;
            updateContent();

            tabListener = new InvalidationListener() {
                @Override public void invalidated(Observable valueModel) {
                    if (valueModel == getTab().selectedProperty()) {
                        setVisible(getTab().isSelected());
                    } else if (valueModel == getTab().contentProperty()) {
                        getChildren().clear();
                        updateContent();
                    }
                }
            };
            tab.selectedProperty().addListener(new InvalidationListener() {
                @Override public void invalidated(Observable valueModel) {
                    setVisible(getTab().isSelected());
                }
            });
            tab.contentProperty().addListener(new InvalidationListener() {
                @Override public void invalidated(Observable valueModel) {
                    getChildren().clear();
                    updateContent();
                }
            });

            tab.selectedProperty().addListener(tabListener);
            tab.contentProperty().addListener(tabListener);

            engine = new TraversalEngine(this, false) {
                @Override public void trav(Node owner, Direction dir) {
                    direction = dir;
                    super.trav(owner, dir);
                }
            };
            engine.addTraverseListener(this);
            setImpl_traversalEngine(engine);
            setVisible(tab.isSelected());
        }

        private void updateContent() {
            if (getTab().getContent() != null) {
                getChildren().add(getTab().getContent());
            }
        }

        private void removeListeners(Tab tab) {
            tab.selectedProperty().removeListener(tabListener);
            tab.contentProperty().removeListener(tabListener);
            engine.removeTraverseListener(this);
        }

        @Override public void onTraverse(Node node, Bounds bounds) {
            int index = engine.registeredNodes.indexOf(node);

            if (index == -1 && direction.equals(Direction.PREVIOUS)) {
                // Sends the focus back the tab
                getSkinnable().requestFocus();
            }
            if (index == -1 && direction.equals(Direction.NEXT)) {
                // Sends the focus to the next focusable control outside of the TabPane
                new TraversalEngine(getSkinnable(), false).trav(getSkinnable(), Direction.NEXT);
            }
        }
    } /* End TabContentRegion */

    /**************************************************************************
     *
     * TabControlButtons: controls to manipulate tab interaction
     *
     **************************************************************************/
    class TabControlButtons extends StackPane {
        private StackPane inner;
        private StackPane downArrow;
        private Pane downArrowBtn;
        private boolean showControlButtons;
        private ContextMenu popup;

        public TabControlButtons() {            
            getStyleClass().setAll("control-buttons-tab");

            TabPane tabPane = getSkinnable();

            downArrowBtn = new Pane();
            downArrowBtn.getStyleClass().setAll("tab-down-button");
            downArrowBtn.setVisible(isShowTabsMenu());
            downArrow = new StackPane();
            downArrow.setManaged(false);
            downArrow.getStyleClass().setAll("arrow");
            downArrow.setRotate(tabPane.getSide().equals(Side.BOTTOM) ? 180.0F : 0.0F);
            downArrowBtn.getChildren().add(downArrow);
            downArrowBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent me) {
                    showPopupMenu();
                }
            });
            
            setupPopupMenu();

            inner = new StackPane() {
                private double getArrowBtnWidth() {
                    if (animationLock) return maxArrowWidth;
                    if (isShowTabsMenu()) {
                        maxArrowWidth = Math.max(maxArrowWidth, snapSize(downArrow.prefWidth(getHeight())) + snapSize(downArrowBtn.prefWidth(getHeight())));
                    }
                    return maxArrowWidth;
                }

                @Override protected double computePrefWidth(double height) {
                    if (animationLock) return innerPrefWidth;
                    innerPrefWidth = getActualPrefWidth();
                    return innerPrefWidth;
                }

                public double getActualPrefWidth() {
                    double pw;
                    double maxArrowWidth = getArrowBtnWidth();
                    pw = 0.0F;
                    if (isShowTabsMenu()) {
                        pw += maxArrowWidth;
                    }
                    if (pw > 0) {
                        pw += snappedLeftInset() + snappedRightInset();
                    }
                    return pw;
                }

                @Override protected double computePrefHeight(double width) {
                    double height = 0.0F;
                    if (isShowTabsMenu()) {
                        height = Math.max(height, snapSize(downArrowBtn.prefHeight(width)));
                    }
                    if (height > 0) {
                        height += snappedTopInset() + snappedBottomInset();
                    }
                    return height;
                }

                @Override protected void layoutChildren() {
                    Side tabPosition = getSkinnable().getSide();
                    double x = 0.0F;
                    //padding.left;
                    double y = snappedTopInset();
                    double h = snapSize(getHeight()) - y + snappedBottomInset();
                    // when on the left or bottom, we need to position the tabs controls
                    // button such that it is the furtherest button away from the tabs.
                    if (tabPosition.equals(Side.BOTTOM) || tabPosition.equals(Side.LEFT)) {
                        x += positionTabsMenu(x, y, h, true);
                    } else {
                        x += positionTabsMenu(x, y, h, false);
                    }
                }

                private double positionTabsMenu(double x, double y, double h, boolean showSep) {
                    double newX = x;
                    if (isShowTabsMenu()) {
                        // DOWN ARROW BUTTON
                        positionArrow(downArrowBtn, downArrow, newX, y, maxArrowWidth, h);
                        newX += maxArrowWidth;
                    }
                    return newX;
                }

                private void positionArrow(Pane btn, StackPane arrow, double x, double y, double width, double height) {
                    btn.resize(width, height);
                    positionInArea(btn, x, y, width, height, /*baseline ignored*/0,
                            HPos.CENTER, VPos.CENTER);
                    // center arrow region within arrow button
                    double arrowWidth = snapSize(arrow.prefWidth(-1));
                    double arrowHeight = snapSize(arrow.prefHeight(-1));
                    arrow.resize(arrowWidth, arrowHeight);
                    positionInArea(arrow, btn.snappedLeftInset(), btn.snappedTopInset(),
                            width - btn.snappedLeftInset() - btn.snappedRightInset(),
                            height - btn.snappedTopInset() - btn.snappedBottomInset(),
                            /*baseline ignored*/0, HPos.CENTER, VPos.CENTER);
                }
            };
            inner.getStyleClass().add("container");
            inner.getChildren().add(downArrowBtn);

            getChildren().add(inner);

            tabPane.sideProperty().addListener(new InvalidationListener() {
                @Override public void invalidated(Observable valueModel) {
                    Side tabPosition = getSkinnable().getSide();
                    downArrow.setRotate(tabPosition.equals(Side.BOTTOM)? 180.0F : 0.0F);
                }
            });
            tabPane.getTabs().addListener(new ListChangeListener<Tab>() {
                @Override public void onChanged(Change<? extends Tab> c) {
                    setupPopupMenu();
                }
            });
            showControlButtons = false;
            if (isShowTabsMenu()) {
                showControlButtons = true;
                requestLayout();
            }
            getProperties().put(ContextMenu.class, popup);
        }

        private boolean showTabsMenu = false;

        private void showTabsMenu(boolean value) {
            if (value && !showTabsMenu) {
                downArrowBtn.setVisible(true);
                showControlButtons = true;
                inner.requestLayout();
                tabHeaderArea.requestLayout();
            } else if (!value && showTabsMenu) {
                hideControlButtons();
            }
            this.showTabsMenu = value;
        }

        private boolean isShowTabsMenu() {
            return showTabsMenu;
        }

        private final DoubleProperty controlTabHeight = new SimpleDoubleProperty(this, "controlTabHeight");
        {
            controlTabHeight.addListener(new InvalidationListener() {
                @Override public void invalidated(Observable valueModel) {
                    requestLayout();
                }
            });
        }
        // TODO: Maybe the getter and setter can be dropped completely after
        // turning controlTabHeight into a DoubleVariable?
        public double getControlTabHeight() {
            return controlTabHeight.get();
        }
        public void setControlTabHeight(double value) {
            controlTabHeight.set(value);
        }

        private boolean animationLock = false;
        private void setAnimationLock(boolean value) {
            animationLock = value;
            tabHeaderArea.requestLayout();
        }

//        FIXME this should be allowed, but the method is final on Node
//        @Override
//        public boolean isVisible() {
//            return getSkinnable().isShowScrollArrows() || getSkinnable().isShowTabsMenu();
//        }

        double maxArrowWidth;
        double innerPrefWidth;

        private double prefWidth;
        @Override protected double computePrefWidth(double height) {
            if (animationLock) {
                return prefWidth;
            }
            prefWidth = getActualPrefWidth(height);
            return prefWidth;
        }

        private double getActualPrefWidth(double height) {
            double pw = snapSize(inner.prefWidth(height));
            if (pw > 0) {
                pw += snappedLeftInset() + snappedRightInset();
            }
            return pw;
        }

        @Override protected double computePrefHeight(double width) {
            return Math.max(getSkinnable().getTabMinHeight(), snapSize(inner.prefHeight(width))) +
                    snappedTopInset() + snappedBottomInset();
        }

        @Override protected void layoutChildren() {
            double x = snappedLeftInset();
            double y = snappedTopInset();
            double w = snapSize(getWidth()) - x + snappedRightInset();
            double h = snapSize(getHeight()) - y + snappedBottomInset();

            if (showControlButtons) {
                showControlButtons();
                showControlButtons = false;
            }

            inner.resize(w, h);
            positionInArea(inner, x, y, w, h, /*baseline ignored*/0, HPos.CENTER, VPos.BOTTOM);
        }

        private void showControlButtons() {
            double prefHeight = snapSize(prefHeight(-1));
            Timeline timeline = new Timeline();
            KeyValue keyValue = new KeyValue(controlTabHeight, prefHeight, Interpolator.EASE_OUT);

            setVisible(true);
            timeline.getKeyFrames().clear();
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(ANIMATION_SPEED), new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent event) {
                    if (popup == null) {
                        setupPopupMenu();
                    }
                    requestLayout();
                }
            }, keyValue));
            timeline.play();
        }

        private void hideControlButtons() {
            setAnimationLock(true);
            Timeline timeline = new Timeline();
            KeyValue keyValue = new KeyValue(controlTabHeight, 0.0, Interpolator.EASE_IN);

            timeline.getKeyFrames().clear();
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(ANIMATION_SPEED), new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent event) {
                    if (!isShowTabsMenu()) {
                        downArrowBtn.setVisible(false);
                    }
                    // If the scroll arrows or tab menu is still visible we don't want
                    // to hide it animate it back it.
                    if (isShowTabsMenu()) {
                        showControlButtons = true;
                    } else {
                        setVisible(false);
                        popup.getItems().clear();
                        popup = null;
                    }
                    setAnimationLock(false);
                    // This needs to be called when we are in the left tabPosition
                    // to allow for the clip offset to move properly (otherwise
                    // it jumps too early - before the animation is done).
                    requestLayout();
                }
            }, keyValue));
            timeline.play();
        }

        private void setupPopupMenu() {
            if (popup == null) {
                popup = new ContextMenu();
//                popup.setManaged(false);
            }
            popup.getItems().clear();
            ToggleGroup group = new ToggleGroup();
            ObservableList<RadioMenuItem> menuitems = FXCollections.<RadioMenuItem>observableArrayList();
            for (final Tab tab : getSkinnable().getTabs()) {
                TabMenuItem item = new TabMenuItem(tab);                
                item.setToggleGroup(group);
                item.setOnAction(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent t) {
                        getSkinnable().getSelectionModel().select(tab);
                    }
                });
                menuitems.add(item);
            }
            popup.getItems().addAll(menuitems);
        }
        
        private void showPopupMenu() {
            for (MenuItem mi: popup.getItems()) {
                TabMenuItem tmi = (TabMenuItem)mi;
                if (selectedTab.equals(tmi.getTab())) {
                    tmi.setSelected(true);
                    break;
                }
            }
            popup.show(downArrowBtn, Side.BOTTOM, 0, 0);            
        }
    } /* End TabControlButtons*/

    class TabMenuItem extends RadioMenuItem {
        Tab tab;
        public TabMenuItem(final Tab tab) {
            super(tab.getText(), TabPaneSkinHack.clone(tab.getGraphic()));                        
            this.tab = tab;
            setDisable(tab.isDisable());
            tab.disableProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable arg0) {
                    setDisable(tab.isDisable());
                }
            });                   
        }

        public Tab getTab() {
            return tab;
        }
    }
}
