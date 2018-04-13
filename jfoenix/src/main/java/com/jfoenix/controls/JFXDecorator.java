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

import com.jfoenix.svg.SVGGlyph;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;


/**
 * Window Decorator allow to resize/move its content Note: the default close button will call stage.close() which will
 * only close the current stage. it will not close the java application, however it can be customized by calling {@link
 * #setOnCloseButtonAction(Runnable)}
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXDecorator extends VBox {

    private Stage primaryStage;

    private double xOffset = 0;
    private double yOffset = 0;
    private double newX, newY, initX, initY, initWidth = -1, initHeight = -1,
        initStageX = -1, initStageY = -1;

    private boolean allowMove = false;
    private boolean isDragging = false;
    private Timeline windowDecoratorAnimation;
    private StackPane contentPlaceHolder = new StackPane();
    private HBox buttonsContainer;

    private ObjectProperty<Runnable> onCloseButtonAction = new SimpleObjectProperty<>(() ->
        primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST)));

    private BooleanProperty customMaximize = new SimpleBooleanProperty(false);
    private boolean maximized = false;
    private BoundingBox originalBox;
    private BoundingBox maximizedBox;

    protected JFXButton btnMax;
    protected JFXButton btnFull;
    protected JFXButton btnClose;
    protected JFXButton btnMin;

    protected StringProperty title = new SimpleStringProperty();
    protected Text text;
    protected Node graphic;
    protected HBox graphicContainer;

    /**
     * Create a window decorator for the specified node with the options:
     * - full screen
     * - maximize
     * - minimize
     *
     * @param stage the primary stage used by the application
     * @param node  the node to be decorated
     */
    public JFXDecorator(Stage stage, Node node) {
        this(stage, node, true, true, true);
    }

    /**
     * Create a window decorator for the specified node with the options:
     * - full screen
     * - maximize
     * - minimize
     *
     * @param stage      the primary stage used by the application
     * @param node       the node to be decorated
     * @param fullScreen indicates whether to show full screen option or not
     * @param max        indicates whether to show maximize option or not
     * @param min        indicates whether to show minimize option or not
     */
    public JFXDecorator(Stage stage, Node node, boolean fullScreen, boolean max, boolean min) {
        primaryStage = stage;
        // Note that setting the style to TRANSPARENT is causing performance
        // degradation, as an alternative we set it to UNDECORATED instead.
        primaryStage.initStyle(StageStyle.UNDECORATED);

        setPickOnBounds(false);
        getStyleClass().add("jfx-decorator");

        initializeButtons();
        initializeContainers(node, fullScreen, max, min);

        primaryStage.fullScreenProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                // remove border
                contentPlaceHolder.getStyleClass().remove("resize-border");
                /*
                 *  note the border property MUST NOT be bound to another property
				 *  when going full screen mode, thus the binding will be lost if exisited
				 */
                contentPlaceHolder.borderProperty().unbind();
                contentPlaceHolder.setBorder(Border.EMPTY);
                if (windowDecoratorAnimation != null) {
                    windowDecoratorAnimation.stop();
                }
                windowDecoratorAnimation = new Timeline(new KeyFrame(Duration.millis(320),
                    new KeyValue(this.translateYProperty(),
                        -buttonsContainer.getHeight(),
                        Interpolator.EASE_BOTH)));
                windowDecoratorAnimation.setOnFinished((finish) -> {
                    this.getChildren().remove(buttonsContainer);
                    this.setTranslateY(0);
                });
                windowDecoratorAnimation.play();
            } else {
                // add border
                if (windowDecoratorAnimation != null) {
                    if (windowDecoratorAnimation.getStatus() == Animation.Status.RUNNING) {
                        windowDecoratorAnimation.stop();
                    } else {
                        this.getChildren().add(0, buttonsContainer);
                    }
                }
                this.setTranslateY(-buttonsContainer.getHeight());
                windowDecoratorAnimation = new Timeline(new KeyFrame(Duration.millis(320),
                    new KeyValue(this.translateYProperty(),
                        0,
                        Interpolator.EASE_BOTH)));
                windowDecoratorAnimation.setOnFinished((finish) -> {
                    contentPlaceHolder.setBorder(new Border(new BorderStroke(Color.BLACK,
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        new BorderWidths(0, 4, 4, 4))));
                    contentPlaceHolder.getStyleClass().add("resize-border");
                });
                windowDecoratorAnimation.play();
            }
        });

        contentPlaceHolder.addEventHandler(MouseEvent.MOUSE_PRESSED, (mouseEvent) ->
            updateInitMouseValues(mouseEvent));
        buttonsContainer.addEventHandler(MouseEvent.MOUSE_PRESSED, (mouseEvent) ->
            updateInitMouseValues(mouseEvent));

        // show the drag cursor on the borders
        addEventFilter(MouseEvent.MOUSE_MOVED, (mouseEvent) -> showDragCursorOnTheborders(mouseEvent));


        // handle drag events on the decorator pane
        addEventFilter(MouseEvent.MOUSE_RELEASED, (mouseEvent) -> isDragging = false);
        this.setOnMouseDragged((mouseEvent) -> handleDragEventOnDecoratorPane(mouseEvent));
    }

    private void initializeButtons() {

        SVGGlyph full = new SVGGlyph(0,
            "FULLSCREEN",
            "M598 214h212v212h-84v-128h-128v-84zM726 726v-128h84v212h-212v-84h128zM214 426v-212h212v84h-128v128h-84zM298 598v128h128v84h-212v-212h84z",
            Color.WHITE);
        full.setSize(16, 16);
        SVGGlyph minus = new SVGGlyph(0,
            "MINUS",
            "M804.571 420.571v109.714q0 22.857-16 38.857t-38.857 16h-694.857q-22.857 0-38.857-16t-16-38.857v-109.714q0-22.857 16-38.857t38.857-16h694.857q22.857 0 38.857 16t16 38.857z",
            Color.WHITE);
        minus.setSize(12, 2);
        minus.setTranslateY(4);
        SVGGlyph resizeMax = new SVGGlyph(0,
            "RESIZE_MAX",
            "M726 810v-596h-428v596h428zM726 44q34 0 59 25t25 59v768q0 34-25 60t-59 26h-428q-34 0-59-26t-25-60v-768q0-34 25-60t59-26z",
            Color.WHITE);
        resizeMax.setSize(12, 12);
        SVGGlyph resizeMin = new SVGGlyph(0,
            "RESIZE_MIN",
            "M80.842 943.158v-377.264h565.894v377.264h-565.894zM0 404.21v619.79h727.578v-619.79h-727.578zM377.264 161.684h565.894v377.264h-134.736v80.842h215.578v-619.79h-727.578v323.37h80.842v-161.686z",
            Color.WHITE);
        resizeMin.setSize(12, 12);
        SVGGlyph close = new SVGGlyph(0,
            "CLOSE",
            "M810 274l-238 238 238 238-60 60-238-238-238 238-60-60 238-238-238-238 60-60 238 238 238-238z",
            Color.WHITE);
        close.setSize(12, 12);
        btnFull = new JFXButton();
        btnFull.getStyleClass().add("jfx-decorator-button");
        btnFull.setCursor(Cursor.HAND);
        btnFull.setOnAction((action) -> primaryStage.setFullScreen(!primaryStage.isFullScreen()));
        btnFull.setGraphic(full);
        btnFull.setTranslateX(-30);
        btnFull.setRipplerFill(Color.WHITE);

        btnClose = new JFXButton();
        btnClose.getStyleClass().add("jfx-decorator-button");
        btnClose.setCursor(Cursor.HAND);
        btnClose.setOnAction((action) -> onCloseButtonAction.get().run());
        btnClose.setGraphic(close);
        btnClose.setRipplerFill(Color.WHITE);

        btnMin = new JFXButton();
        btnMin.getStyleClass().add("jfx-decorator-button");
        btnMin.setCursor(Cursor.HAND);
        btnMin.setOnAction((action) -> primaryStage.setIconified(true));
        btnMin.setGraphic(minus);
        btnMin.setRipplerFill(Color.WHITE);

        btnMax = new JFXButton();
        btnMax.getStyleClass().add("jfx-decorator-button");
        btnMax.setCursor(Cursor.HAND);
        btnMax.setRipplerFill(Color.WHITE);
        btnMax.setOnAction((action) -> maximize(resizeMin, resizeMax));
        btnMax.setGraphic(resizeMax);
    }

    private void maximize(SVGGlyph resizeMin, SVGGlyph resizeMax) {
        if (!isCustomMaximize()) {
            primaryStage.setMaximized(!primaryStage.isMaximized());
            maximized = primaryStage.isMaximized();
            if (primaryStage.isMaximized()) {
                btnMax.setGraphic(resizeMin);
                btnMax.setTooltip(new Tooltip("Restore Down"));
            } else {
                btnMax.setGraphic(resizeMax);
                btnMax.setTooltip(new Tooltip("Maximize"));
            }
        } else {
            if (!maximized) {
                // store original bounds
                originalBox = new BoundingBox(primaryStage.getX(), primaryStage.getY(), primaryStage.getWidth(), primaryStage.getHeight());
                // get the max stage bounds
                Screen screen = Screen.getScreensForRectangle(primaryStage.getX(),
                    primaryStage.getY(),
                    primaryStage.getWidth(),
                    primaryStage.getHeight()).get(0);
                Rectangle2D bounds = screen.getVisualBounds();
                maximizedBox = new BoundingBox(bounds.getMinX(),
                    bounds.getMinY(),
                    bounds.getWidth(),
                    bounds.getHeight());
                // maximized the stage
                primaryStage.setX(maximizedBox.getMinX());
                primaryStage.setY(maximizedBox.getMinY());
                primaryStage.setWidth(maximizedBox.getWidth());
                primaryStage.setHeight(maximizedBox.getHeight());
                btnMax.setGraphic(resizeMin);
                btnMax.setTooltip(new Tooltip("Restore Down"));
            } else {
                // restore stage to its original size
                primaryStage.setX(originalBox.getMinX());
                primaryStage.setY(originalBox.getMinY());
                primaryStage.setWidth(originalBox.getWidth());
                primaryStage.setHeight(originalBox.getHeight());
                originalBox = null;
                btnMax.setGraphic(resizeMax);
                btnMax.setTooltip(new Tooltip("Maximize"));
            }
            maximized = !maximized;
        }
    }

    private void initializeContainers(Node node, boolean fullScreen, boolean max, boolean min) {
        buttonsContainer = new HBox();
        buttonsContainer.getStyleClass().add("jfx-decorator-buttons-container");
        buttonsContainer.setBackground(new Background(new BackgroundFill(Color.BLACK,
            CornerRadii.EMPTY,
            Insets.EMPTY)));
        // BINDING
        buttonsContainer.setPadding(new Insets(4));
        buttonsContainer.setAlignment(Pos.CENTER_RIGHT);

        // customize decorator buttons
        List<JFXButton> btns = new ArrayList<>();
        if (fullScreen) {
            btns.add(btnFull);
        }
        if (min) {
            btns.add(btnMin);
        }
        if (max) {
            btns.add(btnMax);
            // maximize/restore the window on header double click
            buttonsContainer.addEventHandler(MouseEvent.MOUSE_CLICKED, (mouseEvent) -> {
                if (mouseEvent.getClickCount() == 2) {
                    btnMax.fire();
                }
            });
        }
        btns.add(btnClose);

        text = new Text();
        text.getStyleClass().addAll("jfx-decorator-text", "title", "jfx-decorator-title");
        text.setFill(Color.WHITE);
        text.textProperty().bind(title); //binds the Text's text to title
        title.bind(primaryStage.titleProperty()); //binds title to the primaryStage's title

        graphicContainer = new HBox();
        graphicContainer.setPickOnBounds(false);
        graphicContainer.setAlignment(Pos.CENTER_LEFT);
        graphicContainer.getChildren().setAll(text);

        HBox graphicTextContainer = new HBox(graphicContainer, text);
        graphicTextContainer.getStyleClass().add("jfx-decorator-title-container");
        graphicTextContainer.setAlignment(Pos.CENTER_LEFT);
        graphicTextContainer.setPickOnBounds(false);
        HBox.setHgrow(graphicTextContainer, Priority.ALWAYS);
        HBox.setMargin(graphicContainer, new Insets(0, 8 , 0, 8));

        buttonsContainer.getChildren().setAll(graphicTextContainer);
        buttonsContainer.getChildren().addAll(btns);
        buttonsContainer.addEventHandler(MouseEvent.MOUSE_ENTERED, (enter) -> allowMove = true);
        buttonsContainer.addEventHandler(MouseEvent.MOUSE_EXITED, (enter) -> {
            if (!isDragging) {
                allowMove = false;
            }
        });
        buttonsContainer.setMinWidth(180);
        contentPlaceHolder.getStyleClass().add("jfx-decorator-content-container");
        contentPlaceHolder.setMinSize(0, 0);
        contentPlaceHolder.getChildren().add(node);
        ((Region) node).setMinSize(0, 0);
        VBox.setVgrow(contentPlaceHolder, Priority.ALWAYS);
        contentPlaceHolder.getStyleClass().add("resize-border");
        contentPlaceHolder.setBorder(new Border(new BorderStroke(Color.BLACK,
            BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY,
            new BorderWidths(0, 4, 4, 4))));
        // BINDING

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(((Region) node).widthProperty());
        clip.heightProperty().bind(((Region) node).heightProperty());
        node.setClip(clip);
        this.getChildren().addAll(buttonsContainer, contentPlaceHolder);
    }

    private void showDragCursorOnTheborders(MouseEvent mouseEvent) {
        if (primaryStage.isMaximized() || primaryStage.isFullScreen() || maximized) {
            this.setCursor(Cursor.DEFAULT);
            return; // maximized mode does not support resize
        }
        if (!primaryStage.isResizable()) {
            return;
        }
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        Bounds boundsInParent = this.getBoundsInParent();
        if (contentPlaceHolder.getBorder() != null && contentPlaceHolder.getBorder().getStrokes().size() > 0) {
            double borderWidth = contentPlaceHolder.snappedLeftInset();
            if (isRightEdge(x, y, boundsInParent)) {
                if (y < borderWidth) {
                    this.setCursor(Cursor.NE_RESIZE);
                } else if (y > this.getHeight() - borderWidth) {
                    this.setCursor(Cursor.SE_RESIZE);
                } else {
                    this.setCursor(Cursor.E_RESIZE);
                }
            } else if (isLeftEdge(x, y, boundsInParent)) {
                if (y < borderWidth) {
                    this.setCursor(Cursor.NW_RESIZE);
                } else if (y > this.getHeight() - borderWidth) {
                    this.setCursor(Cursor.SW_RESIZE);
                } else {
                    this.setCursor(Cursor.W_RESIZE);
                }
            } else if (isTopEdge(x, y, boundsInParent)) {
                this.setCursor(Cursor.N_RESIZE);
            } else if (isBottomEdge(x, y, boundsInParent)) {
                this.setCursor(Cursor.S_RESIZE);
            } else {
                this.setCursor(Cursor.DEFAULT);
            }
        }
    }

    private void handleDragEventOnDecoratorPane(MouseEvent mouseEvent) {
        isDragging = true;
        if (!mouseEvent.isPrimaryButtonDown() || (xOffset == -1 && yOffset == -1)) {
            return;
        }
            /*
             * Long press generates drag event!
			 */
        if (primaryStage.isFullScreen() || mouseEvent.isStillSincePress() || primaryStage.isMaximized() || maximized) {
            return;
        }

        newX = mouseEvent.getScreenX();
        newY = mouseEvent.getScreenY();


        double deltax = newX - initX;
        double deltay = newY - initY;
        Cursor cursor = this.getCursor();

        if (Cursor.E_RESIZE.equals(cursor)) {
            setStageWidth(initWidth + deltax);
            mouseEvent.consume();
        } else if (Cursor.NE_RESIZE.equals(cursor)) {
            if (setStageHeight(initHeight - deltay)) {
                primaryStage.setY(initStageY + deltay);
            }
            setStageWidth(initWidth + deltax);
            mouseEvent.consume();
        } else if (Cursor.SE_RESIZE.equals(cursor)) {
            setStageWidth(initWidth + deltax);
            setStageHeight(initHeight + deltay);
            mouseEvent.consume();
        } else if (Cursor.S_RESIZE.equals(cursor)) {
            setStageHeight(initHeight + deltay);
            mouseEvent.consume();
        } else if (Cursor.W_RESIZE.equals(cursor)) {
            if (setStageWidth(initWidth - deltax)) {
                primaryStage.setX(initStageX + deltax);
            }
            mouseEvent.consume();
        } else if (Cursor.SW_RESIZE.equals(cursor)) {
            if (setStageWidth(initWidth - deltax)) {
                primaryStage.setX(initStageX + deltax);
            }
            setStageHeight(initHeight + deltay);
            mouseEvent.consume();
        } else if (Cursor.NW_RESIZE.equals(cursor)) {
            if (setStageWidth(initWidth - deltax)) {
                primaryStage.setX(initStageX + deltax);
            }
            if (setStageHeight(initHeight - deltay)) {
                primaryStage.setY(initStageY + deltay);
            }
            mouseEvent.consume();
        } else if (Cursor.N_RESIZE.equals(cursor)) {
            if (setStageHeight(initHeight - deltay)) {
                primaryStage.setY(initStageY + deltay);
            }
            mouseEvent.consume();
        } else if (allowMove) {
            primaryStage.setX(mouseEvent.getScreenX() - xOffset);
            primaryStage.setY(mouseEvent.getScreenY() - yOffset);
            mouseEvent.consume();
        }
    }

    private void updateInitMouseValues(MouseEvent mouseEvent) {
        initStageX = primaryStage.getX();
        initStageY = primaryStage.getY();
        initWidth = primaryStage.getWidth();
        initHeight = primaryStage.getHeight();
        initX = mouseEvent.getScreenX();
        initY = mouseEvent.getScreenY();
        xOffset = mouseEvent.getSceneX();
        yOffset = mouseEvent.getSceneY();
    }


    private boolean isRightEdge(double x, double y, Bounds boundsInParent) {
        return x < this.getWidth() && x > this.getWidth() - contentPlaceHolder.snappedLeftInset();
    }

    private boolean isTopEdge(double x, double y, Bounds boundsInParent) {
        return y >= 0 && y < contentPlaceHolder.snappedLeftInset();
    }

    private boolean isBottomEdge(double x, double y, Bounds boundsInParent) {
        return y < this.getHeight() && y > this.getHeight() - contentPlaceHolder.snappedLeftInset();
    }

    private boolean isLeftEdge(double x, double y, Bounds boundsInParent) {
        return x >= 0 && x < contentPlaceHolder.snappedLeftInset();
    }

    boolean setStageWidth(double width) {
        if (width >= primaryStage.getMinWidth() && width >= buttonsContainer.getMinWidth()) {
            primaryStage.setWidth(width);
//            initX = newX;
            return true;
        } else if (width >= primaryStage.getMinWidth() && width <= buttonsContainer.getMinWidth()) {
            width = buttonsContainer.getMinWidth();
            primaryStage.setWidth(width);
        }
        return false;
    }

    boolean setStageHeight(double height) {
        if (height >= primaryStage.getMinHeight() && height >= buttonsContainer.getHeight()) {
            primaryStage.setHeight(height);
//            initY = newY;
            return true;
        } else if (height >= primaryStage.getMinHeight() && height <= buttonsContainer.getHeight()) {
            height = buttonsContainer.getHeight();
            primaryStage.setHeight(height);
        }
        return false;
    }

    /**
     * set a speficed runnable when clicking on the close button
     *
     * @param onCloseButtonAction runnable to be executed
     */
    public void setOnCloseButtonAction(Runnable onCloseButtonAction) {
        this.onCloseButtonAction.set(onCloseButtonAction);
    }

    /**
     * this property is used to replace JavaFX maximization
     * with a custom one that prevents hiding windows taskbar when
     * the JFXDecorator is maximized.
     *
     * @return customMaximizeProperty whether to use custom maximization or not.
     */
    public final BooleanProperty customMaximizeProperty() {
        return this.customMaximize;
    }

    /**
     * @return whether customMaximizeProperty is active or not
     */
    public final boolean isCustomMaximize() {
        return this.customMaximizeProperty().get();
    }

    /**
     * set customMaximize property
     *
     * @param customMaximize
     */
    public final void setCustomMaximize(final boolean customMaximize) {
        this.customMaximizeProperty().set(customMaximize);
    }

    /**
     * @param maximized
     */
    public void setMaximized(boolean maximized) {
        if (this.maximized != maximized) {
            Platform.runLater(() -> {
                btnMax.fire();
            });
        }
    }

    /**
     * will change the decorator content
     *
     * @param content
     */
    public void setContent(Node content) {
        this.contentPlaceHolder.getChildren().setAll(content);
    }

    /**
     * will set the title
     *
     * @param text
     * @deprecated Use {@link JFXDecorator#setTitle(java.lang.String)} instead.
     */
    public void setText(String text) {
        setTitle(text);
    }

    /**
     * will get the title
     *
     * @deprecated Use {@link JFXDecorator#setTitle(java.lang.String)} instead.
     */
    public String getText() {
        return getTitle();
    }

    public String getTitle() {
        return title.get();
    }

    /**
     * By default this title property is bound to the primaryStage's title property.
     * <p>
     * To change it to something else, use <pre>
     *     {@code jfxDecorator.titleProperty().unbind();}</pre> first.
     *
     */
    public StringProperty titleProperty() {
        return title;
    }

    /**
     * If you want the {@code primaryStage}'s title and the {@code JFXDecorator}'s title to be different, then
     * go ahead and use this method.
     * <p>
     * By default, this title property is bound to the {@code primaryStage}'s title property—so merely setting the
     * {@code primaryStage}'s title, will set the {@code JFXDecorator}'s title.
     *
     */
    public void setTitle(String title) {
        this.title.unbind();
        this.title.set(title);
    }

    public void setGraphic(Node node) {
        if (graphic != null) {
            graphicContainer.getChildren().remove(graphic);
        }
        if (node != null) {
            graphicContainer.getChildren().add(0, node);
        }
        graphic = node;
    }

    public Node getGraphic(Node node) {
        return graphic;
    }
}
