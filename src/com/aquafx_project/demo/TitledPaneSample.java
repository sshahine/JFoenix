package com.aquafx_project.demo;

import com.aquafx_project.AquaFx;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
 
public class TitledPaneSample extends Application {
    final String[] imageNames = new String[]{"bild", "bluetooth", "wifi"};
    final Image[] images = new Image[imageNames.length];
    final ImageView[] pics = new ImageView[imageNames.length];
    final TitledPane[] tps = new TitledPane[imageNames.length];
    final Label label = new Label("N/A");
       
    public static void main(String[] args) {
        launch(args);
    }
 
    @Override public void start(Stage stage) {
        stage.setTitle("TitledPane");
        Scene scene = new Scene(new Group(), 400, 250);
        
        // --- GridPane container
        TitledPane gridTitlePane = new TitledPane();
        GridPane grid = new GridPane();
        grid.setVgap(4);
        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.add(new Label("To: "), 0, 0);
        grid.add(new TextField(), 1, 0);
        grid.add(new Label("Cc: "), 0, 1);
        grid.add(new TextField(), 1, 1);
        grid.add(new Label("Subject: "), 0, 2);
        grid.add(new TextField(), 1, 2);        
        grid.add(new Label("Attachment: "), 0, 3);
        grid.add(label,1, 3);
        gridTitlePane.setText("Grid");
        gridTitlePane.setContent(grid);
        
        // --- Accordion
        final Accordion accordion = new Accordion ();                
        for (int i = 0; i < imageNames.length; i++) {
            images[i] = new 
                Image(AquaFx.class.getResource("demo/images/"+imageNames[i] + ".png").toExternalForm());
            pics[i] = new ImageView(images[i]);
            tps[i] = new TitledPane(imageNames[i],pics[i]); 
        }   
        accordion.getPanes().addAll(tps);
        accordion.setExpandedPane(tps[0]);
        accordion.expandedPaneProperty().addListener(new 
            ChangeListener<TitledPane>() {
                public void changed(ObservableValue<? extends TitledPane> ov,
                    TitledPane old_val, TitledPane new_val) {
                        if (new_val != null) {
                            label.setText(accordion.getExpandedPane().getText() + 
                                ".jpg");
                        }
              }
        });
        
        HBox hbox = new HBox(10);
        hbox.setPadding(new Insets(20, 0, 0, 20));
        hbox.getChildren().setAll(gridTitlePane, accordion);
 
        Group root = (Group)scene.getRoot();
        root.getChildren().add(hbox);
        scene.setFill(Color.rgb(237, 237, 237));
        stage.setScene(scene);
        AquaFx.style();
        stage.show();
    }
}