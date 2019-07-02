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

import com.jfoenix.transitions.JFXAnimationTimer;
import com.jfoenix.transitions.JFXKeyFrame;
import com.jfoenix.transitions.JFXKeyValue;
import com.sun.javafx.event.EventHandlerManager;
import com.sun.javafx.scene.control.skin.TooltipSkin;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * JFXTooltip is the material design implementation of the tooltip.
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 3/29/2019
 */
public class JFXTooltip extends Tooltip {

    private static TooltipBehavior BEHAVIOR = new TooltipBehavior(
        Duration.millis(650),
        Duration.millis(1500),
        Duration.millis(200));

    /**
     * updates the hover duration for {@link JFXTooltip} behavior
     *
     * @param duration
     */
    public static void setHoverDelay(Duration duration) {
        BEHAVIOR.setHoverDelay(duration == null ? Duration.millis(650) : duration);
    }

    /**
     * updates the visible duration for {@link JFXTooltip} behavior
     *
     * @param duration
     */
    public static void setVisibleDuration(Duration duration) {
        BEHAVIOR.setVisibleDuration(duration == null ? Duration.millis(1500) : duration);
    }

    /**
     * updates the left duration for {@link JFXTooltip} behavior
     *
     * @param duration
     */
    public static void setLeftDelay(Duration duration) {
        BEHAVIOR.setLeftDelay(duration == null ? Duration.millis(200) : duration);
    }

    /**
     * Associates the given {@link JFXTooltip} tooltip to the given node.
     *
     * @param node
     * @param tooltip
     */
    public static void install(Node node, JFXTooltip tooltip) {
        BEHAVIOR.install(node, tooltip);
    }

    /**
     * Associates the given {@link JFXTooltip} tooltip to the given node.
     * The tooltip will be shown according to the given {@link Pos} pos
     *
     * @param node
     * @param tooltip
     */
    public static void install(Node node, JFXTooltip tooltip, Pos pos) {
        tooltip.setPos(pos);
        BEHAVIOR.install(node, tooltip);
    }

    /**
     * Removes {@link JFXTooltip} tooltip from the given node
     *
     * @param node
     */
    public static void uninstall(Node node) {
        BEHAVIOR.uninstall(node);
    }

    private Node root = null;
    private boolean hiding = false;

    private JFXAnimationTimer animation = new JFXAnimationTimer(
        JFXKeyFrame.builder().setDuration(Duration.millis(150))
            .setAnimateCondition(() -> !hiding)
            .setKeyValues(JFXKeyValue.builder()
                    .setTargetSupplier(() -> root.opacityProperty())
                    .setEndValue(1).build(),
                JFXKeyValue.builder()
                    .setTargetSupplier(() -> root.scaleXProperty())
                    .setEndValue(1).build(),
                JFXKeyValue.builder()
                    .setTargetSupplier(() -> root.scaleYProperty())
                    .setEndValue(1).build())
            .build(),
        JFXKeyFrame.builder().setDuration(Duration.millis(75))
            .setAnimateCondition(() -> hiding)
            .setKeyValues(JFXKeyValue.builder()
                .setTargetSupplier(() -> root.opacityProperty())
                .setEndValue(0).build()).build()
    );

    /**
     * {@inheritDoc}
     */
    public JFXTooltip() {
        this(null);
    }

    /**
     * {@inheritDoc}
     */
    public JFXTooltip(String text, Pos pos) {
        this(text);
        setPos(pos);
    }

    /**
     * {@inheritDoc}
     */
    public JFXTooltip(String text) {
        super(text);
        init();
    }

    private void init() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
        eventHandlerManager.addEventHandler(WindowEvent.WINDOW_SHOWING, event -> {
            root = getScene().getRoot();
            root.setOpacity(0);
            root.setScaleY(0.8);
            root.setScaleX(0.8);
            animation.setOnFinished(null);
        });
        eventHandlerManager.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
            setAnchorX(getUpdatedAnchorX());
            setAnchorY(getUpdatedAnchorY());
            animation.reverseAndContinue();
        });
    }

    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    private Pos pos = Pos.BOTTOM_CENTER;
    private double margin = 8;

    /**
     * @return the tooltip position
     */
    public Pos getPos() {
        return pos;
    }

    /**
     * sets the tooltip position with respect to its node
     *
     * @param pos
     */
    public void setPos(Pos pos) {
        this.pos = pos == null ? Pos.BOTTOM_CENTER : pos;
    }

    /**
     * @return the gap between tooltip and the associated node
     */
    public double getMargin() {
        return margin;
    }

    /**
     * sets the gap between tooltip and the associated node.
     * the default value is 8
     *
     * @param margin
     */
    public void setMargin(double margin) {
        this.margin = margin;
    }

    private double getUpdatedAnchorY() {
        switch (pos.getVpos()) {
            case CENTER:
                return getAnchorY() - getHeight() / 2;
            case TOP:
            case BASELINE:
                return getAnchorY() - getHeight();
            default:
                return getAnchorY();
        }
    }

    private double getUpdatedAnchorX() {
        switch (pos.getHpos()) {
            case CENTER:
                return getAnchorX() - getWidth() / 2;
            case LEFT:
                return getAnchorX() - getWidth();
            default:
                return getAnchorX();
        }
    }


    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    /**
     * Initialize the style class to 'jfx-tooltip'.
     * <p>
     * This is the selector class from which CSS can be used to style
     * this control.
     */
    private static final String DEFAULT_STYLE_CLASS = "jfx-tooltip";


    /**
     * {@inheritDoc}
     */
    @Override
    public void hide() {
        hiding = true;
        animation.setOnFinished(super::hide);
        animation.reverseAndContinue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show(Node ownerNode, double anchorX, double anchorY) {
        // if tooltip hide animation still running, then hide method is not called yet
        // thus only reverse the animation to show the tooltip again
        hiding = false;
        if (isShowing()) {
            animation.setOnFinished(null);
            animation.reverseAndContinue();
        } else {
            // tooltip was not showing compute its anchors and show it
            Window parent = ownerNode.getScene().getWindow();
            final Bounds origin = ownerNode.localToScene(ownerNode.getBoundsInLocal());
            anchorX = parent.getX() + origin.getMinX() + ownerNode.getScene().getX() + getHPosForNode(ownerNode);
            anchorY = parent.getY() + origin.getMinY() + ownerNode.getScene().getY() + getVPosForNode(ownerNode);
            super.show(ownerNode, anchorX, anchorY);
        }
    }

    private double getHPosForNode(Node node) {
        double hx = -margin;
        switch (pos.getHpos()) {
            case CENTER:
                hx = (node.getBoundsInParent().getWidth() / 2);
                break;
            case RIGHT:
                hx = node.getBoundsInParent().getWidth() + margin;
                break;
        }
        return hx;
    }

    private double getVPosForNode(Node node) {
        double vy = -margin;
        switch (pos.getVpos()) {
            case CENTER:
                vy = (node.getBoundsInParent().getHeight() / 2);
                break;
            case BOTTOM:
                vy = node.getBoundsInParent().getHeight() + margin;
                break;
        }
        return vy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new TooltipSkin(this) {
            {
                Node node = getNode();
                node.setEffect(null);
            }
        };
    }

    private final EventHandlerManager eventHandlerManager = new EventHandlerManager(this);

    /**
     * {@inheritDoc}
     */
    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        return super.buildEventDispatchChain(tail).prepend(eventHandlerManager);
    }

    private static class TooltipBehavior {

        private static String TOOLTIP_PROP = "jfoenix-tooltip";
        private Timeline hoverTimer = new Timeline();
        private Timeline visibleTimer = new Timeline();
        private Timeline leftTimer = new Timeline();
        /**
         * the currently hovered node
         */
        private Node hoveredNode;
        /**
         * the next tooltip to be shown
         */
        private JFXTooltip nextTooltip;
        /**
         * the current showing tooltip
         */
        private JFXTooltip currentTooltip;

        private TooltipBehavior(Duration hoverDelay, Duration visibleDuration, Duration leftDelay) {
            setHoverDelay(hoverDelay);
            hoverTimer.setOnFinished(event -> {
                ensureHoveredNodeIsVisible(() -> {
                    // set tooltip orientation
                    NodeOrientation nodeOrientation = hoveredNode.getEffectiveNodeOrientation();
                    nextTooltip.getScene().setNodeOrientation(nodeOrientation);
                    //show tooltip
                    showTooltip(nextTooltip);
                    currentTooltip = nextTooltip;
                    hoveredNode = null;
                    // start visible timer
                    visibleTimer.playFromStart();
                });
                // clear next tooltip
                nextTooltip = null;
            });
            setVisibleDuration(visibleDuration);
            visibleTimer.setOnFinished(event -> hideCurrentTooltip());
            setLeftDelay(leftDelay);
            leftTimer.setOnFinished(event -> hideCurrentTooltip());
        }

        private void setHoverDelay(Duration duration) {
            hoverTimer.getKeyFrames().setAll(new KeyFrame(duration));
        }

        private void setVisibleDuration(Duration duration) {
            visibleTimer.getKeyFrames().setAll(new KeyFrame(duration));
        }

        private void setLeftDelay(Duration duration) {
            leftTimer.getKeyFrames().setAll(new KeyFrame(duration));
        }

        private void hideCurrentTooltip() {
            currentTooltip.hide();
            currentTooltip = null;
            hoveredNode = null;
        }

        private void showTooltip(JFXTooltip tooltip) {
            // anchors are computed differently for each tooltip
            tooltip.show(hoveredNode, -1, -1);
        }

        private EventHandler<MouseEvent> moveHandler = (MouseEvent event) -> {
            // if tool tip is already showing, do nothing
            if (visibleTimer.getStatus() == Timeline.Status.RUNNING) {
                return;
            }
            hoveredNode = (Node) event.getSource();
            Object property = hoveredNode.getProperties().get(TOOLTIP_PROP);
            if (property instanceof JFXTooltip) {
                JFXTooltip tooltip = (JFXTooltip) property;
                ensureHoveredNodeIsVisible(() -> {
                    // if a tooltip is already showing then show this tooltip immediately
                    if (leftTimer.getStatus() == Timeline.Status.RUNNING) {
                        if (currentTooltip != null) {
                            currentTooltip.hide();
                        }
                        currentTooltip = tooltip;
                        // show the tooltip
                        showTooltip(tooltip);
                        // stop left timer and start the visible timer to hide the tooltip
                        // once finished
                        leftTimer.stop();
                        visibleTimer.playFromStart();
                    } else {
                        // else mark the tooltip as the next tooltip to be shown once the hover
                        // timer is finished (restart the timer)
//                        t.setActivated(true);
                        nextTooltip = tooltip;
                        hoverTimer.stop();
                        hoverTimer.playFromStart();
                    }
                });
            } else {
                uninstall(hoveredNode);
            }
        };
        private WeakEventHandler<MouseEvent> weakMoveHandler = new WeakEventHandler<>(moveHandler);

        private EventHandler<MouseEvent> exitHandler = (MouseEvent event) -> {
            // stop running hover timer as the mouse exited the node
            if (hoverTimer.getStatus() == Timeline.Status.RUNNING) {
                hoverTimer.stop();
            } else if (visibleTimer.getStatus() == Timeline.Status.RUNNING) {
                // if tool tip was already showing, stop the visible timer
                // and start the left timer to hide the current tooltip
                visibleTimer.stop();
                leftTimer.playFromStart();
            }
            hoveredNode = null;
            nextTooltip = null;
        };
        private WeakEventHandler<MouseEvent> weakExitHandler = new WeakEventHandler<>(exitHandler);

        // if mouse is pressed then stop all timers / clear all fields
        private EventHandler<MouseEvent> pressedHandler = (MouseEvent event) -> {
            // stop timers
            hoverTimer.stop();
            visibleTimer.stop();
            leftTimer.stop();
            // hide current tooltip
            if (currentTooltip != null) {
                currentTooltip.hide();
            }
            // clear fields
            hoveredNode = null;
            currentTooltip = null;
            nextTooltip = null;
        };
        private WeakEventHandler<MouseEvent> weakPressedHandler = new WeakEventHandler<>(pressedHandler);

        private void install(Node node, JFXTooltip tooltip) {
            if (node == null) {
                return;
            }
            if (tooltip == null) {
                uninstall(node);
                return;
            }
            node.removeEventHandler(MouseEvent.MOUSE_MOVED, weakMoveHandler);
            node.removeEventHandler(MouseEvent.MOUSE_EXITED, weakExitHandler);
            node.removeEventHandler(MouseEvent.MOUSE_PRESSED, weakPressedHandler);
            node.addEventHandler(MouseEvent.MOUSE_MOVED, weakMoveHandler);
            node.addEventHandler(MouseEvent.MOUSE_EXITED, weakExitHandler);
            node.addEventHandler(MouseEvent.MOUSE_PRESSED, weakPressedHandler);
            node.getProperties().put(TOOLTIP_PROP, tooltip);
        }

        private void uninstall(Node node) {
            if (node == null) {
                return;
            }
            node.removeEventHandler(MouseEvent.MOUSE_MOVED, weakMoveHandler);
            node.removeEventHandler(MouseEvent.MOUSE_EXITED, weakExitHandler);
            node.removeEventHandler(MouseEvent.MOUSE_PRESSED, weakPressedHandler);
            Object tooltip = node.getProperties().get(TOOLTIP_PROP);
            if (tooltip != null) {
                node.getProperties().remove(TOOLTIP_PROP);
                if (tooltip.equals(currentTooltip) || tooltip.equals(nextTooltip)) {
                    weakPressedHandler.handle(null);
                }
            }
        }

        private void ensureHoveredNodeIsVisible(Runnable visibleRunnable) {
            final Window owner = getWindow(hoveredNode);
            if (owner != null && owner.isShowing()) {
                final boolean treeVisible = hoveredNode.impl_isTreeVisible();
                if (treeVisible) {
                    visibleRunnable.run();
                }
            }
        }

        private Window getWindow(final Node node) {
            final Scene scene = node == null ? null : node.getScene();
            return scene == null ? null : scene.getWindow();
        }
    }
}
