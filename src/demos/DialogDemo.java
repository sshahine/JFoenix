package demos;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import customui.components.C3DButton;
import customui.components.C3DDialog;
import customui.components.C3DDialog.C3DDialogAnimation;
import customui.components.C3DListView;

public class DialogDemo extends Application {


	
	@Override
	public void start(Stage stage) throws Exception {

		
		C3DListView<Label> list = new C3DListView<Label>();
		Label label = new Label("SSS1");
		label.setPadding(new Insets(5));
		Label label2 = new Label("SSS2");
		label2.setPadding(new Insets(5));
		Label label3 = new Label("SSS3");
		label3.setPadding(new Insets(5));
		Label label4 = new Label("SSS4");
		label4.setPadding(new Insets(5));
		Label label5 = new Label("SSS5");
		label5.setPadding(new Insets(5));
		Label label6 = new Label("SSS6");
		label6.setPadding(new Insets(5));
		
		list.getItems().add(label);
		list.getItems().add(label2);
		list.getItems().add(label3);
		list.getItems().add(label4);
		list.getItems().add(label5);
		list.getItems().add(label6);
		list.getStyleClass().add("mylistview");
		list.setPrefSize(200, 150);
		
		
		ListView<String> javaList = new ListView<String>();
		javaList.getItems().add("SSSS");
		javaList.getItems().add("SSSS1");
		javaList.getItems().add("SSSS2");
		javaList.getItems().add("SSSS3");
		javaList.getItems().add("SSSS4");
		javaList.getItems().add("SSSS5");
		javaList.getItems().add("SSSS6");
		javaList.setPrefSize(200, 150);
				
		HBox pane = new HBox();
		pane.setSpacing(30);
		pane.setStyle("-fx-background-color:WHITE");
		C3DButton button = new C3DButton("CENTER");
		button.setOnMouseClicked((e)-> new C3DDialog((Pane)stage.getScene().getRoot(), list, C3DDialogAnimation.CENTER).show());
		
		C3DButton button1 = new C3DButton("TOP");
		button1.setOnMouseClicked((e)-> new C3DDialog((Pane)stage.getScene().getRoot(), javaList, C3DDialogAnimation.TOP).show());
		
		C3DButton button2 = new C3DButton("BOTTOM");
		button2.setOnMouseClicked((e)-> new C3DDialog((Pane)stage.getScene().getRoot(), list, C3DDialogAnimation.BOTTOM).show());
		
		C3DButton button3 = new C3DButton("LEFT");
		button3.setOnMouseClicked((e)-> new C3DDialog((Pane)stage.getScene().getRoot(), list, C3DDialogAnimation.LEFT).show());
		
		C3DButton button4 = new C3DButton("RIGHT");
		button4.setOnMouseClicked((e)-> new C3DDialog((Pane)stage.getScene().getRoot(), list, C3DDialogAnimation.RIGHT).show());
		
		pane.getChildren().add(button);
		pane.getChildren().add(button1);
		pane.getChildren().add(button2);
		pane.getChildren().add(button3);
		pane.getChildren().add(button4);
		
				
		StackPane main = new StackPane();
		main.getChildren().add(pane);
		main.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		StackPane.setMargin(pane, new Insets(20,0,0,20));

		final Scene scene = new Scene(main, 600, 400, Color.WHITE);
		stage.setTitle("JavaFX Dialogs ;) ");
		scene.getStylesheets().add(InputDemo.class.getResource("css/styles.css").toExternalForm());
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}
	
	public static void main(String[] args) { launch(args); }






}
