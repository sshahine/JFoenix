package com.aquafx_project.demo;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import com.aquafx_project.AquaFx;
import com.aquafx_project.controls.skin.styles.ButtonType;
import com.aquafx_project.controls.skin.styles.MacOSDefaultIcons;
import com.aquafx_project.controls.skin.styles.TextFieldType;
import com.aquafx_project.nativestuff.NsImageIcon;
import com.aquafx_project.nativestuff.NsImageIconLoader;

public class AquaNetworkDemo extends Application {
    private int sceneHeight = 588;

    @Override public void start(Stage stage) throws Exception {
        AquaFx.styleStage(stage, StageStyle.UNIFIED);
        stage.setResizable(false);

        VBox mainBox = new VBox();

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

        mainBox.getChildren().add(toolBar);
        /*
         * The top content
         */
        HBox header = new HBox();
        header.setSpacing(5);
        header.setPadding(new Insets(20, 0, 5, 0));
        header.setAlignment(Pos.CENTER);
        Label labelSurrounding = new Label("Umgebung:");
        ChoiceBox<Object> choices = new ChoiceBox<Object>();
        choices.setItems(FXCollections.observableArrayList("Automatisch", new Separator(), "Umgebungen bearbeiten ..."));
        choices.getSelectionModel().selectFirst();
        header.getChildren().addAll(labelSurrounding, choices);
        mainBox.getChildren().add(header);

        /*
         * The left-side content
         */
        HBox content = new HBox();
        content.setPadding(new Insets(10, 15, 10, 20));
        content.setSpacing(10);

        ListView<String> list = new ListView<String>();
        ObservableList<String> listItems = FXCollections.observableArrayList("WLAN", "Bluetooth-PAN");
        list.setItems(listItems);
        list.setPrefWidth(200);

        // Create a CellFactory for ListCells
        list.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override public ListCell<String> call(ListView<String> list) {
                return new NetworkCell();
            }
        });

        content.getChildren().add(list);

        /*
         * The right-side content
         */
        GridPane grid = new GridPane();
        // grid.setGridLinesVisible(true);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(18, 5, 8, 15));
        AquaFx.setGroupBox(grid);

        Label labelStatus = new Label("Status:");
        GridPane.setHalignment(labelStatus, HPos.RIGHT);
        grid.add(labelStatus, 0, 0, 2, 1);
        Label labelConnected = new Label("Verbunden");
        labelConnected.setStyle("-fx-font-weight: bold;");
        grid.add(labelConnected, 2, 0);
        Button btnDisableWlan = new Button("WLAN deaktivieren");
        GridPane.setHalignment(btnDisableWlan, HPos.RIGHT);
        grid.add(btnDisableWlan, 3, 0);
        Label labelCurrent = new Label("\"WLAN\" ist mit \"PrettyFlyForAWiFi\" verbunden und \n hat die IP-Adresse 192.168.0.104.");
        labelCurrent.setStyle("-fx-font-size: 11");
        grid.add(labelCurrent, 2, 2, 3, 1);
        Label labelNetworkName = new Label("Netzwerkname:");
        GridPane.setHalignment(labelNetworkName, HPos.RIGHT);
        grid.add(labelNetworkName, 0, 3);
        ChoiceBox<Object> choicesNetwork = new ChoiceBox<Object>();
        choicesNetwork.setItems(FXCollections.observableArrayList("PrettyFlyForAWiFi", new Separator(),
                "Mit anderem Netzwerk verbinden ...", "Netzwerk anlegen"));
        choicesNetwork.getSelectionModel().select(2);
        GridPane.setHalignment(choicesNetwork, HPos.RIGHT);
        GridPane.getHgrow(choicesNetwork);
        grid.add(choicesNetwork, 1, 3, 3, 1);
        CheckBox checkShowNew = new CheckBox("Auf neue Netzwerke hinweisen");
        checkShowNew.setSelected(true);
        grid.add(checkShowNew, 1, 4, 3, 1);
        Label labelExplanation = new Label("Bekannte Netzwerke wewrden automatisch verbunden. \n" + "Falls kein bekanntes Netzwerk vorhanden ist, werden \n" + "Sie vor dem Verbinden mit einem neuen Netzwerk \n" + "gefragt.");
        labelExplanation.setStyle("-fx-font-size: 10");
        grid.add(labelExplanation, 1, 5, 3, 1);
        VBox spacer = new VBox();
        spacer.setPrefHeight(140);
        spacer.setPrefWidth(200);
        grid.add(spacer, 3, 6);
        CheckBox chekShowState = new CheckBox("WLAN-Status in der \n" + "Men\u00FCleiste anzeigen");
        chekShowState.setSelected(true);
        GridPane.setValignment(chekShowState, VPos.TOP);
        grid.add(chekShowState, 0, 7, 2, 1);
        Button btmMore = new Button("Weitere Optionen ...");
        GridPane.setHalignment(btmMore, HPos.RIGHT);
        GridPane.setValignment(btmMore, VPos.BOTTOM);
        grid.add(btmMore, 2, 7, 2, 1);
        Button helpBtn = new Button("?");
        AquaFx.createButtonStyler().setType(ButtonType.HELP).style(helpBtn);
        GridPane.setValignment(helpBtn, VPos.BOTTOM);
        GridPane.setHalignment(helpBtn, HPos.RIGHT);
        grid.add(helpBtn, 4, 7);

        content.getChildren().add(grid);
        mainBox.getChildren().add(content);

        /*
         * Footer
         */
        HBox footerBox = new HBox();
        footerBox.setSpacing(5);
        footerBox.setAlignment(Pos.BOTTOM_LEFT);
        footerBox.setPadding(new Insets(0, 0, 5, 15));
        Image image = new Image(AquaFx.class.getResource("demo/images/lock_open.png").toExternalForm());
        ImageView lockView = new ImageView(image);
        lockView.setPreserveRatio(true);
        lockView.setFitHeight(36);
        footerBox.getChildren().add(lockView);
        Label info = new Label("Zum Sch\u00FCtzen auf das Schloss klicken.");
        info.setStyle("-fx-font-size: 12");
        info.setPadding(new Insets(0, 0, 3, 0));
        footerBox.getChildren().add(info);
        mainBox.getChildren().add(footerBox);

        HBox anchorButtons = new HBox();
        anchorButtons.setPadding(new Insets(0, 15, 0, 0));
        anchorButtons.setSpacing(15);
        anchorButtons.setAlignment(Pos.BOTTOM_RIGHT);
        Button btnAssis = new Button("Assistent ...");
        anchorButtons.getChildren().add(btnAssis);
        Button btnRev = new Button("Zur\u00FCcksetzen");
        btnRev.setDisable(true);
        anchorButtons.getChildren().add(btnRev);
        Button btnUse = new Button("Anwenden");
        btnUse.setDisable(true);
        anchorButtons.getChildren().add(btnUse);
        mainBox.getChildren().add(anchorButtons);

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
        mainBox.getChildren().add(menuBar);

        Scene myScene = new Scene(mainBox, 667, sceneHeight);
        AquaFx.style();
        stage.setTitle("Netzwerk (AquaFX)");
        stage.setScene(myScene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    static class NetworkCell extends ListCell<String> {
        @Override protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {

                HBox cellBox = new HBox();
                cellBox.setAlignment(Pos.CENTER_LEFT);
                cellBox.setSpacing(5);
                cellBox.setPadding(new Insets(2, 5, 3, 2));
                ImageView dotImage;
                VBox texts = new VBox();
                texts.setPrefWidth(95);
                texts.setAlignment(Pos.CENTER_LEFT);
                final Label type = new Label(item);
                final Label legend = new Label(item);
                legend.setStyle("-fx-font-size: 11; -fx-text-fill: gray;");
                ImageView typeImage;

                if (item.equals("WLAN")) {
                    dotImage = new ImageView(NsImageIconLoader.load(NsImageIcon.STATUS_AVAILABLE));
                    legend.setText("Verbunden");
                    Image img = new Image(AquaFx.class.getResource("demo/images/wifi.png").toExternalForm());
                    typeImage = new ImageView(img);
                    typeImage.setPreserveRatio(true);
                    typeImage.setFitHeight(32);
                } else {
                    dotImage = new ImageView(NsImageIconLoader.load(NsImageIcon.STATUS_PARTIALLY_AVAILABLE));
                    legend.setText("Keine IP-Adresse");
                    Image img = new Image(AquaFx.class.getResource("demo/images/bluetooth.png").toExternalForm());
                    typeImage = new ImageView(img);
                    typeImage.setPreserveRatio(true);
                    typeImage.setFitHeight(32);
                }

                cellBox.getChildren().add(dotImage);
                texts.getChildren().add(type);
                texts.getChildren().add(legend);
                cellBox.getChildren().add(texts);
                cellBox.getChildren().add(typeImage);

                setGraphic(cellBox);

                selectedProperty().addListener(new ChangeListener<Boolean>() {

                    @Override public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                            Boolean newValue) {
                        if (newValue) {
                            type.setStyle("-fx-text-fill: white;");
                            legend.setStyle("-fx-font-size: 11; -fx-text-fill: white;");
                        } else {
                            type.setStyle("-fx-text-fill: black;");
                            legend.setStyle("-fx-font-size: 11; -fx-text-fill: gray;");
                        }
                    }
                });
            }

        }
    }
}
