package com.aquafx_project.demo;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.xml.bind.annotation.XmlElement;

import com.aquafx_project.AquaFx;



public class CheckBoxCellSample extends Application {

    private final static ObservableList<TestPerson> data;

    static {
        data = TestPerson.getTestList();
    }

    public static void main(String[] args) {
        Application.launch(CheckBoxCellSample.class, args);
    }

    @Override public void start(Stage stage) {
        stage.setTitle("CheckBoxCell Samples");
        final Scene scene = new Scene(new Group(), 875, 700);
        scene.setFill(Color.LIGHTGRAY);
        Group root = (Group) scene.getRoot();

        root.getChildren().add(getContent(scene));
        AquaFx.style();
        stage.setScene(scene);
        stage.show();
    }

    public Node getContent(Scene scene) {
        // TabPane
        final TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setPrefWidth(scene.getWidth());
        tabPane.setPrefHeight(scene.getHeight());

        tabPane.prefWidthProperty().bind(scene.widthProperty());
        tabPane.prefHeightProperty().bind(scene.heightProperty());

        // list view examples
        Tab listViewTab = new Tab("ListView");
        buildListViewTab(listViewTab);
        tabPane.getTabs().add(listViewTab);

        // tree view examples
        Tab treeViewTab = new Tab("TreeView");
        buildTreeViewTab(treeViewTab);
        tabPane.getTabs().add(treeViewTab);

        // table view examples
        Tab tableViewTab = new Tab("TableView");
        buildTableViewTab(tableViewTab);
        tabPane.getTabs().add(tableViewTab);

        return tabPane;
    }

    private void buildListViewTab(Tab tab) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.setHgap(5);
        grid.setVgap(5);

        // create the listview
        final ListView<TestPerson> listView = new ListView<TestPerson>();
        listView.setItems(data);

        // set the cell factory
        Callback<TestPerson, ObservableValue<Boolean>> getProperty = new Callback<TestPerson, ObservableValue<Boolean>>() {
            @Override public BooleanProperty call(TestPerson person) {
                // given a person, we return the property that represents
                // whether or not they are invited. We can then bind to this
                // bidirectionally.
                return person.telecommuterProperty();
            }
        };
        listView.setCellFactory(CheckBoxListCell.forListView(getProperty));

        grid.add(listView, 0, 0);
        GridPane.setVgrow(listView, Priority.ALWAYS);
        GridPane.setHgrow(listView, Priority.ALWAYS);
        tab.setContent(grid);
    }

    private void buildTreeViewTab(Tab tab) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.setHgap(5);
        grid.setVgap(5);

        // --- TreeView 1
        // create the tree model
        TreeItem<String> root = TreeModels.getFamiliesTree();
        root.setExpanded(true);

        // create the treeView
        TreeView<String> treeView = new TreeView<String>();
        treeView.setRoot(root);

        // set the cell factory
        treeView.setCellFactory(CheckBoxTreeCell.<String> forTreeView());

        grid.add(treeView, 0, 0);
        GridPane.setVgrow(treeView, Priority.ALWAYS);
        GridPane.setHgrow(treeView, Priority.ALWAYS);

        // --- TreeView 2
        // create the tree model
        TreeItem<String> root1 = TreeModels.getFamiliesTree();
        root1.setExpanded(true);

        // update tree model to be independent
        setIndependent(root1);

        // create the treeView
        TreeView<String> treeView1 = new TreeView<String>();
        treeView1.setRoot(root1);

        // set the cell factory
        treeView1.setCellFactory(CheckBoxTreeCell.<String> forTreeView());

        grid.add(treeView1, 1, 0);
        GridPane.setVgrow(treeView1, Priority.ALWAYS);
        GridPane.setHgrow(treeView1, Priority.ALWAYS);

        tab.setContent(grid);
    }

    private void setIndependent(TreeItem<?> item) {
        if (item == null || !(item instanceof CheckBoxTreeItem))
            return;

        CheckBoxTreeItem checkItem = (CheckBoxTreeItem) item;
        checkItem.setIndependent(true);
        for (TreeItem child : item.getChildren()) {
            setIndependent(child);
        }
    }

    private void buildTableViewTab(Tab tab) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.setHgap(5);
        grid.setVgap(5);

        // create the tableview
        TableColumn<TestPerson, Boolean> invitedColumn = new TableColumn<TestPerson, Boolean>("Invited");

        invitedColumn.setCellValueFactory(new PropertyValueFactory<TestPerson, Boolean>("invited"));

        TableColumn<TestPerson, String> nameColumn = new TableColumn<TestPerson, String>("First Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<TestPerson, String>("firstName"));

        TableView<TestPerson> tableView = new TableView<TestPerson>(data);
        tableView.getColumns().setAll(invitedColumn, nameColumn);

        // set the cell factory in the invited TableColumn
        invitedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(invitedColumn));

        grid.add(tableView, 0, 0);
        GridPane.setVgrow(tableView, Priority.ALWAYS);
        GridPane.setHgrow(tableView, Priority.ALWAYS);
        tab.setContent(grid);
    }

    private static class TestPerson {

        private static ObservableList<TestPerson> testList;

        private static long userIDCounter = 1;

        static ObservableList<TestPerson> getTestList() {
            return testList;
        }

        static {
            testList = FXCollections.observableArrayList(new TestPerson("Jenny", "Bond", false, 23.64),
                    new TestPerson("Billy", "James", true, -12.11), new TestPerson("Timmy", "Gordon", false, 45),
                    new TestPerson("Aiden", "Simpson", false, 0), new TestPerson("Jacob", "Grant", true, -92.21),
                    new TestPerson("Jackson", "Matthews", false, 0), new TestPerson("Ethan", "Beck", false, 48.12),
                    new TestPerson("Sophia", "Potts", true, 38.22), new TestPerson("Isabella", "Bair", false, 823.43),
                    new TestPerson("Olivia", "Fowler", false, -201.23), new TestPerson("Jayden", "Walker", true, 49.54),
                    new TestPerson("Emma", "Wong", false, -3.49), new TestPerson("Chloe", "Samuelsson", false, 0.76),
                    new TestPerson("Logan", "Grieve", true, 49.22), new TestPerson("Caden", "Sato", false, 90.56),
                    new TestPerson("Lilly", "Chin", false, -0.06), new TestPerson("Madison", "Barbashov", true, 89.76),
                    new TestPerson("Ryan", "Beatty", false, 123.50), new TestPerson("Hailey", "Giles", false, 90.56),
                    new TestPerson("Molly", "Vos", true, -87.12), new TestPerson("Nolan", "Antonio", false, 992.12),
                    new TestPerson("Bryce", "Marinacci", false, 1832.29), new TestPerson("Maria", "Mayhew", true, -782.12),
                    new TestPerson("Lauren", "Holt", false, 291.21));
        }

        private long userID;

        long getUserID() {
            return userID;
        }

        @XmlElement(name = "userId") public void setUserId(long v) {
            this.userID = v;
        }

        private StringProperty firstNameProperty;

        String getFirstName() {
            return firstNameProperty.get();
        }

        private BooleanProperty telecommuterProperty;

        BooleanProperty telecommuterProperty() {
            return telecommuterProperty;
        }

        TestPerson(String firstName, String lastName, boolean telecommuter, double balance) {
            this.userID = userIDCounter++;
            this.firstNameProperty = new SimpleStringProperty(this, "firstName", firstName);
            this.telecommuterProperty = new SimpleBooleanProperty(this, "telecommuter", telecommuter);
        }

        @Override public String toString() {
            return getFirstName();
        }

        @Override public int hashCode() {
            int hash = 7;
            hash = 89 * hash + (int) (this.userID ^ (this.userID >>> 32));
            return hash;
        }

        @Override public boolean equals(Object o) {
            boolean answer = false;
            if (o instanceof TestPerson) {
                TestPerson target = (TestPerson) o;
                answer = target.getUserID() == userID;
            }
            return answer;
        }
    }

    static class TreeModels {

        static TreeItem<String> getCorporateTree() {
            CheckBoxTreeItem<String> management = new CheckBoxTreeItem<String>("Management");
            CheckBoxTreeItem<String> directors = new CheckBoxTreeItem<String>("Directors");
            CheckBoxTreeItem<String> finance = new CheckBoxTreeItem<String>("Finance");
            management.getChildren().addAll(directors, finance);
            management.setExpanded(true);

            CheckBoxTreeItem<String> sales = new CheckBoxTreeItem<String>("Sales");
            CheckBoxTreeItem<String> marketing = new CheckBoxTreeItem<String>("Marketing");
            CheckBoxTreeItem<String> assistants = new CheckBoxTreeItem<String>("Assistants");
            sales.getChildren().addAll(marketing, assistants);
            sales.setExpanded(true);

            CheckBoxTreeItem<String> engineering = new CheckBoxTreeItem<String>("Engineering");
            CheckBoxTreeItem<String> software = new CheckBoxTreeItem<String>("Software");
            CheckBoxTreeItem<String> hardware = new CheckBoxTreeItem<String>("Hardware");
            CheckBoxTreeItem<String> rAndD = new CheckBoxTreeItem<String>("R & D");
            engineering.getChildren().addAll(software, hardware, rAndD);
            engineering.setExpanded(true);

            CheckBoxTreeItem<String> legal = new CheckBoxTreeItem<String>("Legal");

            final CheckBoxTreeItem<String> root = new CheckBoxTreeItem<String>("EnergyCo");
            root.getChildren().addAll(management, sales, engineering, legal);
            root.setSelected(true);

            return root;
        }

        static TreeItem<String> getFamiliesTree() {
            // Brady bunch
            CheckBoxTreeItem<String> marciaBrady = new CheckBoxTreeItem<String>("Marcia");
            CheckBoxTreeItem<String> carolBrady = new CheckBoxTreeItem<String>("Carol");
            CheckBoxTreeItem<String> gregBrady = new CheckBoxTreeItem<String>("Greg");
            CheckBoxTreeItem<String> peterBrady = new CheckBoxTreeItem<String>("Peter");
            CheckBoxTreeItem<String> bobbyBrady = new CheckBoxTreeItem<String>("Bobby");
            CheckBoxTreeItem<String> mikeBrady = new CheckBoxTreeItem<String>("Mike");
            CheckBoxTreeItem<String> cindyBrady = new CheckBoxTreeItem<String>("Cindy");
            CheckBoxTreeItem<String> janBrady = new CheckBoxTreeItem<String>("Jan");

            CheckBoxTreeItem<String> bradyFamily = new CheckBoxTreeItem<String>("Brady Family");
            bradyFamily.getChildren().addAll(marciaBrady, carolBrady, gregBrady, peterBrady, bobbyBrady, mikeBrady, cindyBrady,
                    janBrady);

            // Giles family
            CheckBoxTreeItem<String> jonathanGiles = new CheckBoxTreeItem<String>("Jonathan");
            CheckBoxTreeItem<String> juliaGiles = new CheckBoxTreeItem<String>("Julia");
            CheckBoxTreeItem<String> mattGiles = new CheckBoxTreeItem<String>("Matt");
            CheckBoxTreeItem<String> sueGiles = new CheckBoxTreeItem<String>("Sue");
            CheckBoxTreeItem<String> ianGiles = new CheckBoxTreeItem<String>("Ian");

            CheckBoxTreeItem<String> gilesFamily = new CheckBoxTreeItem<String>("Giles Family");
            gilesFamily.getChildren().addAll(jonathanGiles, juliaGiles, mattGiles, sueGiles, ianGiles);

            final CheckBoxTreeItem<String> root = new CheckBoxTreeItem<String>("Families");
            root.getChildren().addAll(gilesFamily, bradyFamily);

            return root;
        }
    }

}
