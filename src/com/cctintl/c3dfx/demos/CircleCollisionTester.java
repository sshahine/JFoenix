package com.cctintl.c3dfx.demos;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import javafx.scene.shape.*;

public class CircleCollisionTester extends Application {

  private ArrayList<Shape> nodes;

  public static void main(String[] args) { launch(args); }

  @Override public void start(Stage primaryStage) {
    primaryStage.setTitle("Drag circles around to see collisions");
    Group root = new Group();
    Scene scene = new Scene(root, 400, 400);

    nodes = new ArrayList<>();
    nodes.add(new Circle(15, 15, 30));
    nodes.add(new Circle(90, 60, 30));
    nodes.add(new Circle(40, 200, 30));
    for (Shape block : nodes) {
      setDragListeners(block);
    }
    root.getChildren().addAll(nodes);
    checkShapeIntersection(nodes.get(nodes.size() - 1));

    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public void setDragListeners(final Shape block) {
    final Delta dragDelta = new Delta();

    block.setOnMousePressed(new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent mouseEvent) {
        // record a delta distance for the drag and drop operation.
        dragDelta.x = block.getLayoutX() - mouseEvent.getSceneX();
        dragDelta.y = block.getLayoutY() - mouseEvent.getSceneY();
        block.setCursor(Cursor.NONE);
      }
    });
    block.setOnMouseReleased(new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent mouseEvent) {
        block.setCursor(Cursor.HAND);
      }
    });
    block.setOnMouseDragged(new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent mouseEvent) {
        block.setLayoutX(mouseEvent.getSceneX() + dragDelta.x);
        block.setLayoutY(mouseEvent.getSceneY() + dragDelta.y);
        checkShapeIntersection(block);
      }
    });
  }

  private void checkShapeIntersection(Shape block) {
    boolean collisionDetected = false;
    for (Shape static_bloc : nodes) {
      if (static_bloc != block) {
        static_bloc.setFill(Color.GREEN);

        Shape intersect = Shape.intersect(block, static_bloc);
        if (intersect.getBoundsInLocal().getWidth() != -1) {
          collisionDetected = true;
        }
      }
    }

    if (collisionDetected) {
      block.setFill(Color.BLUE);
    } else {
      block.setFill(Color.GREEN);
    }
  }

  class Delta { double x, y; }
}