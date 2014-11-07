package com.aquafx_project.demo;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.aquafx_project.AquaFx;
import com.aquafx_project.controls.skin.styles.ButtonType;

public class TabPaneDemo extends Application {

    @Override public void start(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root, 300, 210);
        AquaFx.style();
        stage.setScene(scene);
        stage.setTitle("TabPaneDemo");

        TabPane tabPane = new TabPane();
        tabPane.setSide(Side.TOP);
        tabPane.setPadding(new Insets(15));

        // Create Tabs
        Tab tabD = new Tab();
        tabD.setText("tab 1");

        VBox box = new VBox();
        box.setSpacing(15);
        box.setPadding(new Insets(15));
        Button b1 = new Button("regular");
        box.getChildren().add(b1);
        Button b3 = new Button("round rect");
        AquaFx.createButtonStyler().setType(ButtonType.ROUND_RECT).style(b3);
        box.getChildren().add(b3);

        tabD.setContent(box);

        tabPane.getTabs().add(tabD);

        Tab tabE = new Tab();
        tabE.setText("tab 2");
        tabPane.getTabs().add(tabE);

        Tab tabF = new Tab();
        tabF.setText("tab 3");
        tabPane.getTabs().add(tabF);

        scene.setRoot(tabPane);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}