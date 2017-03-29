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

    private final List<Employee> employees = asList(new Employee("Ethan Williams", "Sales Department"),
        new Employee("Emma Jones", "Sales Department"),
        new Employee("Michael Brown", "Sales Department"),
        new Employee("Anna Black", "Sales Department"),
        new Employee("Rodger York", "Sales Department"),
        new Employee("Susan Collins", "Sales Department"),
        new Employee("Mike Graham", "IT Support"),
        new Employee("Judy Mayer", "IT Support"),
        new Employee("Gregory Smith", "IT Support"),
        new Employee("Jacob Smith", "Accounts Department"),
        new Employee("Isabella Johnson", "Accounts Department"));
    private final TreeItem<String> rootNode = new TreeItem<>("MyCompany Human Resources");//, rootIcon);    // Set picture

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        rootNode.setExpanded(true);

        JFXTreeView<String> treeView = new JFXTreeView<>(rootNode);
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
                TreeItem<String> depNode = new TreeItem<>(employee.getDepartment()
//,new ImageView(depIcon)   // Set picture
                );

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

        public void setName(String fName) {
            name.set(fName);
        }

        public String getDepartment() {
            return department.get();
        }

        public void setDepartment(String fName) {
            department.set(fName);
        }
    }
}
