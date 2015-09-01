package demos.components;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import com.cctintl.jfx.controls.JFXButton;
import com.cctintl.jfx.controls.JFXTextField;
import com.cctintl.jfx.controls.JFXTreeTableColumn;
import com.cctintl.jfx.controls.JFXTreeTableView;
import com.cctintl.jfx.controls.RecursiveTreeItem;
import com.cctintl.jfx.controls.cells.editors.TextFieldEditorBuilder;
import com.cctintl.jfx.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.cctintl.jfx.controls.datamodels.treetable.RecursiveTreeObject;


public class TreeTableDemo extends Application {


	@Override
	public void start(Stage primaryStage) throws Exception {
		try {

			JFXTreeTableColumn<User, String> deptColumn = new JFXTreeTableColumn<>("Department");
			deptColumn.setPrefWidth(150);
			deptColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<User, String> param) ->{
				if(deptColumn.validateValue(param)) return param.getValue().getValue().department;
				else return deptColumn.getComputedValue(param);
			});

			JFXTreeTableColumn<User, String> empColumn = new JFXTreeTableColumn<>("Employee");
			empColumn.setPrefWidth(150);
			empColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<User, String> param) ->{
				if(empColumn.validateValue(param)) return param.getValue().getValue().userName;
				else return empColumn.getComputedValue(param);
			});

			JFXTreeTableColumn<User, String> ageColumn = new JFXTreeTableColumn<>("Age");
			ageColumn.setPrefWidth(150);
			ageColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<User, String> param) ->{
				if(ageColumn.validateValue(param)) return param.getValue().getValue().age;
				else return ageColumn.getComputedValue(param);
			});       


			ageColumn.setCellFactory((TreeTableColumn<User, String> param) -> new GenericEditableTreeTableCell<User, String>(new TextFieldEditorBuilder()));
			ageColumn.setOnEditCommit((CellEditEvent<User, String> t)->{
				((User) t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue()).age.set(t.getNewValue());
			});

			empColumn.setCellFactory((TreeTableColumn<User, String> param) -> new GenericEditableTreeTableCell<User, String>(new TextFieldEditorBuilder()));
			empColumn.setOnEditCommit((CellEditEvent<User, String> t)->{
				((User) t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue()).userName.set(t.getNewValue());
			});

			deptColumn.setCellFactory((TreeTableColumn<User, String> param) -> new GenericEditableTreeTableCell<User, String>(new TextFieldEditorBuilder()));
			deptColumn.setOnEditCommit((CellEditEvent<User, String> t)->{
				((User) t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue()).department.set(t.getNewValue());
			});


			// data
			ObservableList<User> users = FXCollections.observableArrayList();
			users.add(new User("Computer Department", "23","CD 1"));
			users.add(new User("Sales Department", "22","Employee 1"));
			users.add(new User("Sales Department", "22","Employee 2"));
			users.add(new User("Sales Department", "25","Employee 4"));
			users.add(new User("Sales Department", "25","Employee 5"));
			users.add(new User("IT Department", "42","ID 2"));
			users.add(new User("HR Department", "22","HR 1"));
			users.add(new User("HR Department", "22","HR 2"));

			for(int i = 0 ; i< 40000; i++){
				users.add(new User("HR Department", i%10+"","HR 2" + i));	
			}
			for(int i = 0 ; i< 40000; i++){
				users.add(new User("Computer Department", i%20+"","CD 2" + i));	
			}

			for(int i = 0 ; i< 40000; i++){
				users.add(new User("IT Department", i%5+"","HR 2" + i));	
			}

			// build tree
			final TreeItem<User> root = new RecursiveTreeItem<User>(users, RecursiveTreeObject::getChildren);
			
			JFXTreeTableView<User> treeView = new JFXTreeTableView<User>(root, users);
			treeView.setShowRoot(false);
			treeView.setEditable(true);
			treeView.getColumns().setAll(deptColumn, ageColumn, empColumn);

			FlowPane main = new FlowPane();
			main.setPadding(new Insets(10));
			main.getChildren().add(treeView);


			JFXButton groupButton = new JFXButton("Group");
			groupButton.setOnAction((action)->{
				new Thread(()-> treeView.group(empColumn)).start();
			});
			main.getChildren().add(groupButton);

			JFXButton unGroupButton = new JFXButton("unGroup");
			unGroupButton.setOnAction((action)->treeView.unGroup());
			main.getChildren().add(unGroupButton);

			JFXTextField filterField = new JFXTextField();
			main.getChildren().add(filterField);

			Label size = new Label();

			filterField.textProperty().addListener((o,oldVal,newVal)->{
				treeView.setPredicate(user -> user.getValue().age.get().contains(newVal) || user.getValue().department.get().contains(newVal) || user.getValue().userName.get().contains(newVal));
			});

			size.textProperty().bind(Bindings.createStringBinding(()->treeView.getCurrentItemsCount()+"", treeView.currentItemsCountProperty()));
			main.getChildren().add(size);

			Scene scene = new Scene(main, 475, 500);
			scene.getStylesheets().add(InputDemo.class.getResource("css/styles.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();



			//			ScenicView.show(scene);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	class User extends RecursiveTreeObject<User>{
		
		StringProperty userName;
		StringProperty age;
		StringProperty department;

		public User(String department, String age, String userName) {
			this.department = new SimpleStringProperty(department) ;
			this.userName = new SimpleStringProperty(userName);
			this.age = new SimpleStringProperty(age);
		}

	}


}