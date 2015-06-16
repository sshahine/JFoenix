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


public class NodesListDemo extends Application {


	@Override
	public void start(Stage primaryStage) throws Exception {
		try {

			C3DButton button1 = new C3DButton();
			Label label = new Label("B1");
			button1.setGraphic(label);
			label.setStyle("-fx-text-fill:WHITE");
			button1.setButtonType(ButtonType.RAISED);
			button1.setStyle("-fx-pref-width:50px;-fx-background-color:#44B449; -fx-background-radius:50px; -fx-pref-height:50px;-fx-text-fill:white");
			C3DButton button2 = new C3DButton("B2");
			button2.setTooltip(new Tooltip("Button 2"));
			button2.setButtonType(ButtonType.RAISED);
			button2.setStyle("-fx-pref-width:50px;-fx-background-color:#44B449; -fx-background-radius:50px; -fx-pref-height:50px;-fx-text-fill:white");
			C3DButton button3 = new C3DButton("B3");
			button3.setButtonType(ButtonType.RAISED);
			button3.setStyle("-fx-pref-width:50px;-fx-background-color:#44B449; -fx-background-radius:50px; -fx-pref-height:50px;-fx-text-fill:white");
			
			button2.setScaleX(0);
			button3.setScaleX(0);
			button2.setScaleY(0);
			button3.setScaleY(0);

			C3DNodesList nodesList = new C3DNodesList();
			
			nodesList.setMaxHeight(200);
			nodesList.setMaxWidth(100);
			
			nodesList.setSpacing(10);
			nodesList.addAnimatedNode(button1, (expanded)->{
				return new ArrayList<KeyValue>(){{
					add(new KeyValue(label.rotateProperty(), expanded? 360:0 , Interpolator.EASE_BOTH));
				}};
			});
			nodesList.addAnimatedNode(button2, (expanded)->{
				return new ArrayList<KeyValue>(){{
					add(new KeyValue(button2.scaleXProperty(), expanded?1:0 , Interpolator.EASE_BOTH));
					add(new KeyValue(button2.scaleYProperty(), expanded?1:0, Interpolator.EASE_BOTH));
				}};
			});
			nodesList.addAnimatedNode(button3, (expanded)->{
				return new ArrayList<KeyValue>(){{
					add(new KeyValue(button3.scaleXProperty(), expanded?1:0 , Interpolator.EASE_BOTH));
					add(new KeyValue(button3.scaleYProperty(), expanded?1:0, Interpolator.EASE_BOTH));
				}};
			});
			button1.setOnAction((action)->{
				nodesList.animateList();
			});

			nodesList.setRotate(180);
			StackPane main = new StackPane();

			main.setPadding(new Insets(10));
			main.getChildren().add(nodesList);
			Scene scene = new Scene(main, 600, 400);
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