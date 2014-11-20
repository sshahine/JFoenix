package demos;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import customui.components.C3DButton;
import customui.components.C3DDialog;
import customui.components.C3DDialog.C3DDialogAnimation;
import customui.components.C3DListView;

public class DialogDemo extends Application {

	int counter = 0 ;
	
	@Override
	public void start(Stage stage) throws Exception {

		
		C3DListView<Label> list = new C3DListView<Label>();
		
		list.getItems().add(new Label("SSS"));
		list.getItems().add(new Label("SSS1"));
		list.getItems().add(new Label("SSS2"));
		list.getItems().add(new Label("SSS3"));
		list.getItems().add(new Label("SSS4"));
		list.getItems().add(new Label("SSS5"));
		list.getItems().add(new Label("SSS6"));
		list.getItems().add(new Label("SSS7"));
		list.getStyleClass().add("mylistview");
		// FIXME : we need to find the size of the list
		
		
		ListView<String> javaList = new ListView<String>();
		javaList.getItems().add("SSSS");
		javaList.getItems().add("SSSS1");
		javaList.getItems().add("SSSS2");
		javaList.getItems().add("SSSS3");
		javaList.getItems().add("SSSS4");
		javaList.getItems().add("SSSS5");
		javaList.getItems().add("SSSS6");
				
		FlowPane pane = new FlowPane();
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
		
		
		C3DButton button3D = new C3DButton("3D");
		button3D.setOnMouseClicked((e)-> list.depthProperty().set(++counter%2));
		
		C3DButton buttonExpand = new C3DButton("EXPAND");
		buttonExpand.setOnMouseClicked((e)-> {list.depthProperty().set(1);list.expand();});
		
		C3DButton buttonCollapse = new C3DButton("COLLAPSE");
		buttonCollapse.setOnMouseClicked((e)-> {list.depthProperty().set(1);list.collapse();});
		
		pane.getChildren().add(button);
		pane.getChildren().add(button1);
		pane.getChildren().add(button2);
		pane.getChildren().add(button3);
		pane.getChildren().add(button4);
		pane.getChildren().add(button3D);
		pane.getChildren().add(buttonExpand);
		pane.getChildren().add(buttonCollapse);
		
		
		AnchorPane listsPane = new AnchorPane();
		listsPane.getChildren().add(list);
		AnchorPane.setLeftAnchor(list, 20.0);
		listsPane.getChildren().add(javaList);
		AnchorPane.setLeftAnchor(javaList, 300.0);
		
		
		VBox box = new VBox();
		box.getChildren().add(pane);
		box.getChildren().add(listsPane);
		box.setSpacing(40);
		
		StackPane main = new StackPane();
		main.getChildren().add(box);
		main.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		StackPane.setMargin(pane, new Insets(20,0,0,20));

		final Scene scene = new Scene(main, 600, 800, Color.WHITE);
		stage.setTitle("JavaFX Dialogs ;) ");
		scene.getStylesheets().add(InputDemo.class.getResource("css/styles.css").toExternalForm());
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}
	
	public static void main(String[] args) { launch(args); }






}
