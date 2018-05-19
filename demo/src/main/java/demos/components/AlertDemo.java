package demos.components;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import com.jfoenix.controls.JFXDrawer.DrawerDirection;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.*;
import javafx.util.Duration;

import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;

public class AlertDemo extends Application {

    @Override
    public void start(Stage stage) {

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setBody(new Label("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."));

        Button leftButton = new JFXButton("Alert");
        leftButton.setLayoutX(50);
        leftButton.setLayoutY(50);

        final Scene scene = new Scene(new Group(leftButton), 800, 800);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();

        JFXAlert<Void> alert = new JFXAlert<>(stage);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(layout);
        alert.initModality(Modality.NONE);
        leftButton.setOnAction(action-> alert.show());
    }

    public static void main(String[] args) {
        launch(args);
    }

}
