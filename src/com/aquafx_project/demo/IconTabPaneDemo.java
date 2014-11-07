package com.aquafx_project.demo;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.aquafx_project.AquaFx;
import com.aquafx_project.controls.skin.styles.TabPaneType;

public class IconTabPaneDemo extends Application {

    @Override public void start(Stage stage) {
        stage.setTitle("icon-TP");
        AquaFx.styleStage(stage, StageStyle.UNIFIED);
        Pane root = new Pane();
        Scene scene = new Scene(root);

        /*
         * Tabpane section
         */
        TabPane tabPane = new TabPane();
        AquaFx.createTabPaneStyler().setType(TabPaneType.ICON_BUTTONS).style(tabPane);

        Tab tab1 = new Tab("Allgemein");
        Image image = new Image(AquaFx.class.getResource("demo/images/preferences/allgemein.png").toExternalForm());
        ImageView pages = new ImageView(image);
        pages.setPreserveRatio(true);
        pages.setFitHeight(36);
        tab1.setGraphic(pages);
        Label label =new Label("Allgemein...");
        label.setPadding(new Insets(15));
        tab1.setContent(label);
        tabPane.getTabs().add(tab1);

        Tab tab2 = new Tab("Etiketten");
        Image image2 = new Image(AquaFx.class.getResource("demo/images/preferences/labels.png").toExternalForm());
        ImageView layout =  new ImageView(image2);
        layout.setPreserveRatio(true);
        layout.setFitHeight(36);
        tab2.setGraphic(layout);
        Label label2 = new Label("Etiketten");
        label2.setPadding(new Insets(15));
        tab2.setContent(label2);
        tabPane.getTabs().add(tab2);

        Tab tab3 = new Tab("Seitenleiste");
        Image image3 = new Image(AquaFx.class.getResource("demo/images/preferences/seitenleiste.png").toExternalForm());
        ImageView umbruch = new ImageView(image3);
        umbruch.setPreserveRatio(true);
        umbruch.setFitHeight(36);
        tab3.setGraphic(umbruch);
        Label label3 = new Label("seitenleiste...");
        label3.setPadding(new Insets(15));
        tab3.setContent(label3);
        tabPane.getTabs().add(tab3);

        Tab tab4 = new Tab("Erweitert");
        Image image4 = new Image(AquaFx.class.getResource("demo/images/preferences/einstellungen.png").toExternalForm());
        ImageView text = new ImageView(image4);
        text.setPreserveRatio(true);
        text.setFitHeight(36);
        tab4.setGraphic(text);

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(20, 20, 18, 20));
        vbox.setSpacing(8);

        CheckBox box1 = new CheckBox("Alle Dateinamensuffixe einblenden");
        CheckBox box2 = new CheckBox("Vor dem \u00C4ndern eines Suffixes nachfragen");
        box2.setSelected(true);
        CheckBox box3 = new CheckBox("Vor dem Entleeren des Papierkorbs nachfragen");
        box3.setSelected(true);
        CheckBox box4 = new CheckBox("Papierkorb sicher entleeren");

        Label info = new Label("Bei Suchvorg\u00E4ngen:");
        info.setPadding(new Insets(9, 0, 0, 0));

        ObservableList<String> items = FXCollections.observableArrayList("Diesen Mac durchsuchen", "Aktuellen Ordner durchsuchen",
                "Letzten Suchbereich verwenden");
        ChoiceBox<String> choice = new ChoiceBox<String>(items);
        
        choice.getSelectionModel().selectFirst();
        vbox.getChildren().addAll(box1, box2, box3, box4, info, choice);

        tab4.setContent(vbox);
        tabPane.getTabs().add(tab4);
        tabPane.getSelectionModel().selectLast();
        root.getChildren().add(tabPane);

        AquaFx.style();
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}