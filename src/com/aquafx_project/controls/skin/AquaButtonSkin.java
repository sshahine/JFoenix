package com.aquafx_project.controls.skin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.animation.Animation.Status;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.util.Duration;

import com.aquafx_project.controls.skin.effects.FocusBorder;
import com.aquafx_project.controls.skin.effects.Shadow;
import com.aquafx_project.controls.skin.styles.ButtonType;
import com.aquafx_project.controls.skin.styles.MacOSDefaultIconConverter;
import com.aquafx_project.controls.skin.styles.MacOSDefaultIcons;
import com.aquafx_project.util.BindableTransition;
import com.sun.javafx.scene.control.skin.ButtonSkin;

public class AquaButtonSkin extends ButtonSkin implements AquaSkin {

    private BindableTransition defaultButtonTransition;

    private String usualButtonStyle = "-fx-background-color: rgb(255, 255, 255), linear-gradient(#ffffff 20%, #ececec 60%, #ececec 80%, #eeeeee 100%); -fx-background-insets:  0, 1; -fx-background-radius: 4, 4; -fx-border-radius: 4; -fx-border-width: 0.5; -fx-border-color: rgb(129, 129, 129);";

    private String armedButtonStyle = "-fx-background-color: linear-gradient(rgb(190, 214, 237) 0%, rgb(178, 213, 237) 100% ), linear-gradient(rgb(165, 193, 238) 0%, rgb(108, 161, 231) 50%, rgb(74, 138, 217) 50%, rgb(105, 167, 236) 75%, rgb(152, 201, 238) 100%), radial-gradient(focus-angle 180deg, focus-distance 95%, center 1% 50%, radius 50%, #78b0ee, transparent), radial-gradient(focus-angle 0deg, focus-distance 95%, center 100% 50%, radius 50%, #78b0ee, transparent); -fx-background-insets: 0, 0, 1 1 1 2, 1 2 1 1; -fx-background-radius: 4, 4, 4, 4; -fx-border-color: rgb(100, 103, 124);";

    public AquaButtonSkin(Button button) {
        super(button);
        registerChangeListener(button.disabledProperty(), "DISABLED");
        registerChangeListener(button.hoverProperty(), "HOVER");
        if (getSkinnable().isDefaultButton()) {
            setDefaultButtonAnimation();
        }

        if (getSkinnable().isFocused()) {
            setFocusBorder();
        } else {
            setDropShadow();
        }
        /**
         * if the button is a default button, it has to stop blinking when pressed
         */
        getSkinnable().setOnMousePressed(new EventHandler<Event>() {
            @Override public void handle(Event arg0) {
                if (getSkinnable().isFocused()) {
                    setFocusBorder();
                }
                if (getSkinnable().isDefaultButton()) {
                    setDefaultButtonAnimation();
                    getSkinnable().setStyle(armedButtonStyle);
                }
            }
        });

        /**
         * if the button is default, the button has to start blinking again, when mouse is released
         */
        getSkinnable().setOnMouseReleased(new EventHandler<Event>() {
            @Override public void handle(Event arg0) {
                if (getSkinnable().isDefaultButton()) {
                    setDefaultButtonAnimation();
                }
            }
        });

        final ChangeListener<Boolean> windowFocusChangedListener = new ChangeListener<Boolean>() {

            @Override public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (newValue != null) {
                    if (newValue.booleanValue() && getSkinnable().isDefaultButton()) {
                        if (defaultButtonTransition != null && defaultButtonTransition.getStatus() != Status.RUNNING) {
                            setDefaultButtonAnimation();
                        }
                    } else if (defaultButtonTransition != null && defaultButtonTransition.getStatus() == Status.RUNNING) {
                        setDefaultButtonAnimation();
                        // button has to look like a usual button again
                        getSkinnable().setStyle(usualButtonStyle);
                    }
                }
            }
        };

        getSkinnable().sceneProperty().addListener(new ChangeListener<Scene>() {

            @Override public void changed(ObservableValue<? extends Scene> observableValue, Scene oldScene, Scene newScene) {
                if (oldScene != null && oldScene.getWindow() != null) {
                    oldScene.getWindow().focusedProperty().removeListener(windowFocusChangedListener);
                }
                if (newScene != null && newScene.getWindow() != null) {
                    newScene.getWindow().focusedProperty().addListener(windowFocusChangedListener);
                }
            }
        });

        if (getSkinnable().getScene() != null && getSkinnable().getScene().getWindow() != null) {
            getSkinnable().getScene().getWindow().focusedProperty().addListener(windowFocusChangedListener);
        }

        iconProperty().addListener(new ChangeListener<MacOSDefaultIcons>() {

            @Override public void changed(ObservableValue<? extends MacOSDefaultIcons> observable, MacOSDefaultIcons oldValue,
                    MacOSDefaultIcons newValue) {
                if (newValue != null && newValue != oldValue) {
                    if (newValue == MacOSDefaultIcons.SHARE) {
                        StackPane stack = new StackPane();
                        String iconBase = MacOSDefaultIcons.SHARE.getStyleName();
                        stack.getStyleClass().add("aquaicon");
                        Region svgIcon = new Region();
                        svgIcon.getStyleClass().add(iconBase + "-square");
                        stack.getChildren().add(svgIcon);
                        Region svgIcon2 = new Region();
                        svgIcon2.getStyleClass().add(iconBase + "-arrow");
                        stack.getChildren().add(svgIcon2);
                        getSkinnable().setGraphic(stack);
                    } else {
                        Region svgIcon = new Region();
                        svgIcon.getStyleClass().add("aqua-" + newValue.getStyleName());
                        svgIcon.getStyleClass().add("aquaicon");
                        getSkinnable().setGraphic(svgIcon);
                        getSkinnable().getStyleClass().add("button-" + newValue.getStyleName());
                    }
                    getSkinnable().requestLayout();
                }
            }
        });
    }

    private void setFocusBorder() {
        getSkinnable().setEffect(new FocusBorder());
    }

    private void setDropShadow() {
        boolean isPill = false;
        if (getSkinnable().getStyleClass().contains(ButtonType.LEFT_PILL.getStyleName()) || getSkinnable().getStyleClass().contains(
                ButtonType.CENTER_PILL.getStyleName()) || getSkinnable().getStyleClass().contains(
                ButtonType.RIGHT_PILL.getStyleName())) {
            isPill = true;
        }
        getSkinnable().setEffect(new Shadow(isPill));
    }

    @Override protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if (p == "HOVER") {
            if (getSkinnable().isDefaultButton() && getSkinnable().isPressed() && getSkinnable().isHover()) {
                getSkinnable().setStyle(armedButtonStyle);
            } else if (getSkinnable().isDefaultButton() && getSkinnable().isPressed() && !getSkinnable().isHover()) {
                getSkinnable().setStyle(usualButtonStyle);
            }
        }
        if (p == "FOCUSED") {
            if (getSkinnable().isFocused()) {
                setFocusBorder();
            } else if (!getSkinnable().isFocused() || getSkinnable().isDisable()) {
                setDropShadow();
            }
        }
        if (p == "DEFAULT_BUTTON") {
            setDefaultButtonAnimation();
        }
        if (p == "DISABLED") {
            if (getSkinnable().isDefaultButton()) {
                if (getSkinnable().isDisabled() && defaultButtonTransition != null && defaultButtonTransition.getStatus() != Status.RUNNING) {
                    defaultButtonTransition.stop();
                } else {
                    setDefaultButtonAnimation();
                }
            }
        }
    }

    private void setDefaultButtonAnimation() {
        if (!getSkinnable().isDisabled()) {
            if (defaultButtonTransition != null && defaultButtonTransition.getStatus() == Status.RUNNING) {
                defaultButtonTransition.stop();
            } else {
                final Duration duration = Duration.millis(500);
                defaultButtonTransition = new BindableTransition(duration);
                defaultButtonTransition.setCycleCount(Timeline.INDEFINITE);
                defaultButtonTransition.setAutoReverse(true);

                // The gradient
                final Color startColor1 = Color.rgb(183, 206, 238);
                final Color startColor2 = Color.rgb(142, 188, 237);
                final Color startColor3 = Color.rgb(114, 174, 236);
                final Color startColor4 = Color.rgb(178, 218, 242);

                final Color endColor1 = Color.rgb(203, 243, 254);
                final Color endColor2 = Color.rgb(166, 211, 248);
                final Color endColor3 = Color.rgb(137, 198, 248);
                final Color endColor4 = Color.rgb(203, 243, 254);

                defaultButtonTransition.fractionProperty().addListener(new ChangeListener<Number>() {

                    @Override public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                        List<BackgroundFill> list = new ArrayList<>();

                        // the animated fill
                        
                        Stop[] stops = new Stop[] { new Stop(0f, Color.color(
                                (endColor1.getRed() - startColor1.getRed()) * newValue.doubleValue() + startColor1.getRed(),
                                (endColor1.getGreen() - startColor1.getGreen()) * newValue.doubleValue() + startColor1.getGreen(),
                                (endColor1.getBlue() - startColor1.getBlue()) * newValue.doubleValue() + startColor1.getBlue())),
                        new Stop(0.5f, Color.color(
                                (endColor2.getRed() - startColor2.getRed()) * newValue.doubleValue() + startColor2.getRed(),
                                (endColor2.getGreen() - startColor2.getGreen()) * newValue.doubleValue() + startColor2.getGreen(),
                                (endColor2.getBlue() - startColor2.getBlue()) * newValue.doubleValue() + startColor2.getBlue())),
                        new Stop(0.51f, Color.color(
                                (endColor3.getRed() - startColor3.getRed()) * newValue.doubleValue() + startColor3.getRed(),
                                (endColor3.getGreen() - startColor3.getGreen()) * newValue.doubleValue() + startColor3.getGreen(),
                                (endColor3.getBlue() - startColor3.getBlue()) * newValue.doubleValue() + startColor3.getBlue())),
                        new Stop(1f, Color.color(
                                (endColor4.getRed() - startColor4.getRed()) * newValue.doubleValue() + startColor4.getRed(),
                                (endColor4.getGreen() - startColor4.getGreen()) * newValue.doubleValue() + startColor4.getGreen(),
                                (endColor4.getBlue() - startColor4.getBlue()) * newValue.doubleValue() + startColor4.getBlue()))};
                        
                        LinearGradient gradient = new LinearGradient(0.0, 0.0, 0.0, 1.0, true, CycleMethod.NO_CYCLE, stops);
                        BackgroundFill backkgroudFill = new BackgroundFill(gradient, new CornerRadii(4.0), new Insets(0));
                        list.add(backkgroudFill);

                        getSkinnable().setBackground(new Background(list.get(0)));
                    }

                });

                defaultButtonTransition.play();
            }
        }
    }

    /***********************************************************************************
     * Adding the possibility to set an Icon to a Button via CSS-Property *
     **********************************************************************************/

    @Override public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        List<CssMetaData<? extends Styleable, ?>> ret = new ArrayList<>(super.getCssMetaData());
        ret.addAll(getClassCssMetaData());
        return ret;
    }

    private ObjectProperty<MacOSDefaultIcons> icon;

    public final ObjectProperty<MacOSDefaultIcons> iconProperty() {
        if (icon == null) {
            icon = new StyleableObjectProperty<MacOSDefaultIcons>() {

                @Override public CssMetaData<? extends Styleable, MacOSDefaultIcons> getCssMetaData() {
                    return StyleableProperties.ICON;
                }

                @Override public Object getBean() {
                    return AquaButtonSkin.this;
                }

                @Override public String getName() {
                    return "icon";
                }
            };
        }
        return icon;
    }

    public void setIcon(MacOSDefaultIcons icon) {
        iconProperty().setValue(icon);
    }

    public MacOSDefaultIcons getIcon() {
        return icon == null ? null : icon.getValue();
    }

    private static class StyleableProperties {
        private static final CssMetaData<Button, MacOSDefaultIcons> ICON = new CssMetaData<Button, MacOSDefaultIcons>("-fx-aqua-icon", MacOSDefaultIconConverter.getInstance()) {
            @Override public boolean isSettable(Button n) {
                Skin<?> skin = n.getSkin();
                if (skin != null && skin instanceof AquaButtonSkin) {
                    return ((AquaButtonSkin) skin).icon == null || !((AquaButtonSkin) skin).icon.isBound();
                }
                return false;
            }

            @SuppressWarnings("unchecked") @Override public StyleableProperty<MacOSDefaultIcons> getStyleableProperty(Button n) {
                Skin<?> skin = n.getSkin();
                if (skin != null && skin instanceof AquaButtonSkin) {
                    return (StyleableProperty<MacOSDefaultIcons>) ((AquaButtonSkin) skin).iconProperty();
                }
                return null;
            }
        };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
            styleables.add(ICON);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    /**
     * @return The CssMetaData associated with this class, which may include the CssMetaData of its
     *         super classes.
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }
}