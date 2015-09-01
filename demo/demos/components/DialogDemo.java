package demos.components;

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

import com.cctintl.jfx.controls.JFXButton;
import com.cctintl.jfx.controls.JFXDialog;
import com.cctintl.jfx.controls.JFXListView;
import com.cctintl.jfx.controls.JFXDialog.DialogTransition;

public class DialogDemo extends Application {

	int counter = 0 ;
	
	@Override
	public void start(Stage stage) throws Exception {

		
		JFXListView<Label> list = new JFXListView<Label>();
		
		list.getItems().add(new Label("SSS"));
		list.getItems().add(new Label("SSS1"));
		list.getItems().add(new Label("SSS2"));
		list.getItems().add(new Label("SSS3"));
		list.getItems().add(new Label("SSS4"));
		list.getItems().add(new Label("SSS5"));
		list.getItems().add(new Label("SSS6"));
		list.getItems().add(new Label("SSS7"));
		list.getStyleClass().add("mylistview");
//		list.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, null)));
		
		
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
		JFXButton button = new JFXButton("CENTER");
		button.setOnMouseClicked((e)-> new JFXDialog((Pane)stage.getScene().getRoot(), list, DialogTransition.CENTER).show());
		
		JFXButton button1 = new JFXButton("TOP");
		button1.setOnMouseClicked((e)-> new JFXDialog((Pane)stage.getScene().getRoot(), javaList, DialogTransition.TOP).show());
		
		JFXButton button2 = new JFXButton("BOTTOM");
		button2.setOnMouseClicked((e)-> new JFXDialog((Pane)stage.getScene().getRoot(), list, DialogTransition.BOTTOM).show());
		
		JFXButton button3 = new JFXButton("LEFT");
		button3.setOnMouseClicked((e)-> new JFXDialog((Pane)stage.getScene().getRoot(), list, DialogTransition.LEFT).show());
		
		JFXButton button4 = new JFXButton("RIGHT");
		button4.setOnMouseClicked((e)-> new JFXDialog((Pane)stage.getScene().getRoot(), list, DialogTransition.RIGHT).show());
		
		
		JFXButton button3D = new JFXButton("3D");
		button3D.setOnMouseClicked((e)-> list.depthProperty().set(++counter%2));
		
		JFXButton buttonExpand = new JFXButton("EXPAND");
		buttonExpand.setOnMouseClicked((e)-> {list.depthProperty().set(1);list.setExpanded(true);});
		
		JFXButton buttonCollapse = new JFXButton("COLLAPSE");
		buttonCollapse.setOnMouseClicked((e)-> {list.depthProperty().set(1);list.setExpanded(false);});
		
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
