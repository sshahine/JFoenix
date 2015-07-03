package demos.components;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.scenicview.ScenicView;

import com.cctintl.c3dfx.controls.C3DTreeTableView;
import com.cctintl.c3dfx.controls.cells.editors.TextFieldEditorBuilder;
import com.cctintl.c3dfx.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.cctintl.c3dfx.controls.datamodels.treetable.RecursiveTreeItem;
import com.cctintl.c3dfx.controls.datamodels.treetable.RecursiveTreeObject;

public class TreeTableDemo extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {

			TreeTableColumn<User, String> deptColumn = 
					new TreeTableColumn<>("Department");
			deptColumn.setPrefWidth(150);
			deptColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<User, String> param) -> 
			new ReadOnlyStringWrapper(param.getValue().getValue().department));
			
			TreeTableColumn<User, String> empColumn = 
					new TreeTableColumn<>("Employee");
			empColumn.setPrefWidth(150);
			empColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<User, String> param) -> 
			new ReadOnlyStringWrapper(param.getValue().getValue().userName));
			
			TreeTableColumn<User, String> ageColumn = 
					new TreeTableColumn<>("Age");
			ageColumn.setPrefWidth(150);
			ageColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<User, String> param) -> 
			new ReadOnlyStringWrapper(param.getValue().getValue().age));       

			ageColumn.setCellFactory(new Callback<TreeTableColumn<User,String>, TreeTableCell<User,String>>() {
				@Override
				public TreeTableCell<User, String> call(TreeTableColumn<User, String> param) {						
					return new GenericEditableTreeTableCell<User, String>(new TextFieldEditorBuilder());
				}
			});
			ageColumn.setOnEditCommit(
			    new EventHandler<CellEditEvent<User, String>>() {
			        @Override
			        public void handle(CellEditEvent<User, String> t) {			        	
			            ((User) t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue()).age = t.getNewValue();
			        }
			    }
			);
			
//			empColumn.setCellFactory(new Callback<TreeTableColumn<User,String>, TreeTableCell<User,String>>() {
//				@Override
//				public TreeTableCell<User, String> call(TreeTableColumn<User, String> param) {						
//					return new GenericEditableTreeTableCell<User, String>(new TextFieldEditorBuilder());
//				}
//			});
//			empColumn.setOnEditCommit(
//			    new EventHandler<CellEditEvent<User, String>>() {
//			        @Override
//			        public void handle(CellEditEvent<User, String> t) {			        	
//			            ((User) t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue()).userName = t.getNewValue();
//			        }
//			    }
//			);
//
//			deptColumn.setCellFactory(new Callback<TreeTableColumn<User,String>, TreeTableCell<User,String>>() {
//				@Override
//				public TreeTableCell<User, String> call(TreeTableColumn<User, String> param) {						
//					return new GenericEditableTreeTableCell<User, String>(new TextFieldEditorBuilder());
//				}
//			});
//			deptColumn.setOnEditCommit(
//			    new EventHandler<CellEditEvent<User, String>>() {
//			        @Override
//			        public void handle(CellEditEvent<User, String> t) {			        	
//			            ((User) t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue()).department = t.getNewValue();
//			        }
//			    }
//			);
			
			
			
			User rootUser = new User("Sales Department", "23","");
			rootUser.getChildren().add(new User("Computer Department", "",""));
			User user1 = new User("Sales Department", "","");
			rootUser.getChildren().add(user1);
			
			User user12 = new User("", "22","");
			user12.getChildren().add(new User("", "","Employee 1"));
			user12.getChildren().add(new User("", "","Employee 2"));
			
			User user13 = new User("", "25","");
			user13.getChildren().add(new User("", "","Employee 4"));
			user13.getChildren().add(new User("", "","Employee 5"));
			
			user1.getChildren().add(user12);
			user1.getChildren().add(user13);
			
			rootUser.getChildren().add(new User("IT Department", "",""));
			User user2 = new User("HR Department", "","");
			rootUser.getChildren().add(user2);
			User hr22 = new User("", "22","");
			user2.getChildren().add(hr22);
			hr22.getChildren().add(new User("", "","HR 1"));
			hr22.getChildren().add(new User("", "","HR 2"));

			
			final TreeItem<User> root = new RecursiveTreeItem<User>(rootUser, User::getChildren);

			
			C3DTreeTableView<User> treeView = new C3DTreeTableView<User>(root);
			treeView.setShowRoot(false);
			treeView.setEditable(true);
			treeView.getColumns().setAll(deptColumn, ageColumn, empColumn);

			StackPane main = new StackPane();
			main.setPadding(new Insets(10));
			main.getChildren().add(treeView);
			Scene scene = new Scene(main, 600, 400);
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
		String userName;
		String age;
		String department;
		
		public User(String department, String age, String userName) {
			this.department = department;
			this.userName = userName;
			this.age = age;
		}
		
	}
	
	
}