package demos.components;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXPopup.PopupHPosition;
import com.jfoenix.controls.JFXPopup.PopupVPosition;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXRippler.RipplerMask;
import com.jfoenix.controls.JFXRippler.RipplerPos;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class PopupDemo extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        JFXHamburger show = new JFXHamburger();
        show.setPadding(new Insets(10, 5, 10, 5));
        JFXRippler rippler = new JFXRippler(show, RipplerMask.CIRCLE, RipplerPos.BACK);

        JFXListView<Label> list = new JFXListView<>();
        for (int i = 1; i < 5; i++) {
            list.getItems().add(new Label("Item " + i));
        }

        AnchorPane container = new AnchorPane();
        container.getChildren().add(rippler);
        AnchorPane.setLeftAnchor(rippler, 200.0);
        AnchorPane.setTopAnchor(rippler, 210.0);

        StackPane main = new StackPane();
        main.getChildren().add(container);

        JFXPopup popup = new JFXPopup(list);
        rippler.setOnMouseClicked(e -> popup.show(rippler, PopupVPosition.TOP, PopupHPosition.LEFT));

        final Scene scene = new Scene(main, 800, 800);
        scene.getStylesheets().add(PopupDemo.class.getResource("/css/jfoenix-components.css").toExternalForm());

        primaryStage.setTitle("JFX Popup Demo");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
