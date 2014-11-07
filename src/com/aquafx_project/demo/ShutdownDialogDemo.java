package com.aquafx_project.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.aquafx_project.AquaFx;

public class ShutdownDialogDemo extends Application {

    public static void main(String[] args) {
        ShutdownDialogDemo.launch();
    }

    @Override public void start(Stage stage) throws Exception {
        BorderPane borderPane = new BorderPane();

        Image image = new Image(AquaFx.class.getResource("demo/images/bild.png").toExternalForm());
        ImageView iv = new ImageView(image);

        BorderPane.setMargin(iv, new Insets(18, 0, 0, 18));
        borderPane.setLeft(iv);

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(20, 20, 0, 20));
        vbox.setSpacing(10);
        Label title = new Label("M\u00F6chten Sie den Computer jetzt ausschalten?");
        title.setStyle("-fx-font-weight: bold");
        vbox.getChildren().add(title);

        Label info = new Label("Wenn Sie keine Auswahl treffen, wird der Computer in 43 Sekunden automatisch ausgeschaltet.");
        info.setStyle("-fx-font-size : 0.8em");
        info.setWrapText(true);
        VBox.setMargin(info, new Insets(14, 0, 0, 0));
        vbox.getChildren().add(info);

        CheckBox checkBox = new CheckBox("Beim n\u00E4chsten Anmelden alle Fenster wieder \u00F6ffnen");
        checkBox.setAllowIndeterminate(false);
        vbox.getChildren().add(checkBox);

        borderPane.setCenter(vbox);

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_RIGHT);
        hbox.setPadding(new Insets(19));
        hbox.setSpacing(12);

        Button cancel = new Button();
        cancel.setText("Abbrechen");
        cancel.setCancelButton(true);
        hbox.getChildren().add(cancel);

        Button off = new Button();
        off.setText("Ausschalten");
        off.setDefaultButton(true);
        hbox.getChildren().add(off);

        borderPane.setBottom(hbox);

        Scene myScene = new Scene(borderPane, 470, 172);
        AquaFx.style();
        stage.setResizable(false);
        stage.setScene(myScene);
        stage.show();
    }

}
