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

package com.jfoenix.skins;

import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXRippler.RipplerPos;
import com.jfoenix.controls.JFXToggleNode;
import com.jfoenix.transitions.JFXFillTransition;
import com.sun.javafx.scene.control.skin.ToggleButtonSkin;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * @author Shadi Shaheen
 * @version 2.0
 * @since 2016-03-09
 */
public class JFXToggleNodeSkin extends ToggleButtonSkin {

    private final StackPane selectionOverLay;
    private JFXRippler rippler;
    private JFXFillTransition ft;
    private Runnable releaseManualRippler = null;
    private boolean invalid = true;

    public JFXToggleNodeSkin(JFXToggleNode toggleNode) {
        super(toggleNode);

        selectionOverLay = new StackPane();
        final Node graphic = getSkinnable().getGraphic();
        if (graphic != null) {
            selectionOverLay.getChildren().add(graphic);
        }
        selectionOverLay.shapeProperty().bind(getSkinnable().shapeProperty());
        selectionOverLay.setPickOnBounds(false);

        rippler = new JFXRippler(selectionOverLay, RipplerPos.FRONT) {
            @Override
            protected Node getMask() {
                return createMask();
            }

            private StackPane createMask() {
                StackPane mask = new StackPane();
                mask.shapeProperty().bind(getSkinnable().shapeProperty());
                mask.backgroundProperty().bind(Bindings.createObjectBinding(() -> {
                    return new Background(new BackgroundFill(Color.WHITE,
                        getSkinnable().getBackground() != null
                        && getSkinnable().getBackground().getFills().size() > 0 ?
                            getSkinnable().getBackground().getFills().get(0).getRadii() : CornerRadii.EMPTY,
                        getSkinnable().getBackground() != null
                        && getSkinnable().getBackground().getFills().size() > 0 ?
                            getSkinnable().getBackground().getFills().get(0).getInsets() : Insets.EMPTY));
                }, getSkinnable().backgroundProperty()));
                mask.resize(getWidth() - snappedRightInset() - snappedLeftInset(),
                    getHeight() - snappedBottomInset() - snappedTopInset());
                return mask;
            }
        };

        // listener to change background color
        getSkinnable().selectedProperty().addListener(observable -> {
            // show animation only on user action
            if (!toggleNode.isDisableAnimation()) {
                if (ft == null) {
                    ft = new JFXFillTransition(Duration.millis(120), selectionOverLay);
                    ft.toValueProperty().bind(toggleNode.selectedColorProperty());
                    ft.fromValueProperty().bind(toggleNode.unSelectedColorProperty());
                }
                ft.stop();
                ft.setRate(getSkinnable().isSelected() ? 1 : -1);
                ft.play();
            } else {
                // disable animation if the selected property changed from code
                updateSelectionBackground();
            }
        });

        // show focus traversal effect
        getSkinnable().armedProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                releaseManualRippler = rippler.createManualRipple();
            } else if (releaseManualRippler != null) {
                releaseManualRippler.run();
            }
        });

        toggleNode.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!toggleNode.isDisableVisualFocus()) {
                if (newVal) {
                    if (!getSkinnable().isPressed()) {
                        rippler.setOverlayVisible(true);
                    }
                } else {
                    rippler.setOverlayVisible(false);
                }
            }
        });
        toggleNode.pressedProperty().addListener((o, oldVal, newVal) -> rippler.setOverlayVisible(false));

        updateChildren();
    }

    public void updateSelectionBackground() {
        CornerRadii radii = getSkinnable().getBackground() == null ? CornerRadii.EMPTY : getSkinnable().getBackground()
            .getFills()
            .get(0)
            .getRadii();
        Insets insets = getSkinnable().getBackground() == null ? Insets.EMPTY : getSkinnable().getBackground()
            .getFills()
            .get(0)
            .getInsets();
        selectionOverLay.setBackground(new Background(new BackgroundFill(getSkinnable().isSelected() ? ((JFXToggleNode) getSkinnable())
            .getSelectedColor() : ((JFXToggleNode) getSkinnable()).getUnSelectedColor(), radii, insets)));
    }

    @Override
    protected void updateChildren() {
        super.updateChildren();
        if (rippler != null) {
            getChildren().add(0, rippler);
        }
        for (int i = 1; i < getChildren().size(); i++) {
            getChildren().get(i).setMouseTransparent(true);
        }
    }

    @Override
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        super.layoutChildren(x, y, w, h);
        if (invalid) {
            updateSelectionBackground();
            invalid = false;
        }
        rippler.resizeRelocate(
            getSkinnable().getLayoutBounds().getMinX(),
            getSkinnable().getLayoutBounds().getMinY(),
            getSkinnable().getWidth(), getSkinnable().getHeight());
    }

}
