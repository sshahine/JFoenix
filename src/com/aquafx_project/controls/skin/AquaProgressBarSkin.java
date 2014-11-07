package com.aquafx_project.controls.skin;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.util.Duration;

import com.aquafx_project.AquaFx;
import com.aquafx_project.util.BindableTransition;
import com.sun.javafx.scene.control.skin.ProgressBarSkin;

public class AquaProgressBarSkin extends ProgressBarSkin implements AquaSkin {

    private BindableTransition indeterminateProgressTransition;
    private BindableTransition determinateProgressTransition;
    private final Image image = new Image(AquaFx.class.getResource("controls/skin/progress.png").toExternalForm());
    private final Background originalBackground = getSkinnable().getBackground();

    public AquaProgressBarSkin(ProgressBar progressBar) {
        super(progressBar);
        if (progressBar.isIndeterminate()) {
            setIndeterminateProgressAnimation();
        } else {
            setDeterminateProgressAnimation();
        }
        registerChangeListener(progressBar.disabledProperty(), "DISABLED");
        registerChangeListener(progressBar.indeterminateProperty(), "INDETERMINATE");
    }

    @Override protected void handleControlPropertyChanged(String propertyReference) {
        super.handleControlPropertyChanged(propertyReference);
        if (propertyReference == "DISABLED") {
            if (getSkinnable().isIndeterminate()) {
                if (getSkinnable().isDisabled() && indeterminateProgressTransition != null && indeterminateProgressTransition.getStatus() == Status.RUNNING) {
                    indeterminateProgressTransition.stop();
                } else {
                    setIndeterminateProgressAnimation();
                }
            } else {
                if (getSkinnable().isDisabled() && determinateProgressTransition != null && determinateProgressTransition.getStatus() == Status.RUNNING) {
                    determinateProgressTransition.stop();
                } else {
                    setDeterminateProgressAnimation();
                }
            }
        }
        if (propertyReference == "INDETERMINATE") {
            if (getSkinnable().isIndeterminate()) {
                if (determinateProgressTransition != null && determinateProgressTransition.getStatus() == Status.RUNNING) {
                    determinateProgressTransition.stop();
                }
                ((StackPane) getSkinnable().lookup(".bar")).setBackground(null);
                setIndeterminateProgressAnimation();
            } else {
                if (indeterminateProgressTransition != null && indeterminateProgressTransition.getStatus() == Status.RUNNING) {
                    indeterminateProgressTransition.stop();
                }
                getSkinnable().setBackground(originalBackground);
                setDeterminateProgressAnimation();
            }
        }
    }

    private void setIndeterminateProgressAnimation() {
        if (!getSkinnable().isDisabled()) {
            if (indeterminateProgressTransition != null && indeterminateProgressTransition.getStatus() == Status.RUNNING) {
                indeterminateProgressTransition.stop();
            } else {
                final Duration duration = Duration.millis(2000);
                indeterminateProgressTransition = new BindableTransition(duration);
                indeterminateProgressTransition.setCycleCount(Timeline.INDEFINITE);
                indeterminateProgressTransition.setAutoReverse(false);
                indeterminateProgressTransition.setInterpolator(Interpolator.LINEAR);
                indeterminateProgressTransition.fractionProperty().addListener(new ChangeListener<Number>() {

                    @Override public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                        double startX = newValue.doubleValue() * 0.82;
                        double endX = newValue.doubleValue() * 0.82 + 0.05d;

                        List<BackgroundFill> list = new ArrayList<>();

                        // the animated fill

                        Stop[] stops0 = new Stop[] { new Stop(0.0f, Color.rgb(176, 176, 176)), new Stop(1f, Color.rgb(207, 207,
                                207)) };
                        LinearGradient gradient0 = new LinearGradient(0.0, 0.0, 0.0, 1.0, true, CycleMethod.NO_CYCLE, stops0);
                        BackgroundFill backkgroudFill0 = new BackgroundFill(gradient0, new CornerRadii(2.0), new Insets(0));
                        list.add(backkgroudFill0);

                        Stop[] stops1 = new Stop[] { new Stop(0.5f, Color.rgb(84, 169, 239)), new Stop(0.5f, Color.rgb(236, 236,
                                236)) };
                        LinearGradient gradient1 = new LinearGradient(startX, 0.45, endX, 0.0, true, CycleMethod.REFLECT, stops1);
                        BackgroundFill backkgroudFill1 = new BackgroundFill(gradient1, new CornerRadii(2.0), new Insets(1));
                        list.add(backkgroudFill1);

                        Stop[] stops2 = new Stop[] { new Stop(0.05f, Color.rgb(255, 255, 255, 0.7)), new Stop(0.05f, Color.rgb(
                                255, 255, 255, 0.55)), new Stop(0.5f, Color.rgb(255, 255, 255, 0.1)), new Stop(0.5f, Color.rgb(
                                255, 255, 255, 0.0)), new Stop(0.6f, Color.rgb(255, 255, 255, 0.0)), new Stop(0.85f, Color.rgb(
                                255, 255, 255, 0.4)), new Stop(1f, Color.rgb(245, 245, 245, 0.7)) };
                        LinearGradient gradient2 = new LinearGradient(0.0, 0.0, 0.0, 1.0, true, CycleMethod.NO_CYCLE, stops2);
                        BackgroundFill backkgroudFill2 = new BackgroundFill(gradient2, new CornerRadii(2.0), new Insets(0));
                        list.add(backkgroudFill2);

                        getSkinnable().setBackground(new Background(list, null));
                    }
                });
                indeterminateProgressTransition.play();
            }
        } else {
            List<BackgroundFill> list = new ArrayList<>();

            Stop[] stops0 = new Stop[] { new Stop(0.0f, Color.rgb(176, 176, 176)), new Stop(1f, Color.rgb(207, 207, 207)) };
            LinearGradient gradient0 = new LinearGradient(0.0, 0.0, 0.0, 1.0, true, CycleMethod.NO_CYCLE, stops0);
            BackgroundFill backkgroudFill0 = new BackgroundFill(gradient0, new CornerRadii(2.0), new Insets(0));
            list.add(backkgroudFill0);

            Stop[] stops1 = new Stop[] { new Stop(0.5f, Color.rgb(84, 169, 239)), new Stop(0.5f, Color.rgb(236, 236, 236)) };
            LinearGradient gradient1 = new LinearGradient(0.0, 0.45, 0.05, 0.0, true, CycleMethod.REFLECT, stops1);
            BackgroundFill backkgroudFill1 = new BackgroundFill(gradient1, new CornerRadii(2.0), new Insets(1));
            list.add(backkgroudFill1);

            Stop[] stops2 = new Stop[] { new Stop(0.05f, Color.rgb(255, 255, 255, 0.7)), new Stop(0.05f, Color.rgb(255, 255, 255,
                    0.55)), new Stop(0.5f, Color.rgb(255, 255, 255, 0.1)), new Stop(0.5f, Color.rgb(255, 255, 255, 0.0)), new Stop(0.6f, Color.rgb(
                    255, 255, 255, 0.0)), new Stop(0.85f, Color.rgb(255, 255, 255, 0.4)), new Stop(1f, Color.rgb(245, 245, 245,
                    0.7)) };
            LinearGradient gradient2 = new LinearGradient(0.0, 0.0, 0.0, 1.0, true, CycleMethod.NO_CYCLE, stops2);
            BackgroundFill backkgroudFill2 = new BackgroundFill(gradient2, new CornerRadii(2.0), new Insets(0));
            list.add(backkgroudFill2);

            getSkinnable().setBackground(new Background(list, null));
        }
    }

    private void setDeterminateProgressAnimation() {
        if (!getSkinnable().isDisabled()) {
            if (determinateProgressTransition != null && determinateProgressTransition.getStatus() == Status.RUNNING) {
                determinateProgressTransition.stop();
            } else {
                final Duration duration = Duration.millis(1000);
                determinateProgressTransition = new BindableTransition(duration);
                determinateProgressTransition.setCycleCount(Timeline.INDEFINITE);
                determinateProgressTransition.setAutoReverse(false);
                determinateProgressTransition.setInterpolator(Interpolator.LINEAR);
                determinateProgressTransition.fractionProperty().addListener(new ChangeListener<Number>() {

                    @Override public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        StackPane bar = ((StackPane) getSkinnable().lookup(".bar"));
                        int start = 1 - (int) ((38 * bar.getHeight() / 20) * newValue.doubleValue());
                        ImagePattern pattern = new ImagePattern(image, start, 0, (38 * bar.getHeight() / 20), bar.getHeight(), false);

                        BackgroundFill backkgroudFill = new BackgroundFill(pattern, new CornerRadii(0), new Insets(0));

                        bar.setBackground(new Background(backkgroudFill));
                    }
                });
                determinateProgressTransition.play();
            }
        } else {
            StackPane bar = ((StackPane) getSkinnable().lookup(".bar"));
            ImagePattern pattern = new ImagePattern(image, 0, 0, (38 * bar.getHeight() / 20), bar.getHeight(), false);
            BackgroundFill backkgroudFill = new BackgroundFill(pattern, new CornerRadii(0), new Insets(0));
            bar.setBackground(new Background(backkgroudFill));
        }
    }

}
