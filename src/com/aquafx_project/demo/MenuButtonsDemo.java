package com.aquafx_project.demo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.aquafx_project.AquaFx;
import com.aquafx_project.controls.skin.styles.ButtonType;
import com.aquafx_project.controls.skin.styles.MacOSDefaultIcons;

public class MenuButtonsDemo extends Application {

    @Override public void start(Stage stage) throws Exception {
        AquaFx.styleStage(stage, StageStyle.UNIFIED);
        BorderPane pane = new BorderPane();

        ToolBar toolBar = new ToolBar();
        Button tbBack = new Button();
        AquaFx.createButtonStyler().setIcon(MacOSDefaultIcons.LEFT).setType(ButtonType.LEFT_PILL).style(tbBack);
        Button tbForward = new Button();
        tbForward.setDisable(true);
        AquaFx.createButtonStyler().setIcon(MacOSDefaultIcons.RIGHT).setType(ButtonType.RIGHT_PILL).style(tbForward);
        HBox separator = new HBox();
        separator.setPrefSize(15, 1);
        Button share = new Button();
        share.setDisable(true);
        AquaFx.createButtonStyler().setIcon(MacOSDefaultIcons.SHARE).style(share);
        HBox separator2 = new HBox();
        separator2.setPrefSize(15, 1);

        ComboBox<String> combo = new ComboBox<String>(FXCollections.observableArrayList("Combo A", "Combo B", "Combo C"));
        ComboBox<String> combo2 = new ComboBox<String>(FXCollections.observableArrayList("Combo A", "Combo B", "Combo C"));
        combo2.setEditable(true);
        ChoiceBox<String> choice = new ChoiceBox<String>(FXCollections.observableArrayList("Choice A", "Choice B", "Choice C"));
        MenuButton m = new MenuButton("Eats");
        m.getItems().addAll(new MenuItem("Burger"), new MenuItem("Hot Dog"));
        m.setPopupSide(Side.RIGHT);

        SplitMenuButton m2 = new SplitMenuButton();
        m2.setText("Shutdown");
        m2.getItems().addAll(new MenuItem("Logout"), new MenuItem("Sleep"));
        m2.setPopupSide(Side.RIGHT);

        ColorPicker colorTB = new ColorPicker(Color.rgb(194, 222, 254));

        Separator seperateIt = new Separator();
        ToggleGroup toolbarGroup = new ToggleGroup();
        ToggleButton sampleButton4 = new ToggleButton("TG1");
        sampleButton4.setToggleGroup(toolbarGroup);
        sampleButton4.setSelected(true);
        AquaFx.createToggleButtonStyler().setType(ButtonType.LEFT_PILL).style(sampleButton4);
        ToggleButton sampleButton5 = new ToggleButton("TG2");
        sampleButton5.setToggleGroup(toolbarGroup);
        sampleButton5.setSelected(true);
        AquaFx.createToggleButtonStyler().setType(ButtonType.CENTER_PILL).style(sampleButton5);
        ToggleButton sampleButton6 = new ToggleButton("TG3");
        sampleButton6.setToggleGroup(toolbarGroup);
        sampleButton6.setSelected(true);
        AquaFx.createToggleButtonStyler().setType(ButtonType.RIGHT_PILL).style(sampleButton6);
        Separator seperateIt2 = new Separator();

        Button sampleButton = new Button("Button");
        ToggleButton sampleButton2 = new ToggleButton("Toggle");
        toolBar.getItems().addAll(tbBack, tbForward, separator, share, separator2, combo, combo2, choice, m, m2, seperateIt2,
                colorTB, sampleButton, sampleButton2, seperateIt, sampleButton4, sampleButton5, sampleButton6);

        pane.setTop(toolBar);

        pane.setCenter(new Label("MenuButtons and other related controls"));

        Scene myScene = new Scene(pane, 950, 200);

        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        menuFile.getItems().addAll(new MenuItem("New"), new MenuItem("Open File..."));
        Menu menuEdit = new Menu("Edit");
        menuEdit.getItems().addAll(new MenuItem("Undo"), new MenuItem("Redo"));
        Menu menuView = new Menu("View");
        menuView.getItems().addAll(new MenuItem("Zoom In"), new MenuItem("Zoom Out"));
        menuBar.getMenus().addAll(menuFile, menuEdit, menuView);
        pane.getChildren().add(menuBar);

        AquaFx.style();
        stage.setTitle("AquaFX");
        stage.setScene(myScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
