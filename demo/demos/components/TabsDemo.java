package demos.components;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import com.cctintl.c3dfx.controls.C3DButton;
import com.cctintl.c3dfx.controls.C3DTabPane;

public class TabsDemo extends Application {

	private String msg = "Tab ";

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Tabs");
		Group root = new Group();
		Scene scene = new Scene(root, 400, 250);

		C3DTabPane tabPane = new C3DTabPane();

		Tab tab = new Tab();
		tab.setText(msg);
		tab.setContent(new Label("Tab"));

		tabPane.getTabs().add(tab);

		C3DButton button = new C3DButton("New Tab");
		button.setOnMouseClicked((o) -> {
			Tab temp = new Tab();
			int count = tabPane.getTabs().size();
			temp.setText(msg + count);
			tabPane.getTabs().add(temp);
		});

		//tabPane.prefWidthProperty().bind(scene.widthProperty());
		tabPane.setMaxWidth(scene.getWidth());
		scene.widthProperty().addListener((o, oldVal, newVal) -> {
			tabPane.setMaxWidth(newVal.doubleValue() - 100);
		});
		tabPane.prefHeightProperty().bind(scene.heightProperty());

		HBox hbox = new HBox();
		hbox.getChildren().addAll(button, tabPane);
		hbox.setSpacing(50);
		hbox.setAlignment(Pos.CENTER);

		root.getChildren().addAll(hbox);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
