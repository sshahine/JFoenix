package com.aquafx_project.demo;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.aquafx_project.AquaFx;
import com.aquafx_project.controls.skin.styles.ControlSizeVariant;

public class AquaSpecialControlsHBoxDemo extends Application {

    private ObservableList<String> items = FXCollections.observableArrayList("A", "B", "C");
    
    @Override public void start(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root);
        AquaFx.style();
        AquaFx.styleStage(stage, StageStyle.UNIFIED);
        stage.setScene(scene);
        stage.setTitle("AquaFX Controls");
        BorderPane pane = new BorderPane();
        
      HBox hbox = new HBox();
      hbox.setSpacing(5);
      hbox.setPadding(new Insets(15));
      hbox.setAlignment(Pos.CENTER);
        /*
         * Different Control-sizes
         */
        Label labelb5 = new Label("regular:");
        hbox.getChildren().add(labelb5);
        
        Button b5 = new Button("Button");
        AquaFx.resizeControl(b5, ControlSizeVariant.REGULAR);
        hbox.getChildren().add(b5);
        
        ToggleButton tb5 = new ToggleButton("ToggleButton");
        AquaFx.resizeControl(tb5, ControlSizeVariant.REGULAR);
        hbox.getChildren().add(tb5);
        
        CheckBox cb5 = new CheckBox("CheckBox");
        cb5.setIndeterminate(true);
        AquaFx.resizeControl(cb5, ControlSizeVariant.REGULAR);
        hbox.getChildren().add(cb5);

        RadioButton rb5 = new RadioButton("RadioButton");
        AquaFx.resizeControl(rb5, ControlSizeVariant.REGULAR);
        hbox.getChildren().add(rb5);

        TextField tf5 = new TextField("TextField");
        AquaFx.resizeControl(tf5, ControlSizeVariant.REGULAR);
        hbox.getChildren().add(tf5);

        ComboBox<String> ecombo5 = new ComboBox<String>();
        ecombo5.setItems(items);
        ecombo5.setEditable(true);
        ecombo5.setPromptText("select");
        AquaFx.resizeControl(ecombo5, ControlSizeVariant.REGULAR);
        hbox.getChildren().add(ecombo5);
        
        ComboBox<String> combo5 = new ComboBox<String>();
        combo5.setItems(items);
        combo5.setPromptText("select");
        AquaFx.resizeControl(combo5, ControlSizeVariant.REGULAR);
        hbox.getChildren().add(combo5);
        
        ChoiceBox<String> choice5 = new ChoiceBox<String>();
        choice5.setItems(items);
        choice5.getSelectionModel().selectFirst();
        AquaFx.resizeControl(choice5, ControlSizeVariant.REGULAR);
        hbox.getChildren().add(choice5);
        
        Slider slider5 = new Slider(0, 50, 20);
        slider5.setShowTickLabels(true);
        slider5.setShowTickMarks(true);
        slider5.setMajorTickUnit(25);
        slider5.setMinorTickCount(4);
        hbox.getChildren().add(slider5);
        
        
 
        pane.setCenter(hbox);
        scene.setRoot(pane);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}