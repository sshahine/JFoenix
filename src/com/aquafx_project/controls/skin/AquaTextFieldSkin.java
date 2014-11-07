package com.aquafx_project.controls.skin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableProperty;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

import com.aquafx_project.controls.skin.effects.FocusBorder;
import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.scene.control.skin.TextFieldSkin;

public class AquaTextFieldSkin extends TextFieldSkin implements AquaSkin {

    private Region searchIconPath;
    private Region cancelSearchIconPath;

    public AquaTextFieldSkin(final TextField textfield) {
        super(textfield);

        registerChangeListener(textfield.focusedProperty(), "FOCUSED");
        if (getSkinnable().isFocused()) {
            setFocusBorder();
        }

        showSearchIconProperty().addListener(new ChangeListener<Boolean>() {

            @Override public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue != null && newValue != oldValue) {
                    if (newValue.booleanValue()) {
                        if (searchIconPath == null) {
                            searchIconPath = new Region();
                            searchIconPath.getStyleClass().add("search-icon");
                        }
                        getChildren().add(searchIconPath);
                    } else if (oldValue != null && searchIconPath != null) {
                        getChildren().remove(searchIconPath);
                    }
                    getSkinnable().requestLayout();
                }
            }
        });

        showSearchIconProperty().addListener(new ChangeListener<Boolean>() {

            @Override public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                if (newValue != null) {
                    if (newValue.booleanValue()) {
                        if (cancelSearchIconPath == null) {
                            cancelSearchIconPath = new Region();
                            cancelSearchIconPath.getStyleClass().add("cancel-search-icon");

                            cancelSearchIconPath.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override public void handle(MouseEvent event) {
                                    getSkinnable().setText("");
                                }
                            });
                        }
                        getSkinnable().textProperty().addListener(new ChangeListener<String>() {

                            @Override public void changed(ObservableValue<? extends String> observable, String oldValue,
                                    String newValue) {
                                if (newValue == null || newValue.length() == 0) {
                                    cancelSearchIconPath.setVisible(false);
                                } else {
                                    cancelSearchIconPath.setVisible(true);
                                    cancelSearchIconPath.setCursor(Cursor.DEFAULT);
                                }
                            }
                        });
                        if (getSkinnable().getText() == null || getSkinnable().getText().length() == 0) {
                            cancelSearchIconPath.setVisible(false);
                        }
                        getChildren().add(cancelSearchIconPath);
                    } else {
                        if (cancelSearchIconPath != null) {
                            getChildren().remove(cancelSearchIconPath);
                        }
                    }
                    getSkinnable().requestLayout();
                }
            }
        });
    }

    @Override protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);
        if (searchIconPath != null) {
            searchIconPath.toFront();
            double height = getSkinnable().getHeight();
            searchIconPath.setLayoutX(0);
            searchIconPath.setLayoutY(0);
            searchIconPath.resize(height, height);
        }
        if (cancelSearchIconPath != null) {
            cancelSearchIconPath.toFront();
            double height = getSkinnable().getHeight();
            cancelSearchIconPath.setTranslateX(getSkinnable().getWidth() - height * 1.5);
            cancelSearchIconPath.setLayoutY(height * 0.15);
            cancelSearchIconPath.resize(height * 0.55, height * 0.55);
        }
    }

    private void setFocusBorder() {
        getSkinnable().setEffect(new FocusBorder());
    }

    @Override protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);

        if (p == "FOCUSED") {
            if (!(getSkinnable().getParent() instanceof ComboBox)) {
                if (getSkinnable().isFocused()) {
                    setFocusBorder();
                } else {
                    getSkinnable().setEffect(null);
                }
            }
        }
    }

    @Override public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        List<CssMetaData<? extends Styleable, ?>> ret = new ArrayList<>(super.getCssMetaData());
        ret.addAll(getClassCssMetaData());
        return ret;
    }

    /***********************************************************************************
     * * Adding the possibility to make a TextField to a SearchField via CSS-Property * *
     **********************************************************************************/

    private BooleanProperty showSearchIcon;

    public final BooleanProperty showSearchIconProperty() {
        if (showSearchIcon == null) {
            showSearchIcon = new StyleableBooleanProperty() {

                @Override public CssMetaData<? extends Styleable, Boolean> getCssMetaData() {
                    return StyleableProperties.SHOW_SEARCH_ICON;
                }

                @Override public Object getBean() {
                    return AquaTextFieldSkin.this;
                }

                @Override public String getName() {
                    return "showSearchIcon";
                }
            };
        }
        return showSearchIcon;
    }

    public void setShowSearchIcon(Boolean showSearchIcon) {
        showSearchIconProperty().setValue(showSearchIcon);
    }

    public boolean isShowSearchIcon() {
        return showSearchIcon == null ? false : showSearchIcon.getValue();
    }

    private static class StyleableProperties {
        private static final CssMetaData<TextField, Boolean> SHOW_SEARCH_ICON = new CssMetaData<TextField, Boolean>("-fx-show-search-icon", BooleanConverter.getInstance(), false) {
            @Override public boolean isSettable(TextField n) {
                Skin<?> skin = n.getSkin();
                if (skin != null && skin instanceof AquaTextFieldSkin) {
                    return ((AquaTextFieldSkin) skin).showSearchIcon == null || !((AquaTextFieldSkin) skin).showSearchIcon.isBound();
                }
                return false;
            }

            @SuppressWarnings("unchecked") @Override public StyleableProperty<Boolean> getStyleableProperty(TextField n) {
                Skin<?> skin = n.getSkin();
                if (skin != null && skin instanceof AquaTextFieldSkin) {
                    return (StyleableProperty<Boolean>) ((AquaTextFieldSkin) skin).showSearchIconProperty();
                }
                return null;
            }
        };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
            styleables.add(SHOW_SEARCH_ICON);
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
