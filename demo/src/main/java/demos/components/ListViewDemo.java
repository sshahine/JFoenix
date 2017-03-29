package demos.components;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ListViewDemo extends Application {

    private static final String ITEM = "Item ";
    private int counter = 0;

    @Override
    public void start(Stage stage) throws Exception {

        JFXListView<Label> list = new JFXListView<>();
        for (int i = 0; i < 4; i++) {
            list.getItems().add(new Label(ITEM + i));
        }
        list.getStyleClass().add("mylistview");

        ListView<String> javaList = new ListView<>();
        for (int i = 0; i < 4; i++) {
            javaList.getItems().add(ITEM + i);
        }

        FlowPane pane = new FlowPane();
        pane.setStyle("-fx-background-color:WHITE");

        JFXButton button3D = new JFXButton("3D");
        button3D.setOnMouseClicked(e -> list.depthProperty().set(++counter % 2));

        JFXButton buttonExpand = new JFXButton("EXPAND");
        buttonExpand.setOnMouseClicked(e -> {
            list.depthProperty().set(1);
            list.setExpanded(true);
        });

        JFXButton buttonCollapse = new JFXButton("COLLAPSE");
        buttonCollapse.setOnMouseClicked(e -> {
            list.depthProperty().set(1);
            list.setExpanded(false);
        });

        pane.getChildren().add(button3D);
        pane.getChildren().add(buttonExpand);
        pane.getChildren().add(buttonCollapse);

        AnchorPane listsPane = new AnchorPane();
        listsPane.getChildren().add(list);
        AnchorPane.setLeftAnchor(list, 20.0);
        listsPane.getChildren().add(javaList);
        AnchorPane.setLeftAnchor(javaList, 300.0);

        VBox box = new VBox();
        box.getChildren().add(pane);
        box.getChildren().add(listsPane);
        box.setSpacing(40);

        StackPane main = new StackPane();
        main.getChildren().add(box);
        main.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        StackPane.setMargin(pane, new Insets(20, 0, 0, 20));

        final Scene scene = new Scene(main, 600, 600, Color.WHITE);
        stage.setTitle("JFX ListView Demo ");
        scene.getStylesheets().add(ListViewDemo.class.getResource("/css/jfoenix-components.css").toExternalForm());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
