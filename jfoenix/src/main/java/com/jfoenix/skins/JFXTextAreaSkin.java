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
package com.jfoenix.skins;

import com.jfoenix.concurrency.JFXUtilities;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.transitions.CachedTransition;
import com.jfoenix.validation.base.ValidatorBase;
import com.sun.javafx.scene.control.skin.TextAreaSkin;
import javafx.animation.Animation.Status;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import java.lang.reflect.Field;

/**
 * <h1>Material Design TextArea Skin</h1>
 *
 * @author Shadi Shaheen
 * @version 2.0
 * @since 2017-01-25
 */
public class JFXTextAreaSkin extends TextAreaSkin {

    private static Background transparentBackground = new Background(
        new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY),
        new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY),
        new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY),
        new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY));

    private boolean invalid = true;

    private StackPane line = new StackPane();
    private StackPane focusedLine = new StackPane();

    private Label errorLabel = new Label();
    private StackPane errorIcon = new StackPane();
    private HBox errorContainer;
    private ScrollPane scrollPane;

    private double initScale = 0.05;
    private double oldErrorLabelHeight = -1;
    //	private Region textPane;
    private double initYLayout = -1;
    private double initHeight = -1;
    private boolean errorShown = false;
    private double currentFieldHeight = -1;
    private double errorLabelInitHeight = 0;
    private boolean heightChanged = false;

    private Pane promptContainer;
    private Text promptText;

    private CachedTransition promptTextUpTransition;
    private CachedTransition promptTextDownTransition;
    private CachedTransition promptTextColorTransition;

    private Timeline hideErrorAnimation;
    private ParallelTransition transition;

    private Scale promptTextScale = new Scale(1, 1, 0, 0);
    private Scale scale = new Scale(initScale, 1);
    private Timeline linesAnimation = new Timeline(
        new KeyFrame(Duration.ZERO,
                     new KeyValue(scale.xProperty(), initScale, Interpolator.EASE_BOTH),
                     new KeyValue(focusedLine.opacityProperty(), 0, Interpolator.EASE_BOTH)),
        new KeyFrame(Duration.millis(1),
                     new KeyValue(focusedLine.opacityProperty(), 1, Interpolator.EASE_BOTH)),
        new KeyFrame(Duration.millis(160),
                     new KeyValue(scale.xProperty(), 1, Interpolator.EASE_BOTH))
    );

    private Paint oldPromptTextFill;
    private BooleanBinding usePromptText = Bindings.createBooleanBinding(() -> usePromptText(),
                                                                         getSkinnable().textProperty(),
                                                                         getSkinnable().promptTextProperty());

    public JFXTextAreaSkin(JFXTextArea textArea) {
        super(textArea);
        // init text area properties
        scrollPane = (ScrollPane) getChildren().get(0);
        ((Region) scrollPane.getContent()).setPadding(new Insets(0));
        // hide text area borders
        scrollPane.setBackground(transparentBackground);
        ((Region) scrollPane.getContent()).setBackground(transparentBackground);
        getSkinnable().setBackground(transparentBackground);
        textArea.setWrapText(true);

        errorLabel.getStyleClass().add("error-label");
        errorLabel.setPadding(new Insets(4, 0, 0, 0));
        errorLabel.setWrapText(true);
        errorIcon.setTranslateY(3);
        StackPane errorLabelContainer = new StackPane();
        errorLabelContainer.getChildren().add(errorLabel);
        StackPane.setAlignment(errorLabel, Pos.CENTER_LEFT);

        promptContainer = new StackPane();

        line.getStyleClass().add("input-line");
        focusedLine.getStyleClass().add("input-focused-line");
        // draw lines
        line.setPrefHeight(1);
        line.setTranslateY(1 + 4 + 2); // translate = prefHeight + init_translation
        line.setBackground(new Background(new BackgroundFill(((JFXTextArea) getSkinnable()).getUnFocusColor(),
                                                             CornerRadii.EMPTY, Insets.EMPTY)));
        if (getSkinnable().isDisabled()) {
            line.setBorder(new Border(new BorderStroke(((JFXTextArea) getSkinnable()).getUnFocusColor(),
                                                       BorderStrokeStyle.DASHED,
                                                       CornerRadii.EMPTY,
                                                       new BorderWidths(1))));
            line.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
                                                                 CornerRadii.EMPTY, Insets.EMPTY)));
        }

        // focused line
        focusedLine.setPrefHeight(2);
        focusedLine.setTranslateY(0 + 4 + 2); // translate = prefHeight + init_translation(-1)
        focusedLine.setBackground(new Background(new BackgroundFill(((JFXTextArea) getSkinnable()).getFocusColor(),
                                                                    CornerRadii.EMPTY, Insets.EMPTY)));
        focusedLine.setOpacity(0);
        focusedLine.getTransforms().add(scale);


        errorContainer = new HBox();
        errorContainer.getChildren().setAll(errorLabelContainer, errorIcon);
        HBox.setHgrow(errorLabelContainer, Priority.ALWAYS);

        errorContainer.setSpacing(10);
        errorContainer.setVisible(false);
        errorContainer.setOpacity(0);

        getChildren().addAll(line, focusedLine, promptContainer, errorContainer);

        getSkinnable().setBackground(transparentBackground);

        //		errorContainer.layoutXProperty().bind(scrollPane.layoutXProperty());
        //		errorContainer.layoutYProperty().bind(scrollPane.layoutYProperty());


        // add listeners to show error label
        errorLabel.heightProperty().addListener((o, oldVal, newVal) -> {
            if (errorShown) {
                if (oldErrorLabelHeight == -1)
                    oldErrorLabelHeight = errorLabelInitHeight = oldVal.doubleValue();

                heightChanged = true;
                double newHeight = this.getSkinnable().getHeight() - oldErrorLabelHeight + newVal.doubleValue();
                //				// show the error
                //				Timeline errorAnimation = new Timeline(
                //						new KeyFrame(Duration.ZERO, new KeyValue(getSkinnable().minHeightProperty(), currentFieldHeight,  Interpolator.EASE_BOTH)),
                //						new KeyFrame(Duration.millis(160),
                //								// text pane animation
                //								new KeyValue(mainPane.translateYProperty(), (initYlayout + mainPane.getMaxHeight()/2) - newHeight/2, Interpolator.EASE_BOTH),
                //								// animate the height change effect
                //								new KeyValue(getSkinnable().minHeightProperty(), newHeight, Interpolator.EASE_BOTH)));
                //				errorAnimation.play();
                //				// show the error label when finished
                //				errorAnimation.setOnFinished(finish->new Timeline(new KeyFrame(Duration.millis(160),new KeyValue(errorContainer.opacityProperty(), 1, Interpolator.EASE_BOTH))).play());
                currentFieldHeight = newHeight;
                oldErrorLabelHeight = newVal.doubleValue();
            }
        });
        errorContainer.visibleProperty().addListener((o, oldVal, newVal) -> {
            // show the error label if it's not shown
            if (newVal) new Timeline(new KeyFrame(Duration.millis(160),
                                                  new KeyValue(errorContainer.opacityProperty(),
                                                               1,
                                                               Interpolator.EASE_BOTH))).play();
        });


        textArea.labelFloatProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) JFXUtilities.runInFX(() -> createFloatingLabel());
            else promptText.visibleProperty().bind(usePromptText);
            createFocusTransition();
        });

        textArea.activeValidatorProperty().addListener((o, oldVal, newVal) -> {
            if (scrollPane != null) {
                if (!((JFXTextArea) getSkinnable()).isDisableAnimation()) {
                    if (hideErrorAnimation != null && hideErrorAnimation.getStatus().equals(Status.RUNNING))
                        hideErrorAnimation.stop();
                    if (newVal != null) {
                        hideErrorAnimation = new Timeline(new KeyFrame(Duration.millis(160),
                                                                       new KeyValue(errorContainer.opacityProperty(),
                                                                                    0,
                                                                                    Interpolator.EASE_BOTH)));
                        hideErrorAnimation.setOnFinished(finish -> {
                            errorContainer.setVisible(false);
                            JFXUtilities.runInFX(() -> showError(newVal));
                        });
                        hideErrorAnimation.play();
                    } else {
                        JFXUtilities.runInFX(() -> hideError());
                    }
                } else {
                    if (newVal != null) JFXUtilities.runInFXAndWait(() -> showError(newVal));
                    else JFXUtilities.runInFXAndWait(() -> hideError());
                }
            }
        });

        textArea.focusColorProperty().addListener((o, oldVal, newVal) -> {
            if (newVal != null) {
                focusedLine.setBackground(new Background(new BackgroundFill(newVal, CornerRadii.EMPTY, Insets.EMPTY)));
                if (((JFXTextArea) getSkinnable()).isLabelFloat()) {
                    promptTextColorTransition = new CachedTransition(promptContainer, new Timeline(
                        new KeyFrame(Duration.millis(1300),
                                     new KeyValue(promptTextFill, newVal, Interpolator.EASE_BOTH)))) {
                        {
                            setDelay(Duration.millis(0));
                            setCycleDuration(Duration.millis(160));
                        }

                        protected void starting() {
                            super.starting();
                            oldPromptTextFill = promptTextFill.get();
                        }
                    };
                    // reset transition
                    transition = null;
                }
            }
        });
        textArea.unFocusColorProperty().addListener((o, oldVal, newVal) -> {
            if (newVal != null)
                line.setBackground(new Background(new BackgroundFill(newVal, CornerRadii.EMPTY, Insets.EMPTY)));
        });

        // handle animation on focus gained/lost event
        textArea.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) focus();
            else unFocus();
        });

        // handle text changing at runtime
        textArea.textProperty().addListener((o, oldVal, newVal) -> {
            if (!getSkinnable().isFocused() && ((JFXTextArea) getSkinnable()).isLabelFloat()) {
                if (newVal == null || newVal.isEmpty()) animateFLoatingLabel(false);
                else animateFLoatingLabel(true);
            }
        });

        textArea.backgroundProperty().addListener((o, oldVal, newVal) -> {
            // Force transparent background
            if (oldVal == transparentBackground && newVal != transparentBackground) {
                textArea.setBackground(transparentBackground);
            }
        });

        textArea.disabledProperty().addListener((o, oldVal, newVal) -> {
            line.setBorder(newVal ? new Border(new BorderStroke(((JFXTextArea) getSkinnable()).getUnFocusColor(),
                                                                BorderStrokeStyle.DASHED,
                                                                CornerRadii.EMPTY,
                                                                new BorderWidths(line.getHeight()))) : Border.EMPTY);
            line.setBackground(new Background(new BackgroundFill(newVal ? Color.TRANSPARENT : ((JFXTextArea) getSkinnable())
                .getUnFocusColor(),
                                                                 CornerRadii.EMPTY, Insets.EMPTY)));
        });

        // prevent setting prompt text fill to transparent when text field is focused (override java transparent color if the control was focused)
        promptTextFill.addListener((o, oldVal, newVal) -> {
            if (Color.TRANSPARENT.equals(newVal) && ((JFXTextArea) getSkinnable()).isLabelFloat()) {
                promptTextFill.set(oldVal);
            }
        });
    }


    @Override
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        super.layoutChildren(x, y, w, h);

        // change control properties if and only if animations are stopped
        if ((transition == null || transition.getStatus().equals(Status.STOPPED))) {
            if (getSkinnable().isFocused() && ((JFXTextArea) getSkinnable()).isLabelFloat()) {
                promptTextFill.set(((JFXTextArea) getSkinnable()).getFocusColor());
            }
        }

        if (invalid) {
            invalid = false;
//			// set the default background of text area viewport to white
            Region viewPort = ((Region) scrollPane.getChildrenUnmodifiable().get(0));
            viewPort.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
                                                                     CornerRadii.EMPTY,
                                                                     Insets.EMPTY)));
            // reapply css of scroll pane in case set by the user
            viewPort.applyCss();

//			errorLabel.maxWidthProperty().bind(Bindings.createDoubleBinding(()->getSkinnable().getWidth()/1.14, getSkinnable().widthProperty()));

            // create floating label
            createFloatingLabel();
            // to position the prompt node properly
            super.layoutChildren(x, y, w, h);
            // update validation container
            if (((JFXTextArea) getSkinnable()).getActiveValidator() != null) updateValidationError();
            // focus
            createFocusTransition();
            if (getSkinnable().isFocused()) focus();
        }

        focusedLine.resizeRelocate(x, h - focusedLine.prefHeight(-1), w, focusedLine.prefHeight(-1));
        line.resizeRelocate(x, h - focusedLine.prefHeight(-1), w, line.prefHeight(-1));
        errorContainer.resizeRelocate(x, y, w, -1);
        errorContainer.setTranslateY(h + focusedLine.getHeight() + 4);
        scale.setPivotX(w / 2);
    }

    private void updateValidationError() {
        if (hideErrorAnimation != null && hideErrorAnimation.getStatus().equals(Status.RUNNING))
            hideErrorAnimation.stop();
        hideErrorAnimation = new Timeline(
            new KeyFrame(Duration.millis(160),
                         new KeyValue(errorContainer.opacityProperty(), 0, Interpolator.EASE_BOTH)));
        hideErrorAnimation.setOnFinished(finish -> {
            errorContainer.setVisible(false);
            showError(((JFXTextArea) getSkinnable()).getActiveValidator());
        });
        hideErrorAnimation.play();
    }

    private void createFloatingLabel() {
        if (((JFXTextArea) getSkinnable()).isLabelFloat()) {
            if (promptText == null) {
                // get the prompt text node or create it
                boolean triggerFloatLabel = false;
                if (((Region) scrollPane.getContent()).getChildrenUnmodifiable().get(0) instanceof Text)
                    promptText = (Text) ((Region) scrollPane.getContent()).getChildrenUnmodifiable().get(0);
                else {
                    Field field;
                    try {
                        field = TextAreaSkin.class.getDeclaredField("promptNode");
                        field.setAccessible(true);
                        createPromptNode();
                        field.set(this, promptText);
                        // position the prompt node in its position
                        triggerFloatLabel = true;
                        oldPromptTextFill = promptTextFill.get();
                    } catch (NoSuchFieldException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                // fixed issue text area is being resized when the content is excedeing its width
                promptText.wrappingWidthProperty().addListener((o, oldval, newVal) -> {
                    if (newVal.doubleValue() > getSkinnable().getWidth())
                        promptText.setWrappingWidth(getSkinnable().getWidth());
                });

                promptText.getTransforms().add(promptTextScale);
                promptContainer.getChildren().add(promptText);
                if (triggerFloatLabel) {
                    promptText.setTranslateY(-promptText.getBoundsInLocal().getHeight() - 2);
                    promptTextScale.setX(0.85);
                    promptTextScale.setY(0.85);
                }
            }

            // create prompt animations
            promptTextUpTransition = new CachedTransition(promptContainer, new Timeline(
                new KeyFrame(Duration.millis(1300),
                             new KeyValue(promptText.translateYProperty(),
                                          -promptText.getLayoutBounds().getHeight() - 2,
                                          Interpolator.EASE_BOTH),
                             new KeyValue(promptTextScale.xProperty(), 0.85, Interpolator.EASE_BOTH),
                             new KeyValue(promptTextScale.yProperty(), 0.85, Interpolator.EASE_BOTH)))) {{
                setDelay(Duration.millis(0));
                setCycleDuration(Duration.millis(240));
            }};

            promptTextColorTransition = new CachedTransition(promptContainer, new Timeline(
                new KeyFrame(Duration.millis(1300),
                             new KeyValue(promptTextFill,
                                          ((JFXTextArea) getSkinnable()).getFocusColor(),
                                          Interpolator.EASE_BOTH)))) {
                {
                    setDelay(Duration.millis(0));
                    setCycleDuration(Duration.millis(160));
                }

                protected void starting() {
                    super.starting();
                    oldPromptTextFill = promptTextFill.get();
                }

                ;
            };

            promptTextDownTransition = new CachedTransition(promptContainer, new Timeline(
                new KeyFrame(Duration.millis(1300),
                             new KeyValue(promptText.translateYProperty(), 0, Interpolator.EASE_BOTH),
                             new KeyValue(promptTextScale.xProperty(), 1, Interpolator.EASE_BOTH),
                             new KeyValue(promptTextScale.yProperty(), 1, Interpolator.EASE_BOTH))
            )) {{
                setDelay(Duration.millis(0));
                setCycleDuration(Duration.millis(240));
            }};
            promptTextDownTransition.setOnFinished((finish) -> {
                promptText.setTranslateY(0);
                promptTextScale.setX(1);
                promptTextScale.setY(1);
            });

            promptText.visibleProperty().unbind();
            promptText.visibleProperty().set(true);
        }
    }

    private void createPromptNode() {
        promptText = new Text();
        promptText.setManaged(false);
        promptText.getStyleClass().add("text");
        promptText.visibleProperty().bind(usePromptText);
        promptText.fontProperty().bind(getSkinnable().fontProperty());
        promptText.textProperty().bind(getSkinnable().promptTextProperty());
        promptText.fillProperty().bind(promptTextFill);
        promptText.setLayoutX(1);
    }

    private void focus() {
        /*
		 * in case the method request layout is not called before focused
		 * this is bug is reported while editing treetableview cells
		 */
        if (scrollPane == null) {
            Platform.runLater(() -> focus());
        } else {
            // create the focus animations
            if (transition == null) createFocusTransition();
            transition.play();
        }
    }

    private void createFocusTransition() {
        transition = new ParallelTransition();
        if (((JFXTextArea) getSkinnable()).isLabelFloat()) {
            transition.getChildren().add(promptTextUpTransition);
            transition.getChildren().add(promptTextColorTransition);
        }
        transition.getChildren().add(linesAnimation);
    }

    private void unFocus() {
        if (transition != null) transition.stop();
        scale.setX(initScale);
        focusedLine.setOpacity(0);
        if (((JFXTextArea) getSkinnable()).isLabelFloat() && oldPromptTextFill != null) {
            promptTextFill.set(oldPromptTextFill);
            if (usePromptText()) promptTextDownTransition.play();
        }
    }

    /**
     * this method is called when the text property is changed when the
     * field is not focused (changed in code)
     *
     * @param up
     */
    private void animateFLoatingLabel(boolean up) {
        if (promptText == null) {
            Platform.runLater(() -> animateFLoatingLabel(up));
        } else {
            if (transition != null) {
                transition.stop();
                transition.getChildren().remove(promptTextUpTransition);
                transition = null;
            }
            if (up && promptContainer.getTranslateY() == 0) {
                promptTextDownTransition.stop();
                promptTextUpTransition.play();
            } else if (!up) {
                promptTextUpTransition.stop();
                promptTextDownTransition.play();
            }
        }
    }

    private boolean usePromptText() {
        String txt = getSkinnable().getText();
        String promptTxt = getSkinnable().getPromptText();
        boolean hasPromptText = (txt == null || txt.isEmpty()) && promptTxt != null && !promptTxt.isEmpty() && !promptTextFill
            .get()
            .equals(Color.TRANSPARENT);
        return hasPromptText;
    }

    private void showError(ValidatorBase validator) {
        // set text in error label
        errorLabel.setText(validator.getMessage());
        // show error icon
        Node awsomeIcon = validator.getIcon();
        errorIcon.getChildren().clear();
        if (awsomeIcon != null) {
            errorIcon.getChildren().add(awsomeIcon);
            StackPane.setAlignment(awsomeIcon, Pos.TOP_RIGHT);
        }
        // init only once, to fix the text pane from resizing
        if (initYLayout == -1) {
            scrollPane.setMaxHeight(scrollPane.getHeight());
            initYLayout = scrollPane.getBoundsInParent().getMinY();
            initHeight = getSkinnable().getHeight();
            currentFieldHeight = initHeight;
        }
        errorContainer.setVisible(true);
        errorShown = true;
    }

    private void hideError() {
        if (heightChanged) {
            new Timeline(new KeyFrame(Duration.millis(160),
                                      new KeyValue(scrollPane.translateYProperty(), 0, Interpolator.EASE_BOTH))).play();
            // reset the height of text field
            new Timeline(new KeyFrame(Duration.millis(160),
                                      new KeyValue(getSkinnable().minHeightProperty(),
                                                   initHeight,
                                                   Interpolator.EASE_BOTH))).play();
            heightChanged = false;
        }
        // clear error label text
        errorLabel.setText(null);
        oldErrorLabelHeight = errorLabelInitHeight;
        // clear error icon
        errorIcon.getChildren().clear();
        // reset the height of the text field
        currentFieldHeight = initHeight;
        // hide error container
        errorContainer.setVisible(false);
        errorShown = false;
    }
}
