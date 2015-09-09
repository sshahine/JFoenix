package demos.components;

import java.math.BigInteger;
import java.security.SecureRandom;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;

public class TabsDemo extends Application {

	private String msg = "Tab 0";

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Tabs");
		Group root = new Group();
		Scene scene = new Scene(root, 700, 250);

		JFXTabPane tabPane = new JFXTabPane();

		Tab tab = new Tab();
		tab.setText(msg);
		tab.setContent(new Label("Tab 0"));

		tabPane.getTabs().add(tab);
		tabPane.setPrefSize(300, 200);
		Tab tab1 = new Tab();
		tab1.setText("Tab 01");
		tab1.setContent(new Label("Tab 01"));

		tabPane.getTabs().add(tab1);

		SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		selectionModel.select(1);

		JFXButton button = new JFXButton("New Tab");
		button.setOnMouseClicked((o) -> {
			Tab temp = new Tab();
			int count = tabPane.getTabs().size();
			temp.setText(msg + count);
			temp.setContent(new Label("Tab 0" + count));
			tabPane.getTabs().add(temp);
		});
		
		tabPane.setMaxWidth(500);

		HBox hbox = new HBox();
		hbox.getChildren().addAll(button, tabPane);
		hbox.setSpacing(50);
		hbox.setAlignment(Pos.CENTER);
		hbox.setStyle("-fx-padding:20");
		
		root.getChildren().addAll(hbox);
		scene.getStylesheets().add(TabsDemo.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setTitle("JFX Tabs Demo");
	}

	private SecureRandom random = new SecureRandom();

	public String nextSessionId() {
		return new BigInteger(50, random).toString(16);
	}
}
