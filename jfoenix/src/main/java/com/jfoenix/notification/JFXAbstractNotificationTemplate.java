package com.jfoenix.notification;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.skins.JFXNotificationBar;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public abstract class JFXAbstractNotificationTemplate extends VBox {

    private JFXNotificationBar notificationBar;

    public abstract void setHeader(Node icon, String title);

    public void setNotificationPane(JFXNotificationBar abstractNotificationPane){
        this.notificationBar = abstractNotificationPane;
    }

    public JFXNotificationBar getNotificationBar(){
        return this.notificationBar;
    }

    public void hide(){
        this.notificationBar.hide();
    }

    public abstract void setHeader(Node icon, String heading, boolean closeButton);

    public abstract void setBody(String title, String subTitle);

    public abstract void setActions(JFXButton... actions);

}

