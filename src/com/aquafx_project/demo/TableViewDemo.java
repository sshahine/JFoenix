package com.aquafx_project.demo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import com.aquafx_project.AquaFx;

public class TableViewDemo extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    final ObservableList<Person> data = FXCollections.observableArrayList(
            new Person("John", "Doe", "john.doe@foo.com", "jd@foo.com", true),
            new Person("Jane", "Doe", "jane.doe@example.com", "jane.d@foo.com", false));

    @Override public void start(Stage primaryStage) throws Exception {
        BorderPane pane = new BorderPane();

        HBox tableContainer = new HBox();
        tableContainer.setPadding(new Insets(20));
        TableView<Person> table = new TableView<Person>();
        // table.setPrefHeight(100);
        // table.setPrefWidth(250);
        table.setEditable(true);
        // table.getSelectionModel().setCellSelectionEnabled(true) ;

        TableColumn<Person, String> firstNameCol = new TableColumn<Person, String>("First Name");
        // firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));
        TableColumn lastNameCol = new TableColumn("Last Name");
        lastNameCol.setEditable(true);
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameCol.setOnEditCommit(new EventHandler<CellEditEvent<Person, String>>() {
            @Override public void handle(CellEditEvent<Person, String> t) {
                ((Person) t.getTableView().getItems().get(t.getTablePosition().getRow())).setLastName(t.getNewValue());
            }
        });
        lastNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("lastName"));
        // TableColumn emailCol = new TableColumn("Email");
        TableColumn<Person, String> firstEmailCol = new TableColumn<Person, String>("Primary");
        firstEmailCol.setMinWidth(200);
        firstEmailCol.setCellValueFactory(new PropertyValueFactory<Person, String>("primaryEmail"));
        TableColumn<Person, String> secondEmailCol = new TableColumn<Person, String>("Secondary");
        secondEmailCol.setMinWidth(200);
        secondEmailCol.setCellValueFactory(new PropertyValueFactory<Person, String>("secondaryEmail"));
        // emailCol.getColumns().addAll(firstEmailCol, secondEmailCol);
        TableColumn<Person, Boolean> vipCol = new TableColumn<Person, Boolean>("VIP");
        vipCol.setEditable(true);
        vipCol.setCellFactory(CheckBoxTableCell.forTableColumn(vipCol));
        final Callback<TableColumn<Person, Boolean>, TableCell<Person, Boolean>> cellFactory = CheckBoxTableCell.forTableColumn(vipCol);

        vipCol.setCellFactory(new Callback<TableColumn<Person, Boolean>, TableCell<Person, Boolean>>() {
            @Override public TableCell<Person, Boolean> call(TableColumn<Person, Boolean> column) {
                TableCell<Person, Boolean> cell = cellFactory.call(column);
                return cell;
            }
        });
        vipCol.setOnEditCommit(new EventHandler<CellEditEvent<Person, Boolean>>() {
            @Override public void handle(CellEditEvent<Person, Boolean> t) {
                ((Person) t.getTableView().getItems().get(t.getTablePosition().getRow())).setVip(t.getNewValue());
            }
        });
        table.getColumns().addAll(firstNameCol, lastNameCol, firstEmailCol, secondEmailCol, vipCol);
        table.setItems(data);
        table.setTableMenuButtonVisible(false);
        tableContainer.getChildren().add(table);

        pane.setCenter(tableContainer);
        pane.setStyle("-fx-background-color: white;");
        Scene scene = new Scene(pane);
        AquaFx.style();
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}