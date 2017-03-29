package demos.components;

import com.jfoenix.controls.JFXTreeView;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

import static java.util.Arrays.asList;

public class TreeViewDemo extends Application {

    private static final String SALES_DEPARTMENT = "Sales Department";
    private static final String IT_SUPPORT = "IT Support";
    private static final String ACCOUNTS_DEPARTMENT = "Accounts Department";

    private final List<Employee> employees = asList(new Employee("Ethan Williams", SALES_DEPARTMENT),
        new Employee("Emma Jones", SALES_DEPARTMENT),
        new Employee("Michael Brown", SALES_DEPARTMENT),
        new Employee("Anna Black", SALES_DEPARTMENT),
        new Employee("Rodger York", SALES_DEPARTMENT),
        new Employee("Susan Collins", SALES_DEPARTMENT),
        new Employee("Mike Graham", IT_SUPPORT),
        new Employee("Judy Mayer", IT_SUPPORT),
        new Employee("Gregory Smith", IT_SUPPORT),
        new Employee("Jacob Smith", ACCOUNTS_DEPARTMENT),
        new Employee("Isabella Johnson", ACCOUNTS_DEPARTMENT));

    private final TreeItem<String> rootNode = new TreeItem<>("MyCompany Human Resources");//, rootIcon);    // Set picture

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        rootNode.setExpanded(true);

        final JFXTreeView<String> treeView = new JFXTreeView<>(rootNode);
        for (Employee employee : employees) {
            TreeItem<String> empLeaf = new TreeItem<>(employee.getName());
            boolean found = false;
            for (TreeItem<String> depNode : rootNode.getChildren()) {
                if (depNode.getValue().contentEquals(employee.getDepartment())) {
                    depNode.getChildren().add(empLeaf);
                    found = true;
                    break;
                }
            }

            if (!found) {
                TreeItem<String> depNode = new TreeItem<>(employee.getDepartment());
                rootNode.getChildren().add(depNode);
                depNode.getChildren().add(empLeaf);
            }
        }

        stage.setTitle("Tree View Sample");
        VBox box = new VBox();
        final Scene scene = new Scene(box, 400, 300);
        scene.setFill(Color.LIGHTGRAY);

        box.getChildren().add(treeView);
        stage.setScene(scene);
        stage.show();

    }

    public static class Employee {

        private final SimpleStringProperty name;
        private final SimpleStringProperty department;

        private Employee(String name, String department) {
            this.name = new SimpleStringProperty(name);
            this.department = new SimpleStringProperty(department);
        }

        public String getName() {
            return name.get();
        }

        public void setName(String firstName) {
            name.set(firstName);
        }

        public String getDepartment() {
            return department.get();
        }

        public void setDepartment(String firstName) {
            department.set(firstName);
        }
    }
}
