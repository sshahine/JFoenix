package demos.components;

import javafx.application.Application;
import javafx.application.Platform;
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

import com.cctintl.c3dfx.controls.C3DTreeTableView;
import com.cctintl.c3dfx.controls.cells.editors.EditableTreeTableCell;
import com.cctintl.c3dfx.controls.datamodels.treetable.RecursiveTreeItem;
import com.cctintl.c3dfx.controls.datamodels.treetable.RecursiveTreeObject;

public class TreeTableDemo extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {

			TreeTableColumn<User, String> empColumn = 
					new TreeTableColumn<>("User");
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
					return new EditableTreeTableCell<User, String>();
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
			
			
			User rootUser = new User("Sales Department", "23");
			rootUser.getChildren().add(new User("Sales Department1", "23"));
			User user1 = new User("Sales Department2", "24");
			rootUser.getChildren().add(user1);
			user1.getChildren().add(new User("Sales Department3", "25"));
			rootUser.getChildren().add(new User("Sales Department4", "22"));
			User user2 = new User("Sales Department5", "20");
			rootUser.getChildren().add(user2);
			user2.getChildren().add(new User("Sales Department6", "21"));

			
			final TreeItem<User> root = new RecursiveTreeItem<User>(rootUser, User::getChildren);

			Platform.runLater(()->{
				user2.getChildren().add(new User("sdlfjaskldfjasdf","2323"));
			});
			
			
			C3DTreeTableView<User> treeView = new C3DTreeTableView<User>(root);
			treeView.setShowRoot(false);
			treeView.setEditable(true);
			treeView.getColumns().setAll(empColumn, ageColumn);

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
		
		public User(String userName, String age) {
			this.userName = userName;
			this.age = age;
		}
		
	}
	
	
}