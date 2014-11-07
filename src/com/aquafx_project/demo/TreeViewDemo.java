package com.aquafx_project.demo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import com.aquafx_project.AquaFx;

public class TreeViewDemo extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    final ObservableList<Person> data = FXCollections.observableArrayList(
            new Person("John", "Doe", "john.doe@foo.com", "jd@foo.com", true),
            new Person("Jane", "Doe", "jane.doe@example.com", "jane.d@foo.com", false));

    @Override public void start(Stage primaryStage) throws Exception {
        BorderPane pane = new BorderPane();

        HBox treeContainer = new HBox();
        treeContainer.setPadding(new Insets(10));
        TreeItem<String> rootItem = new TreeItem<String>("People");
        rootItem.setExpanded(true);
        for (Person person : data) {
            TreeItem<String> personLeaf = new TreeItem<String>(person.getFirstName());
            boolean found = false;
            for (TreeItem<String> statusNode : rootItem.getChildren()) {
                if (statusNode.getValue().equals((!person.getVip() ? "no " : "") + "VIP")) {
                    statusNode.getChildren().add(personLeaf);
                    found = true;
                    break;
                }
            }
            if (!found) {
                TreeItem<String> statusNode = new TreeItem<String>((!person.getVip() ? "no " : "") + "VIP");
                rootItem.getChildren().add(statusNode);
                statusNode.getChildren().add(personLeaf);
            }
        }
        TreeView<String> tree = new TreeView<String>(rootItem);
        tree.setPrefHeight(150);
        tree.setPrefWidth(150);
        treeContainer.getChildren().add(tree);

        pane.setCenter(treeContainer);
        pane.setStyle("-fx-background-color: white;");
        Scene scene = new Scene(pane);
        AquaFx.style();
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}