package com.jfoenix.skins;

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public abstract class JFXNotificationBar extends Region  {

    private final ScrollPane pane;

    public abstract Node getContentPane();

    private static final EventType<Event> ON_SHOWING = new EventType<>(Event.ANY, "NOTIFICATION_PANE_ON_SHOWING");

    private static final EventType<Event> ON_SHOWN = new EventType<>(Event.ANY, "NOTIFICATION_PANE_ON_SHOWN");

    private DoubleProperty transition = new SimpleDoubleProperty() {
        @Override
        protected void invalidated() {
            layoutChildren();
        }
    };

    public abstract void hide();

    public abstract boolean isShowing();

    public abstract boolean isShowFromTop();

    public abstract double getContainerHeight();

    public abstract void relocateInParent(double x, double y);

    public JFXNotificationBar() {
        getStyleClass().add("jfx-notification-bar");
        setVisible(isShowing());
        pane = new ScrollPane();
        pane.setFitToWidth(true);
        pane.setFitToHeight(true);
        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pane.getStyleClass().add("pane");
        getChildren().setAll(pane);
        pane.setContent(getContentPane());
    }

    @Override
    public String getUserAgentStylesheet() {
        return JFXNotificationBar.class.getResource("/css/controls/jfx-notification.css").toExternalForm();
    }

    @Override
    protected void layoutChildren() {
        final double w = getWidth();
        final double h = computePrefHeight(-1);

        final double notificationBarHeight = prefHeight(w);
        final double notificationMinHeight = minHeight(w);

        if (isShowFromTop()) {
            pane.resize(w, h);
            relocateInParent(0, (transition.get() - 1) * notificationMinHeight);
        } else {
            pane.resize(w, notificationBarHeight);
            relocateInParent(0, getContainerHeight() - notificationBarHeight);
        }
    }

    @Override
    protected double computeMinHeight(double width) {
        return Math.max(super.computePrefHeight(width), 0);
    }

    @Override
    protected double computePrefHeight(double width) {
        return Math.max(pane.prefHeight(width), minHeight(width)) * transition.get();
    }

    public void doShow() {
        transitionStartValue = 0;
        doAnimationTransition();
    }

    private final Duration TRANSITION_DURATION = new Duration(350.0);
    private Timeline timeline;
    private double transitionStartValue;

    private void doAnimationTransition() {
        Duration duration;

        if (timeline != null && (timeline.getStatus() != Animation.Status.STOPPED)) {
            duration = timeline.getCurrentTime();
            duration = duration == Duration.ZERO ? TRANSITION_DURATION : duration;
            transitionStartValue = transition.get();
            timeline.stop();
        } else {
            duration = TRANSITION_DURATION;
        }

        timeline = new Timeline();
        timeline.setCycleCount(1);
        KeyFrame k1 = new KeyFrame(
            Duration.ZERO,
            event -> {
                setCache(true);
                setVisible(true);
                pane.fireEvent(new Event(ON_SHOWING));
            },
            new KeyValue(transition, transitionStartValue)
        );

        KeyFrame k2 = new KeyFrame(
            duration,
            event -> {
                pane.setCache(false);
                pane.fireEvent(new Event(ON_SHOWN));
            },
            new KeyValue(transition, 1, Interpolator.EASE_OUT)
        );

        timeline.getKeyFrames().setAll(k1, k2);
        timeline.play();
    }
}
