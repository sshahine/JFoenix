package demos.components.base;

import com.jfoenix.controls.JFXDecorator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Builder;

public abstract class Overdrive extends Application implements Builder<Node> {

    @Override
    public void start(Stage stage) {

        Pane root = new Pane();
        for(int i = 0 ;i < 4000; i++) {
            Label child = new Label("label" + i);
            child.setLayoutX(Math.random() * 500 + 100);
            child.setLayoutY(Math.random() * 500+ 100);
            root.getChildren().add(child);

        }
        root.getChildren().add(build());
        FPSDecorator decorator = new FPSDecorator(stage, root);
        final Scene scene = new Scene(decorator, 800, 800);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
        afterShow(stage);
    }

    protected void afterShow(Stage stage){

    }

    public static void main(String[] args) {
        launch(args);
    }


    class FPSDecorator extends JFXDecorator {
        private final long[] frameTimes = new long[100];
        private int frameTimeIndex = 0 ;
        private boolean arrayFilled = false ;
        public FPSDecorator(Stage stage, Node node) {
            super(stage, node);
            Text fpsLabel = new Text();
            fpsLabel.setFill(Color.WHITE);
            AnimationTimer frameRateMeter = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    long oldFrameTime = frameTimes[frameTimeIndex] ;
                    frameTimes[frameTimeIndex] = now ;
                    frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length ;
                    if (frameTimeIndex == 0) {
                        arrayFilled = true ;
                    }
                    if (arrayFilled) {
                        long elapsedNanos = now - oldFrameTime ;
                        long elapsedNanosPerFrame = elapsedNanos / frameTimes.length ;
                        double frameRate = 1_000_000_000.0 / elapsedNanosPerFrame ;
                        fpsLabel.setText(String.format("Current frame rate: %.3f", frameRate));
                    }
                }
            };
            frameRateMeter.start();
            setGraphic(fpsLabel);
        }
    }

}
