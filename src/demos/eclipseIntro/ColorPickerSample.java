package demos.eclipseIntro;


import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

public class ColorPickerSample extends Application {
      
    ImageView logo = new ImageView(
        new Image(getClass().getResourceAsStream("OracleLogo.png"))
    );
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("ColorPickerSample");

        Scene scene = new Scene(new VBox(20), 300, 300);
        //scene.getStylesheets().add("colorpickersample/ControlStyle.css");
        scene.setFill(Color.web("#ccffcc"));
        VBox box = (VBox) scene.getRoot();

        ToolBar tb = new ToolBar();
        box.getChildren().add(tb);

        final ComboBox logoSamples = new ComboBox();
        logoSamples.setPromptText("Logo");
        logoSamples.setValue("Oracle");
        logoSamples.getItems().addAll(
                "Oracle",
                "Java",
                "JavaFX",
                "Cup");
        
        logoSamples.valueProperty().addListener(new ChangeListener<String>() {
            @Override 
            public void changed(ObservableValue ov, String t, String t1) {                
                logo.setImage(
                    new Image(getClass().getResourceAsStream(t1+"Logo.png"))
                );                  
            }    
        });
        

        final ColorPicker colorPicker = new ColorPicker();  
        colorPicker.setValue(Color.CORAL);
        //colorPicker.getStyleClass().add("button");
        tb.getItems().addAll(logoSamples, colorPicker);

        StackPane stack = new StackPane();
        box.getChildren().add(stack);

        final SVGPath svg = new SVGPath();
        svg.setContent("M70,50 L90,50 L120,90 L150,50 L170,50"
            + "L210,90 L180,120 L170,110 L170,200 L70,200 L70,110 L60,120 L30,90"
            + "L70,50");
        svg.setStroke(Color.DARKGREY);
        svg.setStrokeWidth(2);
        svg.setEffect(new DropShadow());
        svg.setFill(colorPicker.getValue());
        stack.getChildren().addAll(svg, logo);

        colorPicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                svg.setFill(colorPicker.getValue());                    
            }
        });

        stage.setScene(scene);
        stage.show();
    }
}