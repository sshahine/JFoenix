package demos.components;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXButton.ButtonType;
import com.jfoenix.controls.JFXNodesList;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;


public class NodesListDemo extends Application {

    private static final String FX_TEXT_FILL_WHITE = "-fx-text-fill:WHITE";
    private static final String ANIMATED_OPTION_BUTTON = "animated-option-button";
    private static final String ANIMATED_OPTION_SUB_BUTTON = "animated-option-sub-button";
    private static final String ANIMATED_OPTION_SUB_BUTTON2 = "animated-option-sub-button2";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        JFXButton ssbutton1 = new JFXButton();
        Label sslabel = new Label("R1");
        sslabel.setStyle(FX_TEXT_FILL_WHITE);
        ssbutton1.setGraphic(sslabel);
        ssbutton1.setButtonType(ButtonType.RAISED);
        ssbutton1.getStyleClass().addAll(ANIMATED_OPTION_BUTTON, ANIMATED_OPTION_SUB_BUTTON2);

        JFXButton ssbutton2 = new JFXButton("R2");
        ssbutton2.setTooltip(new Tooltip("Button R2"));
        ssbutton2.setButtonType(ButtonType.RAISED);
        ssbutton2.getStyleClass().addAll(ANIMATED_OPTION_BUTTON, ANIMATED_OPTION_SUB_BUTTON2);

        JFXButton ssbutton3 = new JFXButton("R3");
        ssbutton3.setButtonType(ButtonType.RAISED);
        ssbutton3.getStyleClass().addAll(ANIMATED_OPTION_BUTTON, ANIMATED_OPTION_SUB_BUTTON2);


        JFXNodesList nodesList3 = new JFXNodesList();
        nodesList3.setSpacing(10);
        // init nodes
        nodesList3.addAnimatedNode(ssbutton1);
        nodesList3.addAnimatedNode(ssbutton2);
        nodesList3.addAnimatedNode(ssbutton3);


        JFXButton sbutton1 = new JFXButton();
        Label slabel = new Label("B1");
        slabel.setStyle(FX_TEXT_FILL_WHITE);
        sbutton1.setGraphic(slabel);
        sbutton1.setButtonType(ButtonType.RAISED);
        sbutton1.getStyleClass().addAll(ANIMATED_OPTION_BUTTON, ANIMATED_OPTION_SUB_BUTTON);

        JFXButton sbutton2 = new JFXButton("B2");
        sbutton2.setTooltip(new Tooltip("Button B2"));
        sbutton2.setButtonType(ButtonType.RAISED);
        sbutton2.getStyleClass().addAll(ANIMATED_OPTION_BUTTON, ANIMATED_OPTION_SUB_BUTTON);

        JFXButton sbutton3 = new JFXButton("B3");
        sbutton3.setButtonType(ButtonType.RAISED);
        sbutton3.getStyleClass().addAll(ANIMATED_OPTION_BUTTON, ANIMATED_OPTION_SUB_BUTTON);


        JFXNodesList nodesList2 = new JFXNodesList();
        nodesList2.setSpacing(10);
        // init nodes
        nodesList2.addAnimatedNode(sbutton1);
        nodesList2.addAnimatedNode(nodesList3);
        nodesList2.addAnimatedNode(sbutton2);
        nodesList2.addAnimatedNode(sbutton3);
        nodesList2.setRotate(90);


        JFXButton button1 = new JFXButton();
        Label label = new Label("G1");
        button1.setGraphic(label);
        label.setStyle(FX_TEXT_FILL_WHITE);
        button1.setButtonType(ButtonType.RAISED);
        button1.getStyleClass().add(ANIMATED_OPTION_BUTTON);

        JFXButton button2 = new JFXButton("G2");
        button2.setTooltip(new Tooltip("Button G2"));
        button2.setButtonType(ButtonType.RAISED);
        button2.getStyleClass().add(ANIMATED_OPTION_BUTTON);

        JFXButton button3 = new JFXButton("G3");
        button3.setButtonType(ButtonType.RAISED);
        button3.getStyleClass().add(ANIMATED_OPTION_BUTTON);

        JFXNodesList nodesList = new JFXNodesList();
        nodesList.setSpacing(10);
        nodesList.addAnimatedNode(button1);
        final HBox container = new HBox(new JFXButton("INFO"), button2);
        container.setAlignment(Pos.CENTER_RIGHT);
        container.setSpacing(12);
        nodesList.addAnimatedNode(container, (expanded, duration) -> {
                List<KeyFrame> frames = new ArrayList<>();
                frames.add(new KeyFrame(duration,
                    new KeyValue(container.getChildren().get(1).scaleXProperty(), expanded ? 1 : 0, Interpolator.EASE_BOTH),
                    new KeyValue(container.getChildren().get(1).scaleYProperty(), expanded ? 1 : 0, Interpolator.EASE_BOTH)
                ));
                frames.add(new KeyFrame(Duration.millis(duration.toMillis()),
                    new KeyValue(container.getChildren().get(0).opacityProperty(), expanded ? 0 : 1, Interpolator.EASE_BOTH),
                    new KeyValue(container.getChildren().get(0).translateXProperty(), expanded ? 20 : 0, Interpolator.EASE_BOTH)
                ));
                frames.add(new KeyFrame(Duration.millis(duration.toMillis() + 40),
                    new KeyValue(container.getChildren().get(0).opacityProperty(), expanded ? 1 : 0, Interpolator.EASE_BOTH),
                    new KeyValue(container.getChildren().get(0).translateXProperty(), expanded ? 0 : 20, Interpolator.EASE_BOTH)
                ));
                return frames;
            }
        );

        JFXNodesList.alignNodeToChild(container, button2);
        container.getChildren().get(0).setOpacity(0);
        container.setScaleX(1);
        container.setScaleY(1);

        nodesList.addAnimatedNode(nodesList2);
        nodesList.addAnimatedNode(button3);
        nodesList.setRotate(180);


        StackPane main = new StackPane();
        main.setPadding(new Insets(10));

        JFXButton clickButton = new JFXButton("Click Me");
        clickButton.setTranslateY(-50);
        clickButton.setTranslateX(-100);
        main.getChildren().add(clickButton);
        main.getChildren().add(nodesList);

        Scene scene = new Scene(main, 600, 600);
        scene.getStylesheets().add(NodesListDemo.class.getResource("/css/jfoenix-components.css").toExternalForm());
        stage.setScene(scene);

//        ScenicView.show(scene);
        stage.show();
    }
}
