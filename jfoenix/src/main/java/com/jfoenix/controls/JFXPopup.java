/*
 * Copyright (c) 2016 JFoenix
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.jfoenix.controls;

import com.jfoenix.skins.JFXPopupSkin;
import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Window;

/**
 * JFXPopup is the material design implementation of a popup.
 *
 * @author Shadi Shaheen
 * @version 2.0
 * @since 2017-03-01
 */
@DefaultProperty(value = "popupContent")
public class JFXPopup extends PopupControl {

    public enum PopupHPosition {
        RIGHT, LEFT
    }

    public enum PopupVPosition {
        TOP, BOTTOM
    }

    /**
     * Creates empty popup.
     */
    public JFXPopup() {
        this(null);
    }

    /**
     * creates popup with a specified container and content
     *
     * @param content the node that will be shown in the popup
     */
    public JFXPopup(Region content) {
        setPopupContent(content);
        initialize();
    }

    private void initialize() {
        this.setAutoFix(false);
        this.setAutoHide(true);
        this.setHideOnEscape(true);
        this.setConsumeAutoHidingEvents(false);
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
        getScene().getRoot().setStyle("-fx-background-color: TRANSPARENT");
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXPopupSkin(this);
    }

    /***************************************************************************
     *                                                                         *
     * Setters / Getters                                                       *
     *                                                                         *
     **************************************************************************/

    private ObjectProperty<Region> popupContent = new SimpleObjectProperty<>(new Pane());

    public final ObjectProperty<Region> popupContentProperty() {
        return this.popupContent;
    }

    public final Region getPopupContent() {
        return this.popupContentProperty().get();
    }

    public final void setPopupContent(final Region popupContent) {
        this.popupContentProperty().set(popupContent);
    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * show the popup using the default position
     */
    public void show(Node node) {
        this.show(node, PopupVPosition.TOP, PopupHPosition.LEFT, 0, 0);
    }

    /**
     * show the popup according to the specified position
     *
     * @param vAlign can be TOP/BOTTOM
     * @param hAlign can be LEFT/RIGHT
     */
    public void show(Node node, PopupVPosition vAlign, PopupHPosition hAlign) {
        this.show(node, vAlign, hAlign, 0, 0);
    }

    /**
     * show the popup according to the specified position with a certain offset
     *
     * @param vAlign      can be TOP/BOTTOM
     * @param hAlign      can be LEFT/RIGHT
     * @param initOffsetX on the x axis
     * @param initOffsetY on the y axis
     */
    public void show(Node node, PopupVPosition vAlign, PopupHPosition hAlign, double initOffsetX, double initOffsetY) {
        if (!isShowing()) {
            if (node.getScene() == null || node.getScene().getWindow() == null) {
                throw new IllegalStateException("Can not show popup. The node must be attached to a scene/window.");
            }
            Window parent = node.getScene().getWindow();
            final Point2D origin = node.localToScene(0, 0);
            final double anchorX = parent.getX() + origin.getX()
                + node.getScene().getX() + (hAlign == PopupHPosition.RIGHT ? ((Region) node).getWidth() : 0);
            final double anchorY = parent.getY() + origin.getY()
                + node.getScene()
                      .getY() + (vAlign == PopupVPosition.BOTTOM ? ((Region) node).getHeight() : 0);
            this.show(parent, anchorX, anchorY);
            ((JFXPopupSkin) getSkin()).reset(vAlign, hAlign, initOffsetX, initOffsetY);
            Platform.runLater(() -> ((JFXPopupSkin) getSkin()).animate());
        }
    }

    public void show(Window window, double x, double y, PopupVPosition vAlign, PopupHPosition hAlign, double initOffsetX, double initOffsetY) {
        if (!isShowing()) {
            if (window == null) {
                throw new IllegalStateException("Can not show popup. The node must be attached to a scene/window.");
            }
            Window parent = window;
            final double anchorX = parent.getX() + x + initOffsetX;
            final double anchorY = parent.getY() + y + initOffsetY;
            this.show(parent, anchorX, anchorY);
            ((JFXPopupSkin) getSkin()).reset(vAlign, hAlign, initOffsetX, initOffsetY);
            Platform.runLater(() -> ((JFXPopupSkin) getSkin()).animate());
        }
    }

    @Override
    public void hide() {
        super.hide();
        ((JFXPopupSkin) getSkin()).init();
    }

    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    /**
     * Initialize the style class to 'jfx-popup'.
     * <p>
     * This is the selector class from which CSS can be used to style
     * this control.
     */
    private static final String DEFAULT_STYLE_CLASS = "jfx-popup";
}
