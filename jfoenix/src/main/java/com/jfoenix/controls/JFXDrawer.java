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

import com.jfoenix.controls.events.JFXDrawerEvent;
import com.jfoenix.transitions.JFXAnimationTimer;
import com.jfoenix.transitions.JFXKeyFrame;
import com.jfoenix.transitions.JFXKeyValue;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Duration;

import java.util.ArrayList;

/**
 * JFXDrawer is material design implementation of drawer.
 * the drawer has two main nodes, the content and side pane.
 * <ul>
 * <li><b>content pane:</b> is a stack pane that holds the nodes inside the drawer</li>
 * <li><b>side pane:</b> is a stack pane that holds the nodes inside the drawer side area (Drawable node)</li>
 * </ul>
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXDrawer extends StackPane {

    public enum DrawerDirection {
        LEFT(1), RIGHT(-1), TOP(1), BOTTOM(-1);
        private double numVal;

        DrawerDirection(double numVal) {
            this.numVal = numVal;
        }

        public double doubleValue() {
            return numVal;
        }
    }

    private StackPane overlayPane = new StackPane();
    StackPane sidePane = new StackPane();
    private StackPane content = new StackPane();
    private Transition drawerTransition;
    //	private Transition outTransition;
    private Transition partialTransition;
    private Duration holdTime = Duration.seconds(0.2);
    private PauseTransition holdTimer = new PauseTransition(holdTime);

    private double initOffset = 30;
    private DoubleProperty initTranslate = new SimpleDoubleProperty();
    private BooleanProperty overLayVisible = new SimpleBooleanProperty(true);
    private double activeOffset = 20;
    private double startMouse = -1;
    private double startTranslate = -1;
    private double startSize = -1;
    private DoubleProperty translateProperty = sidePane.translateXProperty();
    private boolean resizable = false;
    private boolean openCalled = false;
    private boolean closeCalled = true;

    private DoubleProperty defaultSizeProperty = new SimpleDoubleProperty();
    private ObjectProperty<DoubleProperty> maxSizeProperty =
        new SimpleObjectProperty<>(sidePane.maxWidthProperty());
    private ObjectProperty<DoubleProperty> minSizeProperty =
        new SimpleObjectProperty<>(sidePane.minWidthProperty());
    private ObjectProperty<DoubleProperty> prefSizeProperty =
        new SimpleObjectProperty<>(sidePane.prefWidthProperty());
    private ObjectProperty<ReadOnlyDoubleProperty> sizeProperty =
        new SimpleObjectProperty<>(sidePane.widthProperty());
    private ObjectProperty<ReadOnlyDoubleProperty> parentSizeProperty =
        new SimpleObjectProperty<>();
    private ObjectProperty<Node> boundedNode = new SimpleObjectProperty<>();

    private SimpleObjectProperty<DrawerDirection> directionProperty = new SimpleObjectProperty<>(
        DrawerDirection.LEFT);

    /***************************************************************************
     *                                                                         *
     * Animations                                                                *
     *                                                                         *
     **************************************************************************/

    double translateTo = 0;

    private double tempDrawerSize = getDefaultDrawerSize();

    private JFXAnimationTimer translateTimer = new JFXAnimationTimer(
        new JFXKeyFrame(Duration.millis(420),
            JFXKeyValue.builder()
                .setTargetSupplier(() -> translateProperty)
                .setEndValueSupplier(() -> translateTo)
                .setInterpolator(Interpolator.EASE_BOTH).build()),
        new JFXKeyFrame(Duration.millis(420),
            JFXKeyValue.builder()
                .setTargetSupplier(() -> prefSizeProperty.get())
                .setEndValueSupplier(() -> getDefaultDrawerSize())
                .setAnimateCondition(() -> translateTo == initTranslate.get())
                .setInterpolator(Interpolator.EASE_BOTH).build()),
        new JFXKeyFrame(Duration.millis(420),
            JFXKeyValue.builder()
                .setTargetSupplier(() -> maxSizeProperty.get())
                .setEndValueSupplier(() -> getDefaultDrawerSize())
                .setAnimateCondition(() -> translateTo == initTranslate.get())
                .setInterpolator(Interpolator.EASE_BOTH).build()),
        new JFXKeyFrame(Duration.millis(420),
            JFXKeyValue.builder()
                .setTargetSupplier(() -> prefSizeProperty.get())
                .setEndValueSupplier(() -> tempDrawerSize)
                .setAnimateCondition(() -> translateTo == 0 && tempDrawerSize > getDefaultDrawerSize())
                .setInterpolator(Interpolator.EASE_BOTH).build()),
        new JFXKeyFrame(Duration.millis(420),
            JFXKeyValue.builder()
                .setTargetSupplier(() -> maxSizeProperty.get())
                .setEndValueSupplier(() -> tempDrawerSize)
                .setAnimateCondition(() -> translateTo == 0 && tempDrawerSize > getDefaultDrawerSize())
                .setInterpolator(Interpolator.EASE_BOTH).build())
    );

    /**
     * creates empty drawer node
     */
    public JFXDrawer() {
        initialize();
        overlayPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.1),
            CornerRadii.EMPTY,
            Insets.EMPTY)));
        overlayPane.getStyleClass().add("jfx-drawer-overlay-pane");
        overlayPane.setOpacity(0);
        overlayPane.setMouseTransparent(true);

        sidePane.getStyleClass().add("jfx-drawer-side-pane");
        sidePane.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 1),
            CornerRadii.EMPTY,
            Insets.EMPTY)));
        sidePane.setPickOnBounds(false);
//        sidePane.setStyle("-fx-padding: 0 10 0 0; -fx-background-insets: 0 0 0 0, 50 10 50 50;"
//                          + "-fx-background-color: linear-gradient(from 100% 100% to 80% 100%, rgba(97, 97, 97, 0.0), rgba(00, 00, 00, 0.5)),"
//                          + "linear-gradient(from 0px 0px to 0px 5px, derive(RED, -9%), RED);");
        translateTimer.setCacheNodes(sidePane);
        // add listeners
        initListeners();
        //  init size value
        setDefaultDrawerSize(100);
        this.getChildren().addAll(overlayPane, sidePane);
    }

    private void initialize() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    private void initListeners() {
        updateDirection(directionProperty.get());
        initTranslate.bind(Bindings.createDoubleBinding(() ->
                -1 * directionProperty.get().doubleValue() * defaultSizeProperty.getValue()
                - initOffset * directionProperty.get().doubleValue(),
            defaultSizeProperty, directionProperty));

        // add listeners to update drawer properties
        overLayVisibleProperty().addListener(observable -> {
            final boolean overLayVisible = isOverLayVisible();
            overlayPane.setStyle(!overLayVisible ? "-fx-background-color : transparent;" : "");
            overlayPane.setPickOnBounds(overLayVisible);
        });

        directionProperty.addListener(observable -> updateDirection(directionProperty.get()));
        initTranslate.addListener(observable -> updateDrawerAnimation(initTranslate.get()));

        // mouse drag handler
        translateProperty.addListener(observable -> overlayPane.setOpacity(1 - translateProperty.doubleValue() / initTranslate.get()));

        // add opening/closing action listeners
        translateProperty.addListener((o, oldVal, newVal) -> {
            if (!openCalled && closeCalled
                && directionProperty.get().doubleValue() * newVal.doubleValue() >
                   directionProperty.get().doubleValue() * initTranslate.get() / 2) {
                openCalled = true;
                closeCalled = false;
                fireEvent(new JFXDrawerEvent(JFXDrawerEvent.OPENING));
            }
        });
        translateProperty.addListener((o, oldVal, newVal) -> {
            if (openCalled && !closeCalled
                && directionProperty.get().doubleValue() * newVal.doubleValue() <
                   directionProperty.get().doubleValue() * initTranslate.get() / 2) {
                closeCalled = true;
                openCalled = false;
                fireEvent(new JFXDrawerEvent(JFXDrawerEvent.CLOSING));
            }
        });

        overlayPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> close());

        sidePane.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragHandler);
        sidePane.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
        sidePane.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressedHandler);

        // content listener for mouse hold on a side
        this.content.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            if (!e.isConsumed()) {
                double size = 0;
                long valid = 0;
                for (int i = 0; i < callBacks.size(); i++) {
                    if (!callBacks.get(i).call(null)) {
                        valid++;
                    }
                }
                if (directionProperty.get() == DrawerDirection.RIGHT) {
                    size = content.getWidth();
                } else if (directionProperty.get() == DrawerDirection.BOTTOM) {
                    size = content.getHeight();
                }

                double eventPoint = 0;
                if (directionProperty.get() == DrawerDirection.RIGHT
                    || directionProperty.get() == DrawerDirection.LEFT) {
                    eventPoint = e.getX();
                } else {
                    eventPoint = e.getY();
                }

                if (size + directionProperty.get().doubleValue() * eventPoint < activeOffset
                    && (content.getCursor() == Cursor.DEFAULT || content.getCursor() == null)
                    && valid == 0) {
                    holdTimer.play();
                    e.consume();
                }
            }
        });

        this.content.addEventHandler(MouseEvent.MOUSE_RELEASED, (e) -> {
            holdTimer.stop();
            this.content.removeEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDragHandler);
        });

        holdTimer.setOnFinished(e -> {
//            if (!this.getChildren().contains(overlayPane)) {
//                this.getChildren().add(overlayPane);
//            }
//            if (!this.getChildren().contains(sidePane)) {
//                this.getChildren().add(sidePane);
//            }
            translateTo = initTranslate.get()
                          + initOffset * directionProperty.get().doubleValue()
                          + activeOffset * directionProperty.get().doubleValue();
            overlayPane.setMouseTransparent(!isOverLayVisible());
            translateTimer.setOnFinished(null);
//            translateTimer.setOnFinished(()->{
//                this.content.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDragHandler);
//                this.content.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
//                this.content.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<Event>() {
//                    @Override
//                    public void handle(Event event) {
//                        JFXDrawer.this.content.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
//                        JFXDrawer.this.content.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);
//                    }
//                });
//            });
            translateTimer.start();
        });
    }


    ChangeListener<? super Node> widthListener = (o, oldVal, newVal) -> {
        if (newVal != null && newVal instanceof Region) {
            parentSizeProperty.set(((Region) newVal).widthProperty());
        }
    };
    ChangeListener<? super Node> heightListener = (o, oldVal, newVal) -> {
        if (newVal != null && newVal instanceof Region) {
            parentSizeProperty.set(((Region) newVal).heightProperty());
        }
    };
    ChangeListener<? super Scene> sceneWidthListener = (o, oldVal, newVal) -> {
        if (newVal != null && this.getParent() == null) {
            parentSizeProperty.set(newVal.widthProperty());
        }
    };
    ChangeListener<? super Scene> sceneHeightListener = (o, oldVal, newVal) -> {
        if (newVal != null && this.getParent() == null) {
            parentSizeProperty.set(newVal.heightProperty());
        }
    };

    /**
     * this method will change the drawer behavior according to its direction
     *
     * @param dir
     */
    private void updateDirection(DrawerDirection dir) {
        maxSizeProperty.get().set(-1);
        prefSizeProperty.get().set(-1);

        if (dir == DrawerDirection.LEFT) {
            // change the pane position
            StackPane.setAlignment(sidePane, Pos.CENTER_LEFT);
            // reset old translation
            translateProperty.set(0);
            // set the new translation property
            translateProperty = sidePane.translateXProperty();
            // change the size property
            maxSizeProperty.set(sidePane.maxWidthProperty());
            minSizeProperty.set(sidePane.minWidthProperty());
            prefSizeProperty.set(sidePane.prefWidthProperty());
            sizeProperty.set(sidePane.widthProperty());
            this.boundedNodeProperty().removeListener(heightListener);
            this.boundedNodeProperty().addListener(widthListener);
            if (getBoundedNode() == null) {
                this.boundedNodeProperty().bind(this.parentProperty());
            }
            this.sceneProperty().removeListener(sceneHeightListener);
            this.sceneProperty().removeListener(sceneWidthListener);
            this.sceneProperty().addListener(sceneWidthListener);
        } else if (dir == DrawerDirection.RIGHT) {
            StackPane.setAlignment(sidePane, Pos.CENTER_RIGHT);
            translateProperty.set(0);
            translateProperty = sidePane.translateXProperty();
            maxSizeProperty.set(sidePane.maxWidthProperty());
            minSizeProperty.set(sidePane.minWidthProperty());
            prefSizeProperty.set(sidePane.prefWidthProperty());
            sizeProperty.set(sidePane.widthProperty());
            this.boundedNodeProperty().removeListener(heightListener);
            this.boundedNodeProperty().addListener(widthListener);
            if (getBoundedNode() == null) {
                this.boundedNodeProperty().bind(this.parentProperty());
            }
            this.sceneProperty().removeListener(sceneHeightListener);
            this.sceneProperty().removeListener(sceneWidthListener);
            this.sceneProperty().addListener(sceneWidthListener);
        } else if (dir == DrawerDirection.TOP) {
            StackPane.setAlignment(sidePane, Pos.TOP_CENTER);
            translateProperty.set(0);
            translateProperty = sidePane.translateYProperty();
            maxSizeProperty.set(sidePane.maxHeightProperty());
            minSizeProperty.set(sidePane.minHeightProperty());
            prefSizeProperty.set(sidePane.prefHeightProperty());
            sizeProperty.set(sidePane.heightProperty());
            this.boundedNodeProperty().removeListener(widthListener);
            this.boundedNodeProperty().addListener(heightListener);
            if (getBoundedNode() == null) {
                this.boundedNodeProperty().bind(this.parentProperty());
            }
            this.sceneProperty().removeListener(sceneHeightListener);
            this.sceneProperty().removeListener(sceneWidthListener);
            this.sceneProperty().addListener(sceneHeightListener);
        } else if (dir == DrawerDirection.BOTTOM) {
            StackPane.setAlignment(sidePane, Pos.BOTTOM_CENTER);
            translateProperty.set(0);
            translateProperty = sidePane.translateYProperty();
            maxSizeProperty.set(sidePane.maxHeightProperty());
            minSizeProperty.set(sidePane.minHeightProperty());
            prefSizeProperty.set(sidePane.prefHeightProperty());
            sizeProperty.set(sidePane.heightProperty());
            this.boundedNodeProperty().removeListener(widthListener);
            this.boundedNodeProperty().addListener(heightListener);
            if (getBoundedNode() == null) {
                this.boundedNodeProperty().bind(this.parentProperty());
            }
            this.sceneProperty().removeListener(sceneHeightListener);
            this.sceneProperty().removeListener(sceneWidthListener);
            this.sceneProperty().addListener(sceneHeightListener);
        }
        setDefaultDrawerSize(defaultSizeProperty.get());
        updateDrawerAnimation(initTranslate.get());
    }

    private void updateDrawerAnimation(double translation) {
        translateProperty.set(translation);
        translateTo = translation;
    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    private ArrayList<Callback<Void, Boolean>> callBacks = new ArrayList<>();

    /**
     * the callbacks are used to add conditions to allow
     * starting the drawer when holding on the side part of the content
     */
    public void addInitDrawerCallback(Callback<Void, Boolean> callBack) {
        callBacks.add(callBack);
    }

    /**
     * this method is only used in drawers stack component
     *
     * @param callback
     */
    void bringToFront(Callback<Void, Void> callback) {
        EventHandler<? super MouseEvent> eventFilter = Event::consume;
        final boolean bindSize = prefSizeProperty.get().isBound();
        prefSizeProperty.get().unbind();
        maxSizeProperty.get().unbind();
        // disable mouse events
        this.addEventFilter(MouseEvent.ANY, eventFilter);

        Runnable onFinished = () -> {
            callback.call(null);
            translateTo = 0;
            translateTimer.setOnFinished(() -> {
                if (bindSize) {
                    prefSizeProperty.get().bind(parentSizeProperty.get());
                    maxSizeProperty.get().bind(parentSizeProperty.get());
                }
                // enable mouse events
                this.removeEventFilter(MouseEvent.ANY, eventFilter);
            });
            translateTimer.start();
        };

        if (sizeProperty.get().get() > getDefaultDrawerSize()) {
            tempDrawerSize = sizeProperty.get().get();
        } else {
            tempDrawerSize = getDefaultDrawerSize();
        }
        translateTo = initTranslate.get();
        translateTimer.setOnFinished(onFinished);
        translateTimer.start();
    }

    /**
     * this method indicates whether the drawer is shown or not
     *
     * @return true if he drawer is totally visible else false
     */
    public boolean isShown() {
//        return isTransitionStopped(drawerTransition) && translateProperty.get() == 0;
        return translateTo == 0 && !translateTimer.isRunning();
    }

    public boolean isShowing() {
//        return (isRunningTransition(drawerTransition) && this.drawerTransition.getRate() > 0)
//               || (partialTransition instanceof DrawerPartialTransitionDraw
//                   && isRunningTransition(partialTransition));
        return translateTo == 0 && translateTimer.isRunning();
    }

//    private boolean isRunningTransition(Transition transition) {
//        return transition.getStatus() == Status.RUNNING;
//    }

    public boolean isHiding() {
//        return (isRunningTransition(drawerTransition) && this.drawerTransition.getRate() < 0)
//               || (partialTransition instanceof DrawerPartialTransitionHide
//                   && isRunningTransition(partialTransition));
        return translateTo == initTranslate.get() && translateTimer.isRunning();
    }

    public boolean isHidden() {
        return translateTo == initTranslate.get() && !translateTimer.isRunning();
    }

    public void toggle(){
        if(isShown() || isShowing()){
            close();
        }else{
            open();
        }
    }

    /**
     * toggle the drawer on
     */
    public void open() {
        translateTo = 0;
        overlayPane.setMouseTransparent(!isOverLayVisible());
        translateTimer.setOnFinished(() -> fireEvent(new JFXDrawerEvent(JFXDrawerEvent.OPENED)));
        translateTimer.reverseAndContinue();
    }

    /**
     * toggle the drawer off
     */
    public void close() {
        // unbind properties as the drawer size might be bound to stage size
        maxSizeProperty.get().unbind();
        prefSizeProperty.get().unbind();
        if (sizeProperty.get().get() > getDefaultDrawerSize()) {
            tempDrawerSize = prefSizeProperty.get().get();
        } else {
            tempDrawerSize = getDefaultDrawerSize();
        }
        if(translateTo != initTranslate.get()){
            translateTo = initTranslate.get();
            translateTimer.reverseAndContinue();
        }
        translateTimer.setOnFinished(() -> {
            overlayPane.setMouseTransparent(true);
            fireEvent(new JFXDrawerEvent(JFXDrawerEvent.CLOSED));
        });
    }

    /***************************************************************************
     *                                                                         *
     * Setters / Getters                                                       *
     *                                                                         *
     **************************************************************************/

    public ObservableList<Node> getSidePane() {
        return sidePane.getChildren();
    }

    public void setSidePane(Node... sidePane) {
        this.sidePane.getChildren().setAll(sidePane);
    }

    public ObservableList<Node> getContent() {
        if (!getChildren().contains(this.content)) {
            getChildren().add(0, this.content);
        }
        return content.getChildren();
    }

    public void setContent(Node... content) {
        this.content.getChildren().setAll(content);
        if (!getChildren().contains(this.content)) {
            getChildren().add(0, this.content);
        }
    }

    public double getDefaultDrawerSize() {
        return defaultSizeProperty.get();
    }

    public void setDefaultDrawerSize(double drawerWidth) {
        defaultSizeProperty.set(drawerWidth);
        maxSizeProperty.get().set(drawerWidth);
        prefSizeProperty.get().set(drawerWidth);
    }

    public DrawerDirection getDirection() {
        return directionProperty.get();
    }

    public SimpleObjectProperty<DrawerDirection> directionProperty() {
        return directionProperty;
    }

    public void setDirection(DrawerDirection direction) {
        this.directionProperty.set(direction);
    }

    public final BooleanProperty overLayVisibleProperty() {
        return this.overLayVisible;
    }

    public final boolean isOverLayVisible() {
        return this.overLayVisibleProperty().get();
    }

    public final void setOverLayVisible(final boolean overLayVisible) {
        this.overLayVisibleProperty().set(overLayVisible);
    }

    public boolean isResizableOnDrag() {
        return resizable;
    }

    public void setResizableOnDrag(boolean resizable) {
        this.resizable = resizable;
    }

    private final ObjectProperty<Node> boundedNodeProperty() {
        return this.boundedNode;
    }

    private final Node getBoundedNode() {
        return this.boundedNodeProperty().get();
    }

    private final void setBoundedNode(final Node boundedNode) {
        this.boundedNodeProperty().unbind();
        this.boundedNodeProperty().set(boundedNode);
    }


    /***************************************************************************
     *                                                                         *
     * Custom Events                                                           *
     *                                                                         *
     **************************************************************************/

    public EventHandler<JFXDrawerEvent> getOnDrawerClosed() {
        return onDrawerClosedProperty().get();
    }

    public ObjectProperty<EventHandler<JFXDrawerEvent>> onDrawerClosedProperty() {
        return onDrawerClosed;
    }

    public void setOnDrawerClosed(EventHandler<JFXDrawerEvent> onDrawerClosed) {
        onDrawerClosedProperty().set(onDrawerClosed);
    }

    private ObjectProperty<EventHandler<JFXDrawerEvent>> onDrawerClosed = new ObjectPropertyBase<EventHandler<JFXDrawerEvent>>() {
        @Override
        protected void invalidated() {
            setEventHandler(JFXDrawerEvent.CLOSED, get());
        }

        @Override
        public Object getBean() {
            return JFXDrawer.this;
        }

        @Override
        public String getName() {
            return "onClosed";
        }
    };


    public EventHandler<JFXDrawerEvent> getOnDrawerClosing() {
        return onDrawerClosing.get();
    }

    public ObjectProperty<EventHandler<JFXDrawerEvent>> onDrawerClosingProperty() {
        return onDrawerClosing;
    }

    public void setOnDrawerClosing(EventHandler<JFXDrawerEvent> onDrawerClosing) {
        this.onDrawerClosing.set(onDrawerClosing);
    }

    private ObjectProperty<EventHandler<JFXDrawerEvent>> onDrawerClosing = new ObjectPropertyBase<EventHandler<JFXDrawerEvent>>() {
        @Override
        protected void invalidated() {
            setEventHandler(JFXDrawerEvent.CLOSING, get());
        }

        @Override
        public Object getBean() {
            return JFXDrawer.this;
        }

        @Override
        public String getName() {
            return "onClosing";
        }
    };


    public EventHandler<JFXDrawerEvent> getOnDrawerOpened() {
        return onDrawerOpened.get();
    }

    public ObjectProperty<EventHandler<JFXDrawerEvent>> onDrawerOpenedProperty() {
        return onDrawerOpened;
    }

    public void setOnDrawerOpened(EventHandler<JFXDrawerEvent> onDrawerOpened) {
        this.onDrawerOpened.set(onDrawerOpened);
    }

    private ObjectProperty<EventHandler<JFXDrawerEvent>> onDrawerOpened = new ObjectPropertyBase<EventHandler<JFXDrawerEvent>>() {
        @Override
        protected void invalidated() {
            setEventHandler(JFXDrawerEvent.OPENED, get());
        }

        @Override
        public Object getBean() {
            return JFXDrawer.this;
        }

        @Override
        public String getName() {
            return "onOpened";
        }
    };


    public EventHandler<JFXDrawerEvent> getOnDrawerOpening() {
        return onDrawerOpening.get();
    }

    public ObjectProperty<EventHandler<JFXDrawerEvent>> onDrawerOpeningProperty() {
        return onDrawerOpening;
    }

    public void setOnDrawerOpening(EventHandler<JFXDrawerEvent> onDrawerOpening) {
        this.onDrawerOpening.set(onDrawerOpening);
    }

    private ObjectProperty<EventHandler<JFXDrawerEvent>> onDrawerOpening = new ObjectPropertyBase<EventHandler<JFXDrawerEvent>>() {
        @Override
        protected void invalidated() {
            setEventHandler(JFXDrawerEvent.OPENING, get());
        }

        @Override
        public Object getBean() {
            return JFXDrawer.this;
        }

        @Override
        public String getName() {
            return "onOpening";
        }
    };

    /***************************************************************************
     *                                                                         *
     * Action Handlers                                                         *
     *                                                                         *
     **************************************************************************/

    private EventHandler<MouseEvent> mouseDragHandler = (mouseEvent) -> {
        if (!mouseEvent.isConsumed()) {
            mouseEvent.consume();
            double size = 0;
            Bounds sceneBounds = content.localToScene(content.getLayoutBounds());
            if (directionProperty.get() == DrawerDirection.RIGHT) {
                size = sceneBounds.getMinX() + sceneBounds.getWidth();
            } else if (directionProperty.get() == DrawerDirection.BOTTOM) {
                size = sceneBounds.getMinY() + sceneBounds.getHeight();
            }

            if (startSize == -1) {
                startSize = sizeProperty.get().get();
            }

            double eventPoint = 0;
            if (directionProperty.get() == DrawerDirection.RIGHT
                || directionProperty.get() == DrawerDirection.LEFT) {
                eventPoint = mouseEvent.getSceneX();
            } else {
                eventPoint = mouseEvent.getSceneY();
            }

            if (((size + (directionProperty.get().doubleValue() * eventPoint)) >= activeOffset)
                && (partialTransition != null)) {
                partialTransition = null;
            } else if (partialTransition == null) {
                double currentTranslate;
                if (startMouse < 0) {
                    currentTranslate = initTranslate.get()
                                       + directionProperty.get().doubleValue() * initOffset
                                       + directionProperty.get().doubleValue()
                                         * (size + directionProperty.get().doubleValue()
                                                   * eventPoint);
                } else {
                    currentTranslate = directionProperty.get().doubleValue()
                                       * (startTranslate + directionProperty.get().doubleValue()
                                                           * (eventPoint - startMouse));
                }

                if (directionProperty.get().doubleValue() * currentTranslate <= 0) {
                    // the drawer is hidden
                    if (resizable) {
                        maxSizeProperty.get().unbind();
                        prefSizeProperty.get().unbind();
                        if ((startSize - getDefaultDrawerSize())
                            + directionProperty.get().doubleValue() * currentTranslate > 0) {
                            // change the side drawer size if dragging from hidden
                            maxSizeProperty.get()
                                .set(startSize + directionProperty.get().doubleValue() * currentTranslate);
                            prefSizeProperty.get()
                                .set(startSize + directionProperty.get().doubleValue() * currentTranslate);
                        } else {
                            // if the side drawer is not fully shown perform translation to show it , and set its default size
                            maxSizeProperty.get().set(defaultSizeProperty.get());
                            maxSizeProperty.get().set(defaultSizeProperty.get());
                            translateProperty.set(directionProperty.get().doubleValue()
                                                  * ((startSize - getDefaultDrawerSize())
                                                     + directionProperty.get().doubleValue()
                                                       * currentTranslate));
                        }
                    } else {
                        translateProperty.set(currentTranslate);
                    }
                } else {
                    // the drawer is already shown
                    if (resizable) {
                        if (startSize + directionProperty.get()
                                            .doubleValue() * currentTranslate <= parentSizeProperty.get().get()) {
                            // change the side drawer size after being shown
                            maxSizeProperty.get().unbind();
                            prefSizeProperty.get().unbind();
                            maxSizeProperty.get()
                                .set(startSize + directionProperty.get().doubleValue() * currentTranslate);
                            prefSizeProperty.get()
                                .set(startSize + directionProperty.get().doubleValue() * currentTranslate);
                        } else {
                            // bind the drawer size to its parent
                            maxSizeProperty.get().bind(parentSizeProperty.get());
                            prefSizeProperty.get().bind(parentSizeProperty.get());
                        }
                    }
                    translateProperty.set(0);
                }
            }
        }
    };

    private EventHandler<MouseEvent> mousePressedHandler = (mouseEvent) -> {
        translateTimer.setOnFinished(null);
        translateTimer.stop();
        if (directionProperty.get() == DrawerDirection.RIGHT
            || directionProperty.get() == DrawerDirection.LEFT) {
            startMouse = mouseEvent.getSceneX();
        } else {
            startMouse = mouseEvent.getSceneY();
        }
        startTranslate = translateProperty.get();
        startSize = sizeProperty.get().get();

    };

    private EventHandler<MouseEvent> mouseReleasedHandler = (mouseEvent) -> {
        if (directionProperty.get().doubleValue() * translateProperty.get()
            > directionProperty.get().doubleValue() * initTranslate.get() / 2) {
            // show side pane
            if (translateProperty.get() != 0.0) {
                partialOpen();
            }
        } else {
            // hide the sidePane
            if (translateProperty.get() != initTranslate.get()) {
                partialClose();
            }
        }
        if (sizeProperty.get().get() > getDefaultDrawerSize()) {
            tempDrawerSize = prefSizeProperty.get().get();
        } else {
            tempDrawerSize = getDefaultDrawerSize();
        }
        // reset drawer animation properties
        startMouse = -1;
        startTranslate = -1;
        startSize = sizeProperty.get().get();
    };

    private void partialClose() {
        translateTo = initTranslate.get();
        translateTimer.setOnFinished(() -> {
            overlayPane.setMouseTransparent(true);
            fireEvent(new JFXDrawerEvent(JFXDrawerEvent.CLOSED));
        });
        translateTimer.start();
    }

    private void partialOpen() {
        translateTo = 0;
        overlayPane.setMouseTransparent(!isOverLayVisible());
        translateTimer.setOnFinished(() -> fireEvent(new JFXDrawerEvent(JFXDrawerEvent.OPENED)));
        translateTimer.start();
    }

    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    /**
     * Initialize the style class to 'jfx-drawer'.
     * <p>
     * This is the selector class from which CSS can be used to style
     * this control.
     */
    private static final String DEFAULT_STYLE_CLASS = "jfx-drawer";

}

