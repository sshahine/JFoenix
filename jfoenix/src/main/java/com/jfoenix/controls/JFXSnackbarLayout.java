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

package com.jfoenix.controls;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * JFXSnackbarLayout default layout for snackbar content
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2018-11-16
 */
public class JFXSnackbarLayout extends BorderPane {

    private Label toast;
    private JFXButton action;
    private StackPane actionContainer;

    public JFXSnackbarLayout(String message) {
        this(message, null, null);
    }

    public JFXSnackbarLayout(String message, String actionText, EventHandler<ActionEvent> actionHandler) {
        initialize();

        toast = new Label();
        toast.setMinWidth(Control.USE_PREF_SIZE);
        toast.getStyleClass().add("jfx-snackbar-toast");
        toast.setWrapText(true);
        toast.setText(message);
        StackPane toastContainer = new StackPane(toast);
        toastContainer.setPadding(new Insets(20));
        actionContainer = new StackPane();
        actionContainer.setPadding(new Insets(0, 10, 0, 0));

        toast.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> {
            if (getPrefWidth() == -1) {
                return getPrefWidth();
            }
            double actionWidth = actionContainer.isVisible() ? actionContainer.getWidth() : 0.0;
            return prefWidthProperty().get() - actionWidth;
        }, prefWidthProperty(), actionContainer.widthProperty(), actionContainer.visibleProperty()));

        setLeft(toastContainer);
        setRight(actionContainer);

        if (actionText != null) {
            action = new JFXButton();
            action.setText(actionText);
            action.setOnAction(actionHandler);
            action.setMinWidth(Control.USE_PREF_SIZE);
            action.setButtonType(JFXButton.ButtonType.FLAT);
            action.getStyleClass().add("jfx-snackbar-action");
            // actions will be added upon showing the snackbar if needed
            actionContainer.getChildren().add(action);

            if (actionText != null && !actionText.isEmpty()) {
                action.setVisible(true);
                actionContainer.setVisible(true);
                actionContainer.setManaged(true);
                // to force updating the layout bounds
                action.setText("");
                action.setText(actionText);
                action.setOnAction(actionHandler);
            } else {
                actionContainer.setVisible(false);
                actionContainer.setManaged(false);
                action.setVisible(false);
            }
        }
    }

    private static final String DEFAULT_STYLE_CLASS = "jfx-snackbar-layout";

    public String getToast() {
        return toast.getText();
    }

    public void setToast(String toast) {
        this.toast.setText(toast);
    }


    private void initialize() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    }
}

