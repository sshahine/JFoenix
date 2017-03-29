
package demos.components;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXSlider.IndicatorPosition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SliderDemo extends Application {

    @Override
    public void start(Stage stage) {
        JFXSlider horLeftSlider = new JFXSlider();
        horLeftSlider.setMinWidth(500);

        JFXSlider horRightSlider = new JFXSlider();
        horRightSlider.setMinWidth(500);
        horRightSlider.setIndicatorPosition(IndicatorPosition.RIGHT);

        JFXSlider verLeftSlider = new JFXSlider();
        verLeftSlider.setMinHeight(500);
        verLeftSlider.setOrientation(Orientation.VERTICAL);

        JFXSlider verRightSlider = new JFXSlider();
        verRightSlider.setMinHeight(500);
        verRightSlider.setOrientation(Orientation.VERTICAL);
        verRightSlider.setIndicatorPosition(IndicatorPosition.RIGHT);

        HBox hbox = new HBox();
        hbox.setSpacing(450);
        hbox.getChildren().addAll(verRightSlider, verLeftSlider);

        VBox vbox = new VBox();
        Button click = new Button("Click");
        vbox.getChildren().addAll(horRightSlider, horLeftSlider, hbox, click);
        vbox.setSpacing(100);
        vbox.setPadding(new Insets(100, 50, 50, 150));

        Scene scene = new Scene(new Group());
        ((Group) scene.getRoot()).getChildren().add(vbox);
        scene.getStylesheets().add(SliderDemo.class.getResource("/css/jfoenix-components.css").toExternalForm());
        stage.setScene(scene);
        stage.setWidth(900);
        stage.setHeight(900);
        stage.show();
        stage.setTitle("JFX Slider Demo");

        MyDialog dilaog = new MyDialog(stage);
        dilaog.initStyle(StageStyle.UNDECORATED);
        dilaog.initOwner(stage);
        dilaog.initModality(Modality.NONE);
        click.setOnAction((action) -> dilaog.show());
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static final class MyDialog extends Dialog {
        MyDialog(Stage stage) {
            stage.getScene().widthProperty().addListener((o, oldVal, newVal) -> {
                setWidth(newVal.doubleValue());
            });
            stage.getScene().heightProperty().addListener((o, oldVal, newVal) -> {
                setHeight(newVal.doubleValue());
            });
            stage.xProperty().addListener((o, oldVal, newVal) -> {
                setX(newVal.doubleValue());
            });
            stage.yProperty().addListener((o, oldVal, newVal) -> {
                setY(newVal.doubleValue());
            });
        }
    }
}
