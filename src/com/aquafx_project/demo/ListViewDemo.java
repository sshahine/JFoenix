package com.aquafx_project.demo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import com.aquafx_project.AquaFx;

public class ListViewDemo extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    
    final ObservableList<Person> data = FXCollections.observableArrayList(
            new Person("John", "Doe", "john.doe@foo.com", "jd@foo.com", true),
            new Person("Jane", "Doe", "jane.doe@example.com", "jane.d@foo.com", false));

    @Override public void start(Stage primaryStage) throws Exception {
        BorderPane pane = new BorderPane();
        
        HBox listContainer = new HBox();
        listContainer.setSpacing(10);
        listContainer.setPadding(new Insets(10));
        ListView<String> list = new ListView<String>();
        ObservableList<String> listItems = FXCollections.observableArrayList("Item 1", "Item 2", "Item 3", "Item 4");
        list.setItems(listItems);
        list.setPrefWidth(150);
        list.setPrefHeight(70);
        listContainer.getChildren().add(list);
        TableView<Person> listTable = new TableView<Person>();
        listTable.getStyleClass().add("hide-header");
        listTable.setPrefHeight(250);
        listTable.setPrefWidth(150);
        TableColumn<Person, String> firstNameListCol = new TableColumn<Person, String>("First Name");
        firstNameListCol.setMinWidth(100);
        firstNameListCol.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));
        listTable.getColumns().add(firstNameListCol);
        listTable.setItems(data);
        listTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        listContainer.getChildren().add(listTable);
        ListView<String> horizontalList = new ListView<String>();
        horizontalList.setItems(listItems);
        horizontalList.setPrefWidth(250);
        horizontalList.setPrefHeight(50);
        horizontalList.setOrientation(Orientation.HORIZONTAL);
        listContainer.getChildren().add(horizontalList);
        
        pane.setCenter(listContainer);
        pane.setStyle("-fx-background-color: white;");
        Scene scene = new Scene(pane);
        AquaFx.style();
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}