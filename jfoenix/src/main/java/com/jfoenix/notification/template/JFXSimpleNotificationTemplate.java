package com.jfoenix.notification.template;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.notification.JFXAbstractNotificationTemplate;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.Arrays;

public class JFXSimpleNotificationTemplate extends JFXAbstractNotificationTemplate {

    private HBox header = new HBox();
    private StackPane body = new StackPane();
    private FlowPane actions = new FlowPane();

    public JFXSimpleNotificationTemplate() {
        initialize();
        header.getStyleClass().addAll("jfx-notification-header");
        body.getStyleClass().addAll("jfx-notification-body");
        actions.getStyleClass().addAll("jfx-notification-action");
        getChildren().setAll(header, body, actions);
    }

    @Override
    public void setHeader(Node icon, String heading, boolean closeButton) {
        Label labelTitle = new Label(heading);
        labelTitle.getStyleClass().addAll("label-header");
        labelTitle.setMaxWidth(Double.MAX_VALUE);
        header.getChildren().addAll(icon, labelTitle);
        HBox.setHgrow(labelTitle, Priority.ALWAYS);

        if(closeButton){
            JFXRippler ripplerClose = new JFXRippler();
            ripplerClose.setOnMouseClicked(__ -> { ripplerClose.setDisable(true); hide(); } );
            StackPane boxClose = new StackPane();
            boxClose.getStyleClass().add("box-close");
            StackPane graphic = new StackPane();
            graphic.getStyleClass().setAll("graphic");
            boxClose.getChildren().add(graphic);
            ripplerClose.setControl(boxClose);
            header.getChildren().add(ripplerClose);
        }
    }

    @Override
    public void setHeader(Node icon, String heading) {
        setHeader(icon,heading,true);
    }

    @Override
    public void setBody(String subTitle, String message) {
        Label labelSubTitle = new Label(subTitle);
        Label labelMessage = new Label(message);
        labelSubTitle.getStyleClass().addAll("label-sub-title");
        labelMessage.getStyleClass().addAll("label-message");

        VBox bodyContent = new VBox();
        VBox.setVgrow(labelMessage, Priority.ALWAYS);

        bodyContent.getChildren().addAll(labelSubTitle, labelMessage);
        bodyContent.getStyleClass().add("simple-body");
        body.getChildren().add(bodyContent);
    }

    @Override
    public void setActions(JFXButton... actions) {
        Arrays.asList(actions).forEach(button -> {
            button.getStyleClass().add("btn-action");
            button.setOnMouseClicked( __ -> hide() );
        });
        this.actions.getChildren().setAll(actions);
    }

    @Override
    public String getUserAgentStylesheet() {
        return JFXSimpleNotificationTemplate.class.getResource("/css/controls/jfx-notification.css").toExternalForm();
    }

    private static final String DEFAULT_STYLE_CLASS = "jfx-notification-template";

    private void initialize() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

}
