package demos;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class ClipImageDemo extends Application {
	public DoubleProperty xCordinate;
	public DoubleProperty yCordinate;
	@Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Image image = new Image(ClipImageDemo.class.getResource("../Desert.jpg").toExternalForm());
        Scene scene = new Scene(root, image.getWidth(), image.getHeight(), Color.WHITE);
        final ImageView view = new ImageView();
        view.setImage(image);

        xCordinate = new SimpleDoubleProperty(100.0f);
        yCordinate = new SimpleDoubleProperty(100.0f);


        final Circle c1 = new Circle();
        c1.centerXProperty().bind(xCordinate);
        c1.centerYProperty().bind(yCordinate);
        c1.setRadius(50.0f);

        final Circle c2 = new Circle();
        c2.centerXProperty().bind(xCordinate);
        c2.centerYProperty().bind(yCordinate);
        c2.setRadius(35.0f);

        scene.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xCordinate.set(event.getX());
                yCordinate.set(event.getY());
                System.out.println("xCordinate " + xCordinate + " yCordinate " + yCordinate);
                // update mask clip
                Shape mask = Path.subtract(c1, c2);
                view.setClip(mask);
            }
        });

        Shape mask = Path.subtract(c1, c2);
        view.setClip(mask);

        root.getChildren().add(view);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

     public static void main(String[] args) {
        launch(args);
    }
}