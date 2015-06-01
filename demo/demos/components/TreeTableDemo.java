package demos.components;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.scenicview.ScenicView;

import com.cctintl.c3dfx.controls.C3DTreeTableView;

public class TreeTableDemo extends Application {


	final TreeItem<User> root = new TreeItem<>(new User("Sales Department", "23"));

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {

			root.getChildren().add(new TreeItem<>(new User("Sales Department1", "23")));
			TreeItem<User> root1 = new TreeItem<>(new User("Sales Department2", "24"));
			root.getChildren().add(root1);
			root1.getChildren().add(new TreeItem<>(new User("Sales Department3", "25")));

			
			root.getChildren().add(new TreeItem<>(new User("Sales Department4", "22")));
			TreeItem<User> root2 = new TreeItem<>(new User("Sales Department5", "20"));
			root.getChildren().add(root2);
			root2.getChildren().add(new TreeItem<>(new User("Sales Department6", "21")));
			

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



			C3DTreeTableView<User> treeView = new C3DTreeTableView<User>(root);
			treeView.setShowRoot(false);

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

	class User {
		String userName;
		String age;
		public User(String userName, String age) {
			this.userName = userName;
			this.age = age;
		}

	}
}