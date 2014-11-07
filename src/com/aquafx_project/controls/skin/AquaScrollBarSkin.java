package com.aquafx_project.controls.skin;

import javafx.animation.FadeTransition;
import javafx.animation.FadeTransitionBuilder;
import javafx.animation.SequentialTransition;
import javafx.animation.SequentialTransitionBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.util.Duration;

import com.aquafx_project.util.BindableTransition;
import com.sun.javafx.scene.control.skin.ScrollBarSkin;


public class AquaScrollBarSkin extends ScrollBarSkin implements AquaSkin{

    private BindableTransition growScrollbarTransition;
    private FadeTransition fadeIn;
    private SequentialTransition fadeOutSeq;
    private boolean alreadyFaded = false;
    private boolean alreadyHovered = false;
    private boolean wide = false;
    private boolean fadeable = false;

    public AquaScrollBarSkin(ScrollBar scrollBar) {
        super(scrollBar);

        if (getNode().getParent() instanceof ScrollPane) {
            fadeable = true;
        }
        scrollBar.setVisible(!fadeable);
        registerChangeListener(scrollBar.hoverProperty(), "HOVER");
        registerChangeListener(scrollBar.valueProperty(), "VALUE");
        registerChangeListener(scrollBar.visibleProperty(), "VISIBLE");
    }

    @Override protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if (p == "HOVER") {
            setGrowScrollbarAnimation();
            if (getSkinnable().isHover() && fadeable) {
                fadeOutSeq.jumpTo(Duration.millis(0));
                fadeOutSeq.stop();
            } else if (fadeable && alreadyFaded) {
                fadeOutSeq.playFromStart();
            }
        }
        if (p == "VALUE") {
            /*
             * when value changes, scrolling is activated and the scrollbar has to fade in for some
             * time and fade out again, when there is no further interaction
             */
            if (fadeable && fadeOutSeq != null && fadeOutSeq.getCurrentRate() != 0.0d) {
                fadeOutSeq.playFromStart();
            } else if (fadeable) {
                fading();
            }
        }
        if (p == "VISIBLE") {
            if (fadeable && getSkinnable().isVisible()) {
                fading();
            }
        }
    }

    private void fading() {
        if (fadeIn == null) {
            fadeOutSeq = SequentialTransitionBuilder.create().delay(Duration.millis(2000)).children(
                    FadeTransitionBuilder.create().delay(Duration.millis(300)).fromValue(1.0).toValue(0.0).onFinished(
                            new EventHandler<ActionEvent>() {

                                @Override public void handle(ActionEvent event) {
                                    alreadyFaded = false;
                                    alreadyHovered = false;
                                    wide = false;
                                    getSkinnable().setStyle(null);
                                    for (Node child : getChildren()) {
                                        child.setStyle(null);
                                    }
                                }
                            }).build()).node(getSkinnable()).build();

            fadeIn = FadeTransitionBuilder.create().delay(Duration.millis(100)).node(getSkinnable()).fromValue(0.0).toValue(1.0).onFinished(
                    new EventHandler<ActionEvent>() {

                        @Override public void handle(ActionEvent event) {
                            alreadyFaded = true;
                            fadeOutSeq.playFromStart();
                        }
                    }).build();
        }
        if (fadeIn.getCurrentRate() == 0.0d && !alreadyFaded) {
            fadeIn.play();
        }

    }

    private void setGrowScrollbarAnimation() {

        if (getSkinnable().isHover() && !alreadyHovered && alreadyFaded) {

            if (growScrollbarTransition == null) {
                growScrollbarTransition = new BindableTransition(Duration.millis(200));
                growScrollbarTransition.setCycleCount(1);

                final double startWidth = 4;
                final double endWidth = 6;

                growScrollbarTransition.fractionProperty().addListener(new ChangeListener<Number>() {

                    @Override public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                        for (Node child : getChildren()) {
                            if (child.getStyleClass().get(0).equals("increment-button") || child.getStyleClass().get(0).equals(
                                    "decrement-button")) {
                                if (getSkinnable().getOrientation() == Orientation.VERTICAL) {
                                    child.setStyle("-fx-padding: 0.0em " + ((endWidth - startWidth) * newValue.doubleValue() + startWidth) + "pt 0.0em 0.0em;}");
                                } else if (getSkinnable().getOrientation() == Orientation.HORIZONTAL) {
                                    child.setStyle("-fx-padding: " + ((endWidth - startWidth) * newValue.doubleValue() + startWidth) + "pt  0.0em 0.0em 0.0em;}");
                                }
                            }
                        }
                    }
                });
            }
            growScrollbarTransition.play();
            alreadyHovered = true;
        } else if (!wide && !getSkinnable().isHover() && alreadyHovered) {
            /*
             * when scrollbar is shown and we hover out, it still should be shown for the time of
             * the fadeOut- transition
             */
            if (growScrollbarTransition != null) {
                growScrollbarTransition.stop();
            }
            if (getSkinnable().getOrientation() == Orientation.VERTICAL) {
                getSkinnable().setStyle(" -fx-padding: -1.0 0.0 -1.0 0.0;");
            } else {
                getSkinnable().setStyle(" -fx-padding: 0.0 0.0 0.0 0.0;");
            }
            for (Node child : getChildren()) {
                if (child.getStyleClass().get(0).equals("track")) {
                    if (getSkinnable().getOrientation() == Orientation.VERTICAL) {
                        child.setStyle("-fx-background-color: linear-gradient(rgb(238.0, 238.0, 238.0, 0.8) 0.0%, rgb(255.0, 255.0, 255.0, 0.8) 100.0%);" + "-fx-border-width: 0.0 0.0 0.0 1.0;" + "-fx-border-insets: 0.0 0.0 0.0 -1.0;" + "-fx-border-color: rgb(198.0, 198.0, 198.0);");
                    } else if (getSkinnable().getOrientation() == Orientation.HORIZONTAL) {
                        child.setStyle("-fx-background-color: linear-gradient(rgb(238.0, 238.0, 238.0, 0.8) 0.0%, rgb(255.0, 255.0, 255.0, 0.8) 100.0%);" + "-fx-border-width: 1.0 0.0 0.0 0.0;" + "-fx-border-insets: -1.0 0.0 0.0 0.0;" + "-fx-border-color: rgb(198.0, 198.0, 198.0);");
                    }
                } else if (child.getStyleClass().get(0).equals("thumb")) {
                    if (getSkinnable().getOrientation() == Orientation.VERTICAL) {
                        child.setStyle("-fx-background-radius: 6.0;" + "-fx-background-insets: 0.0 2.0 0.0 2.0;");
                    } else if (getSkinnable().getOrientation() == Orientation.HORIZONTAL) {
                        child.setStyle("-fx-background-radius: 6.0;" + "-fx-background-insets: 2.0 0.0 2.0 0.0;");
                    }
                } else if (child.getStyleClass().get(0).equals("increment-button") || child.getStyleClass().get(0).equals(
                        "decrement-button")) {
                    if (getSkinnable().getOrientation() == Orientation.VERTICAL) {
                        child.setStyle("-fx-padding: 0.0em 6.0pt 0.0em 0.0em");
                    } else if (getSkinnable().getOrientation() == Orientation.HORIZONTAL) {
                        child.setStyle("-fx-padding: 6.0pt 0.0em 0.0em 0.0em");
                    }
                }
            }
            wide = true;
        }
    }
}
