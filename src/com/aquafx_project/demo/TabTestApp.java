package com.aquafx_project.demo;

import com.aquafx_project.AquaFx;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class TabTestApp extends Application { 

    public static void main(String[] args) { 
        launch(args); 
    } 

    @Override 
    public void start(Stage primaryStage) throws Exception { 
        BorderPane pane = new BorderPane(); 
         
        TabPane tabPane = new TabPane(); 
        Tab tab1 = new Tab("Tab 1 (disabled)"); 
        tab1.setContent(new Label("Lorem Ipsum tab1"));
        tab1.setDisable(true); 
        tabPane.getTabs().add(tab1); 
        final Tab tab2 = new Tab("Tab 2"); 
        tab2.setContent(new Label("Lorem Ipsum"));
        tabPane.getTabs().add(tab2); 
        pane.setTop(tabPane); 
         
        Button btn = new Button("disable/enable Tab 2"); 
        btn.setOnAction(new EventHandler<ActionEvent>() { 
            @Override 
            public void handle(ActionEvent event) { 
                tab2.setDisable(!tab2.isDisable()); 
            } 
        }); 
        pane.setBottom(btn); 
         
        Scene scene = new Scene(pane, 500, 200); 

        primaryStage.setScene(scene); 
        AquaFx.style();
        primaryStage.show(); 

    } 
}