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

import com.jfoenix.skins.JFXTextFieldSkin;
import com.jfoenix.svg.SVGGlyph;
import com.sun.javafx.scene.text.HitInfo;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

/**
 * JFXPasswordFieldSkin inherits JFXTextFieldSkin, and plaintext the password
 *
 * @author Siqi You
 * @version 1.0
 * @since 2020-09-01
 */
public class JFXPasswordFieldSkin<T extends JFXPasswordField> extends JFXTextFieldSkin {

    private static final PseudoClass HAS_NO_SIDE_NODE = PseudoClass.getPseudoClass("no-side-nodes");
    private static final PseudoClass HAS_RIGHT_NODE = PseudoClass.getPseudoClass("right-node-visible");
    private static final String OPEN_EYE_SVG = "M651.669528 512c0-14.5903-2.084475-28.290323-6.251378-41.395806-11.019984 24.423249-35.143405 41.395806-63.733557 41.395806l-7.440459-0.593518-5.957689 0.593518c-30.973432 0-56.285934-25.607214-56.285934-56.882522l0.297782-5.952572-0.297782-7.447623c0-28.290323 16.97665-52.709479 41.097-63.727417-13.104459-4.172019-26.802436-6.252401-41.097-6.252401-77.132728 0-139.67311 62.834071-139.67311 140.263557 0 77.428464 62.540382 139.971915 139.67311 139.971915S651.669528 589.428464 651.669528 512L651.669528 512zM958.70846 512c0 0-153.36904-297.80564-448.198905-297.80564-293.63669 0-445.219037 297.80564-445.219037 297.80564s164.385954 297.804616 447.306582 297.804616C805.038568 809.80564 958.70846 512 958.70846 512L958.70846 512zM304.725748 512c0-114.954125 92.619444-208.164017 207.27374-208.164017 114.657366 0 207.27067 93.209892 207.27067 208.164017 0 114.953102-92.613304 208.167087-207.27067 208.167087C397.345192 720.167087 304.725748 626.953102 304.725748 512L304.725748 512zM304.725748 512";
    private static final String CLOSE_EYE_SVG = "M733.976092 128.406508c-32.626072-18.838044-74.333986-7.659447-93.16282 24.961508l-40.699957 70.307276c-28.245298-6.075369-58.112536-9.480932-89.604784-9.480932-293.635667 0-445.218014 297.803593-445.218014 297.803593s82.454944 149.279908 231.025701 236.45127l-31.253818 53.983495c-18.831904 32.618908-7.659447 74.327846 24.964578 93.164866 32.615838 18.828834 74.330916 7.659447 93.164866-24.964578L758.936577 221.569328C777.772574 188.95042 766.591931 147.238412 733.976092 128.406508zM304.728818 511.997953c0-114.952078 92.615351-208.164017 207.269647-208.164017 13.520945 0 26.731828 1.300622 39.521109 3.776l-37.147038 64.167438c-0.791016-0.01228-1.578961-0.044002-2.37407-0.044002-77.131705 0-139.67004 62.835094-139.67004 140.263557 0 26.116821 7.132444 50.523697 19.526729 71.417563l-37.134759 64.144926C323.559699 611.141998 304.728818 563.795667 304.728818 511.997953z M770.978844 300.511261 699.60119 423.39601c12.599969 26.88737 19.668968 56.910151 19.668968 88.601944 0 109.690238-84.347037 199.515033-191.68367 207.520357l-51.458999 88.590688c11.92868 1.092891 24.075325 1.695618 36.464493 1.695618 292.447609 0 446.1175-297.807686 446.1175-297.807686S895.060861 388.454196 770.978844 300.511261z";

    private final JFXPasswordField jfxPasswordField;
    private StackPane rightPane;
    private SVGGlyph eyeGlyph;
    private BooleanProperty maskTextProperty;

    public JFXPasswordFieldSkin(final JFXPasswordField jfxPasswordField) {
        super(jfxPasswordField);
        this.jfxPasswordField = jfxPasswordField;
        this.maskTextProperty = jfxPasswordField.maskTextPropertyProperty();
        updateChildren();
    }

    /**
     * right node
     *
     * @return
     */
    public ObjectProperty<Node> rightProperty() {

        // create eye SVGGlyph
        eyeGlyph = new SVGGlyph(0, "eye", OPEN_EYE_SVG, null);
        eyeGlyph.setSize(20, 14);
        eyeGlyph.setFill(jfxPasswordField.getDefaultColor());
        eyeGlyph.setCursor(Cursor.HAND);
        eyeGlyph.getStyleClass().addAll("eye-button");

        //
        eyeGlyph.setOnMouseReleased(e -> {
            maskTextProperty.set(!maskTextProperty.getValue());
            // triggered update
            jfxPasswordField.appendText(" ");
            jfxPasswordField.deletePreviousChar();
            // reverse eye icon
            reverseEye(eyeGlyph);
        });


        ObjectProperty<Node> rightProperty = new SimpleObjectProperty<Node>();
        rightProperty.set(eyeGlyph);
        return rightProperty;
    }


    /**
     * reverse eye icon
     *
     * @param eye
     */
    private void reverseEye(SVGGlyph eye) {
        String eyeSvg;

        String currentSvg = ((SVGPath) eye.getShape()).getContent();
        if (currentSvg.equals(OPEN_EYE_SVG)) {
            eyeSvg = CLOSE_EYE_SVG;
        } else {
            eyeSvg = OPEN_EYE_SVG;
        }

        SVGPath shape = new SVGPath();
        shape.setContent(eyeSvg);
        eye.setShape(shape);
    }

    /**
     * plaintext or ciphertext
     *
     * @param txt
     * @return
     */
    @Override
    protected String maskText(String txt) {
        // init mask text property
        if (null == maskTextProperty) {
            maskTextProperty = new SimpleBooleanProperty(true);
        }

        if (maskTextProperty.get()) {
            int n = txt.length();
            StringBuilder passwordBuilder = new StringBuilder(n);
            for (int i = 0; i < n; i++) {
                passwordBuilder.append(BULLET);
            }
            return passwordBuilder.toString();
        } else {
            return txt;
        }
    }

    @Override
    protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if ("RIGHT_NODE".equals(p)) {
            updateChildren();
        }
    }

    /**
     * update child elements
     */
    private void updateChildren() {

        Node newRight = rightProperty().get();
        // add right pane(eye)
        if (newRight != null) {
            getChildren().remove(rightPane);
            rightPane = new StackPane(newRight);
            rightPane.setManaged(false);
            rightPane.setAlignment(Pos.CENTER_RIGHT);
            rightPane.getStyleClass().add("right-pane");
            getChildren().add(rightPane);
        }

        jfxPasswordField.pseudoClassStateChanged(HAS_RIGHT_NODE, newRight != null);
        jfxPasswordField.pseudoClassStateChanged(HAS_NO_SIDE_NODE, newRight == null);
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        final double fullHeight = h + snappedTopInset() + snappedBottomInset();
        final double rightWidth = rightPane == null ? 0.0 : snapSize(rightPane.prefWidth(fullHeight));
        final double textFieldStartX = snapPosition(x);
        final double textFieldWidth = w - snapSize(rightWidth);

        super.layoutChildren(textFieldStartX, 0, textFieldWidth, fullHeight);

        if (rightPane != null) {
            final double rightStartX = rightPane == null ? 0.0 : w - rightWidth + snappedLeftInset();
            rightPane.resizeRelocate(rightStartX, 0, rightWidth, fullHeight);
        }
    }

    @Override
    public HitInfo getIndex(double x, double y) {
        return super.getIndex(x, y);
    }

    @Override
    protected double computePrefWidth(double h, double topInset, double rightInset, double bottomInset, double leftInset) {
        final double pw = super.computePrefWidth(h, topInset, rightInset, bottomInset, leftInset);
        final double rightWidth = rightPane == null ? 0.0 : snapSize(rightPane.prefWidth(h));

        return pw + rightWidth;
    }

    @Override
    protected double computePrefHeight(double w, double topInset, double rightInset, double bottomInset, double leftInset) {
        final double ph = super.computePrefHeight(w, topInset, rightInset, bottomInset, leftInset);
        final double rightHeight = rightPane == null ? 0.0 : snapSize(rightPane.prefHeight(-1));

        return Math.max(ph, rightHeight);
    }

    @Override
    protected double computeMinWidth(double h, double topInset, double rightInset, double bottomInset, double leftInset) {
        final double mw = super.computeMinWidth(h, topInset, rightInset, bottomInset, leftInset);
        final double rightWidth = rightPane == null ? 0.0 : snapSize(rightPane.minWidth(h));

        return mw + rightWidth;
    }

    @Override
    protected double computeMinHeight(double w, double topInset, double rightInset, double bottomInset, double leftInset) {
        final double mh = super.computeMinHeight(w, topInset, rightInset, bottomInset, leftInset);
        final double rightHeight = rightPane == null ? 0.0 : snapSize(rightPane.minHeight(-1));

        return Math.max(mh, rightHeight);
    }
}
