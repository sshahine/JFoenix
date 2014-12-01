package com.cctintl.c3dfx.demos;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FishSim extends Application {
  private static final Paint SCENE_FILL = new RadialGradient(
    0, 0, 300, 300, 500, false, CycleMethod.NO_CYCLE, 
    FXCollections.observableArrayList(new Stop(0, Color.BLACK), new Stop(1, Color.BLUE))
  );

  @Override public void start(Stage stage) {
    final RippleGenerator rippler = new RippleGenerator();

    final Scene scene = new Scene(rippler, 600, 400, SCENE_FILL);

    scene.setOnMousePressed(new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent event) {
        rippler.setGeneratorCenterX(event.getSceneX());
        rippler.setGeneratorCenterY(event.getSceneY());
        rippler.createRipple();
        rippler.startGenerating();
      }
    });

    scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent event) {
        rippler.setGeneratorCenterX(event.getSceneX());
        rippler.setGeneratorCenterY(event.getSceneY());
      }
    });

    scene.setOnMouseReleased(new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent event) {
        rippler.stopGenerating();
      }
    });

    stage.setTitle("Click, hold mouse button down and move around to create ripples");
    stage.setScene(scene);
    stage.setResizable(false);

    stage.show();
  }

  public static void main(String[] args) { launch(args); }
}

/**
 * Generates ripples on the screen every 0.5 seconds or whenever
 * the createRipple method is called. Ripples grow and fade out
 * over 3 seconds
 */
class RippleGenerator extends Group {
  private class Ripple extends Circle {
    Timeline animation = new Timeline(
      new KeyFrame(Duration.ZERO,       new KeyValue(radiusProperty(),  0)),
      new KeyFrame(Duration.seconds(1), new KeyValue(opacityProperty(), 1)),
      new KeyFrame(Duration.seconds(3), new KeyValue(radiusProperty(),  100)),
      new KeyFrame(Duration.seconds(3), new KeyValue(opacityProperty(), 0))
    );

    private Ripple(double centerX, double centerY) {
      super(centerX, centerY, 0, null);
      setStroke(Color.rgb(200, 200, 255));
    }
  }

  private double generatorCenterX = 100.0;
  private double generatorCenterY = 100.0;

  private Timeline generate = new Timeline(
      new KeyFrame(Duration.seconds(0.5), new EventHandler<ActionEvent>() {
        @Override public void handle(ActionEvent event) {
          createRipple();
        }
      }
    )
  );

  public RippleGenerator() {
    generate.setCycleCount(Timeline.INDEFINITE);
  }

  public void createRipple() {
    final Ripple ripple = new Ripple(generatorCenterX, generatorCenterY);
    getChildren().add(ripple);
    ripple.animation.play();

    Timeline remover = new Timeline(
      new KeyFrame(Duration.seconds(3), new EventHandler<ActionEvent>() {
        @Override public void handle(ActionEvent event) {
          getChildren().remove(ripple);
          ripple.animation.stop(); 
        }
      })  
    );
    remover.play();
  }

  public void startGenerating() {
    generate.play();
  }

  public void stopGenerating() {
    generate.stop();
  }

  public void setGeneratorCenterX(double generatorCenterX) {
    this.generatorCenterX = generatorCenterX;
  }

  public void setGeneratorCenterY(double generatorCenterY) {
    this.generatorCenterY = generatorCenterY;
  }
}