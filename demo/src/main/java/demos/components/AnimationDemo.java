package demos.components;

import com.jfoenix.animation.JFXNodesAnimation;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import static javafx.animation.Interpolator.EASE_BOTH;

public class AnimationDemo extends Application {


    public static final String STYLE = "-fx-background-radius:50; -fx-min-width:50; -fx-min-height:50;";

    @Override
    public void start(Stage stage) {

        FlowPane main = new FlowPane();
        main.setVgap(20);
        main.setHgap(20);

        StackPane colorPane = new StackPane();
        colorPane.setStyle(STYLE);
        colorPane.getStyleClass().add("red-500");
        main.getChildren().add(colorPane);

        StackPane colorPane1 = new StackPane();
        colorPane1.setStyle(STYLE);
        colorPane1.getStyleClass().add("blue-500");

        StackPane placeHolder = new StackPane(colorPane1);
        placeHolder.setStyle(STYLE);
        main.getChildren().add(placeHolder);


        StackPane colorPane2 = new StackPane();
        colorPane2.setStyle(STYLE);
        colorPane2.getStyleClass().add("green-500");
        main.getChildren().add(colorPane2);

        StackPane colorPane3 = new StackPane();
        colorPane3.setStyle(STYLE);
        colorPane3.getStyleClass().add("yellow-500");
        main.getChildren().add(colorPane3);


        StackPane colorPane4 = new StackPane();
        colorPane4.setStyle(STYLE);
        colorPane4.getStyleClass().add("purple-500");
        main.getChildren().add(colorPane4);


        StackPane wizard = new StackPane();
        wizard.getChildren().add(main);
        StackPane.setMargin(main, new Insets(100));
        wizard.setStyle("-fx-background-color:WHITE");

        StackPane nextPage = new StackPane();

        StackPane newPlaceHolder = new StackPane();
        newPlaceHolder.setStyle("-fx-background-radius:50; -fx-max-width:50; -fx-max-height:50;");
        nextPage.getChildren().add(newPlaceHolder);
        StackPane.setAlignment(newPlaceHolder, Pos.TOP_LEFT);


        JFXHamburger h4 = new JFXHamburger();
        h4.setMaxSize(40, 40);
        HamburgerBackArrowBasicTransition burgerTask3 = new HamburgerBackArrowBasicTransition(h4);
        burgerTask3.setRate(-1);
        h4.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            burgerTask3.setRate(burgerTask3.getRate() * -1);
            burgerTask3.play();
        });
        nextPage.getChildren().add(h4);
        StackPane.setAlignment(h4, Pos.TOP_LEFT);
        StackPane.setMargin(h4, new Insets(10));


        JFXNodesAnimation<FlowPane, StackPane> animation = new FlowPaneStackPaneJFXNodesAnimation(main,
                                                                                                  nextPage,
                                                                                                  wizard,
                                                                                                  colorPane1);

        colorPane1.setOnMouseClicked((click) -> animation.animate());

        final Scene scene = new Scene(wizard, 800, 200);
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(ButtonDemo.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                           ButtonDemo.class.getResource("/css/jfoenix-components.css").toExternalForm());
        stage.setTitle("JFX Button Demo");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    private static final class FlowPaneStackPaneJFXNodesAnimation extends JFXNodesAnimation<FlowPane, StackPane> {
        private final Pane tempPage;
        private final FlowPane main;
        private final StackPane nextPage;
        private final StackPane wizard;
        private final StackPane colorPane1;

        private double newX;
        private double newY;

        FlowPaneStackPaneJFXNodesAnimation(final FlowPane main, final StackPane nextPage, final StackPane wizard,
                                           final StackPane colorPane1) {
            super(main, nextPage);
            this.main = main;
            this.nextPage = nextPage;
            this.wizard = wizard;
            this.colorPane1 = colorPane1;
            tempPage = new Pane();
            newX = 0;
            newY = 0;
        }

        @Override
        public void init() {
            nextPage.setOpacity(0);
            wizard.getChildren().add(tempPage);
            wizard.getChildren().add(nextPage);
            newX = colorPane1.localToScene(colorPane1.getBoundsInLocal()).getMinX();
            newY = colorPane1.localToScene(colorPane1.getBoundsInLocal()).getMinY();
            tempPage.getChildren().add(colorPane1);
            colorPane1.setTranslateX(newX);
            colorPane1.setTranslateY(newY);
        }

        @Override
        public void end() {

        }

        @Override
        public Animation animateSharedNodes() {
            return new Timeline();
        }

        @Override
        public Animation animateExit() {
            final Integer endValue = 0;
            return new Timeline(
                new KeyFrame(Duration.millis(300),
                             new KeyValue(main.opacityProperty(), endValue, EASE_BOTH)),
                new KeyFrame(Duration.millis(520),
                             new KeyValue(colorPane1.translateXProperty(), endValue, EASE_BOTH),
                             new KeyValue(colorPane1.translateYProperty(), endValue, EASE_BOTH)),
                new KeyFrame(Duration.millis(200),
                             new KeyValue(colorPane1.scaleXProperty(), 1, EASE_BOTH),
                             new KeyValue(colorPane1.scaleYProperty(), 1, EASE_BOTH)),
                new KeyFrame(Duration.millis(1000),
                             new KeyValue(colorPane1.scaleXProperty(), 40, EASE_BOTH),
                             new KeyValue(colorPane1.scaleYProperty(), 40, EASE_BOTH)));
        }

        @Override
        public Animation animateEntrance() {
            return new Timeline(new KeyFrame(Duration.millis(320),
                                             new KeyValue(nextPage.opacityProperty(), 1, EASE_BOTH)));
        }

    }
}
