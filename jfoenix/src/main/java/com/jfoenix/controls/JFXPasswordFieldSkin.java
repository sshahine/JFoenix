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
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 * JFXPasswordFieldSkin inherits JFXTextFieldSkin, and plaintext the password
 *
 * @author Siqi You
 * @version 1.0
 * @since 2020-09-01
 */
public class JFXPasswordFieldSkin<T extends JFXPasswordField> extends JFXTextFieldSkin {

    private static final PseudoClass PSEUDO_CLASS_OPEN = PseudoClass.getPseudoClass("open");
    private static final PseudoClass PSEUDO_CLASS_CLOSE = PseudoClass.getPseudoClass("close");

    private final JFXPasswordField jfxPasswordField;
    private StackPane rightPane;
    private SVGGlyph eyeGlyph;

    public JFXPasswordFieldSkin(final JFXPasswordField jfxPasswordField) {
        super(jfxPasswordField);
        this.jfxPasswordField = jfxPasswordField;
        updateChildren();
    }

    /**
     * update child elements
     */
    private void updateChildren() {
        Node rightNode = createEyeGlyph();
        // add right pane(eye)
        rightPane = new StackPane(rightNode);
        rightPane.setManaged(false);
        rightPane.setAlignment(Pos.CENTER_RIGHT);
        rightPane.getStyleClass().add("right-pane");
        this.getChildren().add(rightPane);
    }

    /**
     * create eye glyph
     *
     * @return
     */
    public Node createEyeGlyph() {
        eyeGlyph = new SVGGlyph();
        // default open
        eyeGlyph.pseudoClassStateChanged(PSEUDO_CLASS_OPEN, true);
        eyeGlyph.getStyleClass().add("eye-button");
        eyeGlyph.setOnMouseReleased(this::changeTextAndEye);

        return eyeGlyph;
    }

    /**
     * @param event
     */
    private void changeTextAndEye(MouseEvent event) {
        boolean mask = jfxPasswordField.getMask();
        jfxPasswordField.setMask(!mask);
        // triggered update
        jfxPasswordField.appendText(" ");
        jfxPasswordField.deletePreviousChar();
        // reverse eye shape
        eyeGlyph.pseudoClassStateChanged(PSEUDO_CLASS_OPEN, !mask);
        eyeGlyph.pseudoClassStateChanged(PSEUDO_CLASS_CLOSE, mask);
    }


    /**
     * plaintext or ciphertext
     *
     * @param txt
     * @return
     */
    @Override
    protected String maskText(String txt) {
        // handler mask text
        if (jfxPasswordField != null && jfxPasswordField.getMask()) {
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
