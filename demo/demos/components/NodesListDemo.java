package demos.components;


import java.util.ArrayList;

import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.cctintl.c3dfx.controls.C3DButton;
import com.cctintl.c3dfx.controls.C3DButton.ButtonType;
import com.cctintl.c3dfx.controls.C3DNodesList;
import com.cctintl.c3dfx.controls.C3DNodesListContainer;


public class NodesListDemo extends Application {


	@Override
	public void start(Stage primaryStage) throws Exception {
		try {

			C3DButton ssbutton1 = new C3DButton();			
			Label sslabel = new Label("ssB1");
			sslabel.setStyle("-fx-text-fill:WHITE");			
			ssbutton1.setGraphic(sslabel);			
			ssbutton1.setButtonType(ButtonType.RAISED);
			ssbutton1.getStyleClass().addAll("animated-option-button","animated-option-sub-button2");
			
			C3DButton ssbutton2 = new C3DButton("ssB2");
			ssbutton2.setTooltip(new Tooltip("Button 2"));
			ssbutton2.setButtonType(ButtonType.RAISED);
			ssbutton2.getStyleClass().addAll("animated-option-button","animated-option-sub-button2");
			
			C3DButton ssbutton3 = new C3DButton("ssB3");
			ssbutton3.setButtonType(ButtonType.RAISED);
			ssbutton3.getStyleClass().addAll("animated-option-button","animated-option-sub-button2");
			
			
			C3DNodesList nodesList3 = new C3DNodesList();
			nodesList3.setSpacing(10);
			// init nodes
			nodesList3.addAnimatedNode(ssbutton1, (expanded)->{ return new ArrayList<KeyValue>(){{ add(new KeyValue(sslabel.rotateProperty(), expanded? 360:0 , Interpolator.EASE_BOTH));}};});
			nodesList3.addAnimatedNode(ssbutton2);
			nodesList3.addAnimatedNode(ssbutton3);
			
			
			
			C3DButton sbutton1 = new C3DButton();			
			Label slabel = new Label("sB1");
			slabel.setStyle("-fx-text-fill:WHITE");			
			sbutton1.setGraphic(slabel);			
			sbutton1.setButtonType(ButtonType.RAISED);
			sbutton1.getStyleClass().addAll("animated-option-button","animated-option-sub-button");
			
			C3DButton sbutton2 = new C3DButton("sB2");
			sbutton2.setTooltip(new Tooltip("Button 2"));
			sbutton2.setButtonType(ButtonType.RAISED);
			sbutton2.getStyleClass().addAll("animated-option-button","animated-option-sub-button");
			
			C3DButton sbutton3 = new C3DButton("sB3");
			sbutton3.setButtonType(ButtonType.RAISED);
			sbutton3.getStyleClass().addAll("animated-option-button","animated-option-sub-button");
			
			
			C3DNodesList nodesList2 = new C3DNodesList();
			nodesList2.setSpacing(10);
			// init nodes
			nodesList2.addAnimatedNode(sbutton1, (expanded)->{ return new ArrayList<KeyValue>(){{ add(new KeyValue(slabel.rotateProperty(), expanded? 360:0 , Interpolator.EASE_BOTH));}};});
			nodesList2.addAnimatedNode(new C3DNodesListContainer(nodesList3));
			nodesList2.addAnimatedNode(sbutton2);
			nodesList2.addAnimatedNode(sbutton3);
			nodesList2.setRotate(90);
			
			
			C3DButton button1 = new C3DButton();			
			Label label = new Label("B1");
			button1.setGraphic(label);			
			label.setStyle("-fx-text-fill:WHITE");			
			button1.setButtonType(ButtonType.RAISED);
			button1.getStyleClass().add("animated-option-button");
			
			C3DButton button2 = new C3DButton("B2");
			button2.setTooltip(new Tooltip("Button 2"));
			button2.setButtonType(ButtonType.RAISED);
			button2.getStyleClass().add("animated-option-button");
			
			C3DButton button3 = new C3DButton("B3");
			button3.setButtonType(ButtonType.RAISED);
			button3.getStyleClass().add("animated-option-button");
			
			
			C3DNodesList nodesList = new C3DNodesList();
			nodesList.setSpacing(10);
			nodesList.addAnimatedNode(button1, (expanded)->{ return new ArrayList<KeyValue>(){{ add(new KeyValue(label.rotateProperty(), expanded? 360:0 , Interpolator.EASE_BOTH));}};});
			nodesList.addAnimatedNode(button2);
			nodesList.addAnimatedNode(new C3DNodesListContainer(nodesList2));
			nodesList.addAnimatedNode(button3);
			nodesList.setRotate(180);
			
			
			StackPane main = new StackPane();
			main.setPadding(new Insets(10));
			
			C3DButton e = new C3DButton("Test Mouse Events");
			e.setTranslateY(-50);
			e.setTranslateX(-100);
			main.getChildren().add(e);
			main.getChildren().add(nodesList);
			
			Scene scene = new Scene(main, 600, 600);
			scene.getStylesheets().add(InputDemo.class.getResource("css/styles.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

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

		public String toString(){
			return userName + " : " + age;
		}

	}
}