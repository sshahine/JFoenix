package com.aquafx_project.demo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.aquafx_project.AquaFx;
import com.aquafx_project.controls.skin.styles.ButtonType;
import com.aquafx_project.controls.skin.styles.MacOSDefaultIcons;
import com.aquafx_project.controls.skin.styles.TextFieldType;

public class AquaDateAndTimeDemo extends Application {
    @Override public void start(Stage stage) throws Exception {
        AquaFx.styleStage(stage, StageStyle.UNIFIED);
        stage.setResizable(false);

        BorderPane pane = new BorderPane();

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
        separator2.setPrefSize(279, 1);
        TextField search = new TextField();
        AquaFx.createTextFieldStyler().setType(TextFieldType.SEARCH).style(search);
        toolBar.getItems().addAll(tbBack, tbForward, separator, btnAll, separator2, search);

        pane.setTop(toolBar);

        /*
         * TabPane for Content
         */
        TabPane contentTabPane = new TabPane();
        contentTabPane.setPadding(new Insets(25, 15, 15, 15));
        Tab tabDate = new Tab();
        tabDate.setText("Datum & Uhrzeit");
        contentTabPane.getTabs().add(tabDate);
        Tab tabTimezone = new Tab();
        tabTimezone.setText("Zeitzone");
        contentTabPane.getTabs().add(tabTimezone);
        Tab tabClock = new Tab();
        tabClock.setText("Uhr");

        /*
         * The ClockSettings-Content
         */
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(25, 30, 0, 60));

        CheckBox box1 = new CheckBox("Datum und Uhrzeit in der Men\u00FCleiste anzeigen");
        box1.setSelected(true);
        grid.add(box1, 0, 0, 3, 1);

        Label labelTime = new Label("Zeitoptionen:");
        ToggleGroup group = new ToggleGroup();
        RadioButton radioButtonDigital = new RadioButton("Digital");
        radioButtonDigital.setToggleGroup(group);
        radioButtonDigital.setSelected(true);
        RadioButton radioButtonAnalog = new RadioButton("Analog");
        radioButtonAnalog.setToggleGroup(group);
        grid.addRow(2, labelTime, radioButtonDigital, radioButtonAnalog);
        GridPane.setHalignment(labelTime, HPos.RIGHT);
        GridPane.setMargin(radioButtonAnalog, new Insets(0, 0, 0, 20));

        CheckBox box2 = new CheckBox("Uhrzeit mit Sekunden anzeigen");
        grid.add(box2, 1, 3, 2, 1);
        CheckBox box3 = new CheckBox("Blinkende Trennzeichen");
        grid.add(box3, 1, 4, 2, 1);
        CheckBox box4 = new CheckBox("24 Stunden verwenden");
        box4.setSelected(true);
        grid.add(box4, 1, 5, 2, 1);
        CheckBox box5 = new CheckBox("Uhrzeit mit Suffix anzeigen");
        box5.setDisable(true);
        grid.add(box5, 1, 6, 2, 1);

        Label labelDate = new Label("Datumsoptionen:");
        grid.add(labelDate, 0, 8);
        GridPane.setHalignment(labelDate, HPos.RIGHT);
        GridPane.setMargin(labelDate, new Insets(0, 0, 0, 40));
        CheckBox box6 = new CheckBox("Wochentag anzeigen");
        box6.setSelected(true);
        grid.add(box6, 1, 8, 2, 1);
        CheckBox box7 = new CheckBox("Datum anzeigen");
        grid.add(box7, 1, 9, 2, 1);

        HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.setAlignment(Pos.CENTER_LEFT);
        CheckBox box8 = new CheckBox("Zeit vorlesen:");
        ChoiceBox<String> choices = new ChoiceBox<String>(FXCollections.observableArrayList("Zur vollen Stunde",
                "Zur halben Stunde", "Zur Viertelstunde"));
        choices.setDisable(true);
        choices.getSelectionModel().selectFirst();
        Button abjustVoice = new Button();
        abjustVoice.setText("Stimme anpassen ...");
        abjustVoice.setDisable(true);
        hbox.getChildren().addAll(box8, choices, abjustVoice);
        grid.add(hbox, 0, 14, 4, 1);

        tabClock.setContent(grid);
        /*
         * Content finished.. add it to Tab
         */
        contentTabPane.getTabs().add(tabClock);
        contentTabPane.getSelectionModel().select(tabClock);
        pane.setCenter(contentTabPane);

        /*
         * Footer
         */
        HBox footerBox = new HBox();
        footerBox.setSpacing(5);
        footerBox.setAlignment(Pos.BOTTOM_LEFT);
        Image image = new Image(AquaFx.class.getResource("demo/images/lock_open.png").toExternalForm());
        ImageView lockView = new ImageView(image);
        lockView.setPreserveRatio(true);
        lockView.setFitHeight(36);
        footerBox.getChildren().add(lockView);
        Label info = new Label("Zum Sch\u00FCtzen auf das Schloss klicken.");
        info.setStyle("-fx-font-size: 12");
        info.setPadding(new Insets(0, 0, 3, 0));
        footerBox.getChildren().add(info);

        AnchorPane anchorpane = new AnchorPane();
        Button helpBtn = new Button("?");
        AquaFx.createButtonStyler().setType(ButtonType.HELP).style(helpBtn);
        anchorpane.getChildren().addAll(footerBox, helpBtn);
        AnchorPane.setBottomAnchor(footerBox, 15.0);
        AnchorPane.setLeftAnchor(footerBox, 17.0);
        AnchorPane.setBottomAnchor(helpBtn, 15.0);
        AnchorPane.setRightAnchor(helpBtn, 15.0);
        pane.setBottom(anchorpane);

        /*
         * MenuBar
         */
        MenuBar menuBar = new MenuBar();
        Menu menuSystemPreferences = new Menu("Systemeinstellungen");
        Menu menuServices = new Menu("Dienste");
        MenuItem menuNoService = new MenuItem("Keine Dienste Verf\u00FCgbar");
        menuNoService.setDisable(true);

        MenuItem menuServPref = new MenuItem("Dienste Enistellungen ...");
        menuServices.getItems().addAll(menuNoService, menuServPref);
        MenuItem menuHide = new MenuItem("Sytemeinstellungen ausblenden");
        menuHide.setAccelerator(KeyCombination.keyCombination("shortcut+H"));
        MenuItem menuHideOthers = new MenuItem("Andere ausblenden");
        menuHideOthers.setAccelerator(KeyCombination.keyCombination("Alt+shortcut+H"));
        MenuItem menuShowAll = new MenuItem("Alle einblenden");
        menuShowAll.setDisable(true);
        menuSystemPreferences.getItems().addAll(new MenuItem("\u00DCber Systemeinstellungen"), new SeparatorMenuItem(), menuServices,
                new SeparatorMenuItem(), menuHide, menuHideOthers, menuShowAll, new MenuItem("Systemeinstellungen beenden"));

        Menu menuEdit = new Menu("Bearbeiten");
        menuEdit.getItems().addAll(new MenuItem("..."));

        Menu menuPreferences = new Menu("Einstellungen");
        menuPreferences.getItems().addAll(new MenuItem("..."));

        Menu menuWindow = new Menu("Fenster");
        MenuItem windowClose = new MenuItem("Schlie\u00dfen");
        windowClose.setAccelerator(KeyCombination.keyCombination("shortcut+W"));
        MenuItem windowDock = new MenuItem("Im Dock ablegen");
        windowDock.setAccelerator(KeyCombination.keyCombination("shortcut+M"));
        CheckMenuItem actual = new CheckMenuItem("Datum & Uhrzeit");
        actual.setSelected(true);
        menuWindow.getItems().addAll(windowClose, windowDock, new SeparatorMenuItem(), actual);

        Menu menuHelp = new Menu("Hilfe");
        MenuItem help = new MenuItem("Systemeinstellungen-Hilfe");
        help.setAccelerator(KeyCombination.keyCombination("shortcut+?"));
        menuHelp.getItems().addAll(new MenuItem("Schlie\u00dfen"), help);

        menuBar.getMenus().addAll(menuSystemPreferences, menuEdit, menuPreferences, menuWindow, menuHelp);
        pane.getChildren().add(menuBar);

        Scene myScene = new Scene(pane, 667, 563);
        AquaFx.style();
        stage.setTitle("AquaFX");
        stage.setScene(myScene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
