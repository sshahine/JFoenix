package com.aquafx_project.demo;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.aquafx_project.AquaFx;
import com.aquafx_project.controls.skin.styles.ButtonType;
import com.aquafx_project.controls.skin.styles.ControlSizeVariant;
import com.aquafx_project.controls.skin.styles.MacOSDefaultIcons;

public class AquaSpecialControlsDemo extends Application {

    private ObservableList<String> items = FXCollections.observableArrayList("A", "B", "C");

    @Override public void start(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root);
        AquaFx.style();
        AquaFx.styleStage(stage, StageStyle.UNIFIED);
        stage.setScene(scene);
        stage.setTitle("AquaFX Controls");
        BorderPane pane = new BorderPane();
        pane.setStyle("-fx-background-color: white;");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHalignment(HPos.RIGHT);
        grid.getColumnConstraints().add(column1);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHalignment(HPos.LEFT);
        grid.getColumnConstraints().add(column2);

        /*
         * Toolbar section
         */
        ToolBar toolBar = new ToolBar();
        Button tbBack = new Button();
        AquaFx.createButtonStyler().setIcon(MacOSDefaultIcons.LEFT).setType(ButtonType.LEFT_PILL).style(tbBack);
        Button tbForward = new Button();
        tbForward.setDisable(true);
        AquaFx.createButtonStyler().setIcon(MacOSDefaultIcons.RIGHT).setType(ButtonType.RIGHT_PILL).style(tbForward);
        
        HBox separator = new HBox();
        separator.setPrefSize(15, 1);
        Button btnAll = new Button("Alle einblenden");
        HBox separator2 = new HBox();
        separator2.setPrefSize(15, 1);
        Button share = new Button();
        share.setDisable(true);
        AquaFx.createButtonStyler().setIcon(MacOSDefaultIcons.SHARE).style(share);
        
        
        toolBar.getItems().addAll(tbBack, tbForward, separator, btnAll, separator2, share);
        pane.setTop(toolBar);

        Label info = new Label("Those Controls are styled by AquaFX:");
        grid.add(info, 0, 0, 5, 1);
        GridPane.setHalignment(info, HPos.LEFT);

        /*
         * Different Control-sizes
         */
        Label labelb5 = new Label("regular:");
        grid.add(labelb5, 0, 2);
        Button b5 = new Button("AquaFX");
        b5.setStyle("-fx-font-size: 30.0;-fx-background-radius: 6.0, 6.0;-fx-border-radius: 6.0;");
        AquaFx.createButtonStyler().setSizeVariant(ControlSizeVariant.REGULAR).style(b5);
        grid.add(b5, 1, 2);
        ToggleButton tb5 = new ToggleButton("ToggleButton");
        AquaFx.createToggleButtonStyler().setSizeVariant(ControlSizeVariant.REGULAR).style(tb5);
        grid.add(tb5, 2, 2);
        CheckBox cb5 = new CheckBox("CheckBox");
        cb5.setIndeterminate(true);
        AquaFx.createCheckBoxStyler().setSizeVariant(ControlSizeVariant.REGULAR).style(cb5);
        grid.add(cb5, 3, 2);
        RadioButton rb5 = new RadioButton("RadioButton");
        AquaFx.createRadioButtonStyler().setSizeVariant(ControlSizeVariant.REGULAR).style(rb5);
        grid.add(rb5, 4, 2);
        TextField tf5 = new TextField("TextField");
        AquaFx.createTextFieldStyler().setSizeVariant(ControlSizeVariant.REGULAR).style(tf5);
        grid.add(tf5, 5, 2);
        ComboBox<String> ecombo5 = new ComboBox<String>();
        ecombo5.setItems(items);
        ecombo5.setEditable(true);
        ecombo5.setPromptText("select");
        AquaFx.createComboBoxStyler().setSizeVariant(ControlSizeVariant.REGULAR).style(ecombo5);
        grid.add(ecombo5, 6, 2);
        ComboBox<String> combo5 = new ComboBox<String>();
        combo5.setItems(items);
        combo5.setPromptText("select");
        AquaFx.createComboBoxStyler().setSizeVariant(ControlSizeVariant.REGULAR).style(combo5);
        grid.add(combo5, 7, 2);
        ChoiceBox<String> choice5 = new ChoiceBox<String>();
        choice5.setItems(items);
        choice5.getSelectionModel().selectFirst();
        AquaFx.createChoiceBoxStyler().setSizeVariant(ControlSizeVariant.REGULAR).style(choice5);
        grid.add(choice5, 8, 2);

        Label labelb6 = new Label("small:");
        AquaFx.createLabelStyler().setSizeVariant(ControlSizeVariant.SMALL).style(labelb6);
        grid.add(labelb6, 0, 3);
        Button b6 = new Button("Button");
        AquaFx.createButtonStyler().setSizeVariant(ControlSizeVariant.SMALL).style(b6);
        grid.add(b6, 1, 3);
        ToggleButton tb6 = new ToggleButton("ToggleButton");
        AquaFx.createToggleButtonStyler().setSizeVariant(ControlSizeVariant.SMALL).style(tb6);
        grid.add(tb6, 2, 3);
        CheckBox cb6 = new CheckBox("CheckBox");
        cb6.setSelected(true);
        // cb6.setDisable(true);
        AquaFx.createCheckBoxStyler().setSizeVariant(ControlSizeVariant.SMALL).style(cb6);
        grid.add(cb6, 3, 3);
        RadioButton rb6 = new RadioButton("RadioButton");
        AquaFx.createRadioButtonStyler().setSizeVariant(ControlSizeVariant.SMALL).style(rb6);
        grid.add(rb6, 4, 3);
        TextField tf6 = new TextField("TextField");
        AquaFx.createTextFieldStyler().setSizeVariant(ControlSizeVariant.SMALL).style(tf6);
        grid.add(tf6, 5, 3);
        ComboBox<String> ecombo6 = new ComboBox<String>();
        ecombo6.setItems(items);
        ecombo6.setEditable(true);
        ecombo6.setPromptText("select");
        AquaFx.createComboBoxStyler().setSizeVariant(ControlSizeVariant.SMALL).style(ecombo6);
        grid.add(ecombo6, 6, 3);
        ComboBox<String> combo6 = new ComboBox<String>();
        combo6.setItems(items);
        combo6.setPromptText("select");
        AquaFx.createComboBoxStyler().setSizeVariant(ControlSizeVariant.SMALL).style(combo6);
        grid.add(combo6, 7, 3);
        ChoiceBox<String> choice6 = new ChoiceBox<String>();
        choice6.setItems(items);
        choice6.getSelectionModel().selectFirst();
        AquaFx.createChoiceBoxStyler().setSizeVariant(ControlSizeVariant.SMALL).style(choice6);
        grid.add(choice6, 8, 3);

        Label labelb7 = new Label("mini:");
        AquaFx.createLabelStyler().setSizeVariant(ControlSizeVariant.MINI).style(labelb7);
        grid.add(labelb7, 0, 4);
        Button b7 = new Button("Button");
        AquaFx.createButtonStyler().setSizeVariant(ControlSizeVariant.MINI).style(b7);
        grid.add(b7, 1, 4);
        ToggleButton tb7 = new ToggleButton("ToggleButton");
        AquaFx.createToggleButtonStyler().setSizeVariant(ControlSizeVariant.MINI).style(tb7);
        grid.add(tb7, 2, 4);
        CheckBox cb7 = new CheckBox("CheckBox");
        cb7.setIndeterminate(true);
        // cb7.setDisable(true);
        AquaFx.createCheckBoxStyler().setSizeVariant(ControlSizeVariant.MINI).style(cb7);
        grid.add(cb7, 3, 4);
        RadioButton rb7 = new RadioButton("RadioButton");
        AquaFx.createRadioButtonStyler().setSizeVariant(ControlSizeVariant.MINI).style(rb7);
        grid.add(rb7, 4, 4);
        TextField tf7 = new TextField("TextField");
        AquaFx.createTextFieldStyler().setSizeVariant(ControlSizeVariant.MINI).style(tf7);
        grid.add(tf7, 5, 4);
        ComboBox<String> ecombo7 = new ComboBox<String>();
        ecombo7.setItems(items);
        ecombo7.setEditable(true);
        ecombo7.setPromptText("select");
        AquaFx.createComboBoxStyler().setSizeVariant(ControlSizeVariant.MINI).style(ecombo7);
        grid.add(ecombo7, 6, 4);
        ComboBox<String> combo7 = new ComboBox<String>();
        combo7.setItems(items);
        combo7.setPromptText("select");
        AquaFx.createComboBoxStyler().setSizeVariant(ControlSizeVariant.MINI).style(combo7);
        grid.add(combo7, 7, 4);
        ChoiceBox<String> choice7 = new ChoiceBox<String>();
        choice7.setItems(items);
        choice7.getSelectionModel().selectFirst();
        AquaFx.createChoiceBoxStyler().setSizeVariant(ControlSizeVariant.MINI).style(choice7);
        grid.add(choice7, 8, 4);

        Label labelb8 = new Label("regular:");
        grid.add(labelb8, 0, 6);
        Slider slider5 = new Slider(0, 50, 20);
        slider5.setShowTickLabels(true);
        slider5.setShowTickMarks(true);
        slider5.setMajorTickUnit(25);
        slider5.setMinorTickCount(4);
        grid.add(slider5, 1, 6, 2, 1);
        Slider slider11 = new Slider(0, 50, 20);
        slider11.setShowTickLabels(false);
        slider11.setShowTickMarks(false);
        grid.add(slider11, 3, 6, 2, 1);
        ProgressIndicator indicator1 = new ProgressIndicator();
        grid.add(indicator1, 5, 6);
        ProgressIndicator indicator2 = new ProgressIndicator(0.5);
        grid.add(indicator2, 6, 6);
        // TextArea area1 = new TextArea();
        // area1.setPromptText("TextArea with promptText");
        // area1.setPrefRowCount(2);
        // area1.setPrefColumnCount(15);
        // grid.add(area1, 7, 6, 2, 1);
        ProgressBar bar = new ProgressBar(0.6);
        grid.add(bar, 7, 6, 2, 1);

        Label labelb9 = new Label("small:");
        AquaFx.createLabelStyler().setSizeVariant(ControlSizeVariant.SMALL).style(labelb9);
        grid.add(labelb9, 0, 7);
        Slider slider6 = new Slider(0, 50, 20);
        slider6.setShowTickLabels(true);
        slider6.setShowTickMarks(true);
        slider6.setMajorTickUnit(25);
        slider6.setMinorTickCount(4);
        AquaFx.createSliderStyler().setSizeVariant(ControlSizeVariant.SMALL).style(slider6);
        grid.add(slider6, 1, 7, 2, 1);
        Slider slider12 = new Slider(0, 50, 20);
        slider12.setShowTickLabels(false);
        slider12.setShowTickMarks(false);
        AquaFx.createSliderStyler().setSizeVariant(ControlSizeVariant.SMALL).style(slider12);
        grid.add(slider12, 3, 7, 2, 1);
        ProgressIndicator indicator3 = new ProgressIndicator();
        AquaFx.createProgressIndicatorStyler().setSizeVariant(ControlSizeVariant.SMALL).style(indicator3);
        grid.add(indicator3, 5, 7);
        ProgressIndicator indicator4 = new ProgressIndicator(0.5);
        AquaFx.createProgressIndicatorStyler().setSizeVariant(ControlSizeVariant.SMALL).style(indicator4);
        grid.add(indicator4, 6, 7);
        // TextArea area2 = new TextArea();
        // area2.setPromptText("TextArea with promptText");
        // area2.setPrefRowCount(2);
        // area2.setPrefColumnCount(15);
        // AquaFx.resizeControl(area2, ControlSizeVariant.SMALL);
        // grid.add(area2, 7, 7, 2, 1);
        ProgressBar bar2 = new ProgressBar(0.6);
        AquaFx.createProgressBarStyler().setSizeVariant(ControlSizeVariant.SMALL).style(bar2);
        grid.add(bar2, 7, 7, 2, 1);

        Label labelb10 = new Label("mini:");
        AquaFx.createLabelStyler().setSizeVariant(ControlSizeVariant.MINI).style(labelb10);
        grid.add(labelb10, 0, 8);
        Slider slider7 = new Slider(0, 50, 20);
        slider7.setShowTickLabels(true);
        slider7.setShowTickMarks(true);
        slider7.setMajorTickUnit(25);
        slider7.setMinorTickCount(4);
        AquaFx.createSliderStyler().setSizeVariant(ControlSizeVariant.MINI).style(slider7);
        grid.add(slider7, 1, 8, 2, 1);
        Slider slider13 = new Slider(0, 50, 20);
        slider13.setShowTickLabels(false);
        slider13.setShowTickMarks(false);
        AquaFx.createSliderStyler().setSizeVariant(ControlSizeVariant.MINI).style(slider13);
        grid.add(slider13, 3, 8, 2, 1);
        ProgressIndicator indicator5 = new ProgressIndicator();
        AquaFx.createProgressIndicatorStyler().setSizeVariant(ControlSizeVariant.MINI).style(indicator5);
        grid.add(indicator5, 5, 8);
        ProgressIndicator indicator6 = new ProgressIndicator(0.5);
        AquaFx.createProgressIndicatorStyler().setSizeVariant(ControlSizeVariant.MINI).style(indicator6);
        grid.add(indicator6, 6, 8);
        // TextArea area3 = new TextArea();
        // area3.setPromptText("TextArea with promptText");
        // area3.setPrefRowCount(2);
        // area3.setPrefColumnCount(15);
        // AquaFx.resizeControl(area3, ControlSizeVariant.MINI);
        // grid.add(area3, 7, 8, 2, 1);
        ProgressBar bar3 = new ProgressBar(0.6);
        AquaFx.createProgressBarStyler().setSizeVariant(ControlSizeVariant.MINI).style(bar3);
        grid.add(bar3, 7, 8, 2, 1);

        /*
         * a GroupBox
         */
        VBox box = new VBox();
        box.setSpacing(15);
        box.setPadding(new Insets(15));
        AquaFx.setGroupBox(box);
        Label groupInfo = new Label("This is a GroupBox,\nwhich is applicable for all Panes\nvia AquaFX");
        box.getChildren().add(groupInfo);
        grid.add(box, 0, 10, 6, 3);

        /*
         * Special Controls
         */
        Label labelb2 = new Label("Help Button:");
        GridPane.setHalignment(labelb2, HPos.RIGHT);
        grid.add(labelb2, 6, 10);
        Button b2 = new Button("?");
        AquaFx.createButtonStyler().setType(ButtonType.HELP).style(b2);
        grid.add(b2, 7, 10);

        Label labelb3 = new Label("Rounded Button:");
        GridPane.setHalignment(labelb3, HPos.RIGHT);
        grid.add(labelb3, 6, 11);
        Button b3 = new Button("round rect");
        AquaFx.createButtonStyler().setType(ButtonType.ROUND_RECT).style(b3);
        grid.add(b3, 7, 11);

        Label labelb4 = new Label("Share Button:");
        GridPane.setHalignment(labelb4, HPos.RIGHT);
        grid.add(labelb4, 6, 12);
        Button b4 = new Button();
        AquaFx.createButtonStyler().setIcon(MacOSDefaultIcons.SHARE).style(b4);
        grid.add(b4, 7, 12);

        pane.setCenter(grid);
        scene.setRoot(pane);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}