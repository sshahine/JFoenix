package demos.gui.uicomponents;

import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.IntegerTextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import io.datafx.controller.ViewController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellEditEvent;

import javax.annotation.PostConstruct;
import java.util.Random;

@ViewController(value = "/fxml/ui/TreeTableView.fxml", title = "Material Design Example")
public class TreeTableViewController {

    @FXML
    private
    JFXTreeTableView<Person> treeTableView;
    @FXML
    private
    JFXTreeTableView<Person> editableTreeTableView;
    @FXML
    private
    JFXTreeTableColumn<Person, String> firstNameColumn;
    @FXML
    private
    JFXTreeTableColumn<Person, String> lastNameColumn;
    @FXML
    private
    JFXTreeTableColumn<Person, Integer> ageColumn;
    @FXML
    private
    JFXTreeTableColumn<Person, String> firstNameEditableColumn;
    @FXML
    private
    JFXTreeTableColumn<Person, String> lastNameEditableColumn;
    @FXML
    private
    JFXTreeTableColumn<Person, Integer> ageEditableColumn;
    @FXML
    private
    Label treeTableViewCount;
    @FXML
    private
    JFXButton treeTableViewAdd;
    @FXML
    private
    JFXButton treeTableViewRemove;
    @FXML
    private
    Label editableTreeTableViewCount;
    @FXML
    private
    JFXTextField searchField;
    @FXML
    private
    JFXTextField searchField2;

    @PostConstruct
    public void init() {

        String[] names = {"Morley", "Scott", "Kruger", "Lain",
            "Kennedy", "Gawron", "Han", "Hall", "Aydogdu", "Grace",
            "Spiers", "Perera", "Smith", "Connoly",
            "Sokolowski", "Chaow", "James", "June"};
        Random random = new Random();


        firstNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Person, String> param) -> {
            if (firstNameColumn.validateValue(param)) {
                return param.getValue().getValue().firstName;
            } else {
                return firstNameColumn.getComputedValue(param);
            }
        });
        lastNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Person, String> param) -> {
            if (lastNameColumn.validateValue(param)) {
                return param.getValue().getValue().lastName;
            } else {
                return lastNameColumn.getComputedValue(param);
            }
        });
        ageColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Person, Integer> param) -> {
            if (ageColumn.validateValue(param)) {
                return param.getValue().getValue().age.asObject();
            } else {
                return ageColumn.getComputedValue(param);
            }
        });

        ObservableList<Person> people = FXCollections.observableArrayList();
        for (int i = 0; i < 100; i++) {
            people.add(new Person(names[random.nextInt(names.length)],
                names[random.nextInt(names.length)],
                random.nextInt(100)));
        }
        treeTableView.setRoot(new RecursiveTreeItem<>(people, RecursiveTreeObject::getChildren));

        treeTableView.setShowRoot(false);
        treeTableViewCount.textProperty()
            .bind(Bindings.createStringBinding(() -> "( " + treeTableView.getCurrentItemsCount() + " )",
                treeTableView.currentItemsCountProperty()));
        treeTableViewAdd.disableProperty()
            .bind(Bindings.notEqual(-1, treeTableView.getSelectionModel().selectedIndexProperty()));
        treeTableViewRemove.disableProperty()
            .bind(Bindings.equal(-1, treeTableView.getSelectionModel().selectedIndexProperty()));
        treeTableViewAdd.setOnMouseClicked((e) -> {
            people.add(new Person(names[random.nextInt(names.length)],
                names[random.nextInt(names.length)],
                random.nextInt(100)));
            treeTableView.currentItemsCountProperty().set(treeTableView.currentItemsCountProperty().get() + 1);
        });
        treeTableViewRemove.setOnMouseClicked((e) -> {
            people.remove(treeTableView.getSelectionModel().selectedItemProperty().get().getValue());
            treeTableView.currentItemsCountProperty().set(treeTableView.currentItemsCountProperty().get() - 1);
        });
        searchField.textProperty().addListener((o, oldVal, newVal) -> {
            treeTableView.setPredicate(person -> person.getValue().firstName.get()
                .contains(newVal) || person.getValue().lastName
                .get()
                .contains(newVal) || (person.getValue().age.get() + "").contains(newVal));
        });


        firstNameEditableColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Person, String> param) -> {
            if (firstNameEditableColumn.validateValue(param)) {
                return param.getValue().getValue().firstName;
            } else {
                return firstNameEditableColumn.getComputedValue(param);
            }
        });
        lastNameEditableColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Person, String> param) -> {
            if (lastNameEditableColumn.validateValue(param)) {
                return param.getValue().getValue().lastName;
            } else {
                return lastNameEditableColumn.getComputedValue(param);
            }
        });
        ageEditableColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Person, Integer> param) -> {
            if (ageEditableColumn.validateValue(param)) {
                return param.getValue().getValue().age.asObject();
            } else {
                return ageEditableColumn.getComputedValue(param);
            }
        });
        // add editors
        firstNameEditableColumn.setCellFactory((TreeTableColumn<Person, String> param) -> new GenericEditableTreeTableCell<>(
            new TextFieldEditorBuilder()));
        firstNameEditableColumn.setOnEditCommit((CellEditEvent<Person, String> t) -> t.getTreeTableView()
            .getTreeItem(t.getTreeTablePosition()
                .getRow())
            .getValue().firstName.set(t.getNewValue()));
        lastNameEditableColumn.setCellFactory((TreeTableColumn<Person, String> param) -> new GenericEditableTreeTableCell<>(
            new TextFieldEditorBuilder()));
        lastNameEditableColumn.setOnEditCommit((CellEditEvent<Person, String> t) -> t.getTreeTableView()
            .getTreeItem(t.getTreeTablePosition()
                .getRow())
            .getValue().lastName.set(t.getNewValue()));
        ageEditableColumn.setCellFactory((TreeTableColumn<Person, Integer> param) -> new GenericEditableTreeTableCell<>(
            new IntegerTextFieldEditorBuilder()));
        ageEditableColumn.setOnEditCommit((CellEditEvent<Person, Integer> t) -> t.getTreeTableView()
            .getTreeItem(t.getTreeTablePosition()
                .getRow())
            .getValue().age.set(t.getNewValue()));

        ObservableList<Person> people2 = FXCollections.observableArrayList();
        for (int i = 0; i < 200; i++) {
            people2.add(new Person(names[random.nextInt(names.length)],
                names[random.nextInt(names.length)],
                random.nextInt(100)));
        }
        editableTreeTableView.setRoot(new RecursiveTreeItem<>(people2, RecursiveTreeObject::getChildren));
        editableTreeTableView.setShowRoot(false);
        editableTreeTableView.setEditable(true);
        editableTreeTableViewCount.textProperty()
            .bind(Bindings.createStringBinding(() -> "( " + editableTreeTableView.getCurrentItemsCount() + " )",
                editableTreeTableView.currentItemsCountProperty()));
        searchField2.textProperty()
            .addListener((o, oldVal, newVal) -> editableTreeTableView.setPredicate(person -> person.getValue().firstName
                .get()
                .contains(newVal) || person.getValue().lastName
                .get()
                .contains(newVal) || (person.getValue().age.get() + "").contains(newVal)));
    }

    /*
     * data class
     */
    class Person extends RecursiveTreeObject<Person> {
        final StringProperty firstName;
        final StringProperty lastName;
        final IntegerProperty age;

        Person(String firstName, String lastName, int age) {
            this.firstName = new SimpleStringProperty(firstName);
            this.lastName = new SimpleStringProperty(lastName);
            this.age = new SimpleIntegerProperty(age);
        }
    }


}
