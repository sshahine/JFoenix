package demos.components;

import com.jfoenix.animation.JFXNodesAnimation;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AnimationDemo extends Application {

	
	@Override public void start(Stage stage) {

		FlowPane main = new FlowPane();
		main.setVgap(20);
		main.setHgap(20);
		
		StackPane colorPane = new StackPane();
		colorPane.setStyle("-fx-background-radius:50; -fx-min-width:50; -fx-min-height:50;");
		colorPane.getStyleClass().add("red-500");
		main.getChildren().add(colorPane);
		
		StackPane colorPane1 = new StackPane();
		colorPane1.setStyle("-fx-background-radius:50; -fx-min-width:50; -fx-min-height:50;");
		colorPane1.getStyleClass().add("blue-500");
		
		StackPane placeHolder = new StackPane(colorPane1);
		placeHolder.setStyle("-fx-background-radius:50; -fx-min-width:50; -fx-min-height:50;");
		main.getChildren().add(placeHolder);
		
		
		StackPane colorPane2 = new StackPane();
		colorPane2.setStyle("-fx-background-radius:50; -fx-min-width:50; -fx-min-height:50;");
		colorPane2.getStyleClass().add("green-500");
		main.getChildren().add(colorPane2);
		
		StackPane colorPane3 = new StackPane();
		colorPane3.setStyle("-fx-background-radius:50; -fx-min-width:50; -fx-min-height:50;");
		colorPane3.getStyleClass().add("yellow-500");
		main.getChildren().add(colorPane3);
		
		
		StackPane colorPane4 = new StackPane();
		colorPane4.setStyle("-fx-background-radius:50; -fx-min-width:50; -fx-min-height:50;");
		colorPane4.getStyleClass().add("purple-500");
		main.getChildren().add(colorPane4);
		

		StackPane wizard = new StackPane();
		wizard.getChildren().add(main);
		StackPane.setMargin(main, new Insets(100));
		wizard.setStyle("-fx-background-color:WHITE");
		
		StackPane nextPage = new StackPane();
		
		StackPane newPlaceHolder = new StackPane();
		newPlaceHolder.setStyle("-fx-background-radius:50; -fx-max-width:50; -fx-max-height:50;");
		nextPage.getChildren().add(newPlaceHolder);
		StackPane.setAlignment(newPlaceHolder, Pos.TOP_LEFT);
		
		
		JFXHamburger h4 = new JFXHamburger();
		h4.setMaxSize(40, 40);
		HamburgerBackArrowBasicTransition burgerTask3 = new HamburgerBackArrowBasicTransition(h4);
		burgerTask3.setRate(-1);
		h4.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
			burgerTask3.setRate(burgerTask3.getRate()*-1);
			burgerTask3.play();
		});
		nextPage.getChildren().add(h4);
		StackPane.setAlignment(h4, Pos.TOP_LEFT);
		StackPane.setMargin(h4, new Insets(10));
		
		
		JFXNodesAnimation<FlowPane, StackPane> animation = new JFXNodesAnimation<FlowPane, StackPane>(main,nextPage) {
			
			private Pane tempPage = new Pane();
			
			double x = 0;
			double y = 0;
			
			@Override
			public void init() {
				nextPage.setOpacity(0);
				wizard.getChildren().add(tempPage);	
				wizard.getChildren().add(nextPage);
				
				x = colorPane1.localToScene(colorPane1.getBoundsInLocal()).getMinX();
				y = colorPane1.localToScene(colorPane1.getBoundsInLocal()).getMinY();
				
				tempPage.getChildren().add(colorPane1);
				colorPane1.setTranslateX(x);
				colorPane1.setTranslateY(y);
				
				
			}
			
			@Override
			public void end() {
				
			}
			
			@Override
			public Animation animateSharedNodes() {
				return new Timeline();
			}
			
			@Override
			public Animation animateExit() {
				return new Timeline(
						new KeyFrame(Duration.millis(300),
								new KeyValue(main.opacityProperty(), 0, Interpolator.EASE_BOTH)),					
						new KeyFrame(Duration.millis(520),
						new KeyValue(colorPane1.translateXProperty(), 0, Interpolator.EASE_BOTH),
						new KeyValue(colorPane1.translateYProperty(), 0, Interpolator.EASE_BOTH)),
						new KeyFrame(Duration.millis(200),
								new KeyValue(colorPane1.scaleXProperty(), 1, Interpolator.EASE_BOTH),
								new KeyValue(colorPane1.scaleYProperty(), 1, Interpolator.EASE_BOTH)),
						new KeyFrame(Duration.millis(1000),
								new KeyValue(colorPane1.scaleXProperty(), 40, Interpolator.EASE_BOTH),
								new KeyValue(colorPane1.scaleYProperty(), 40, Interpolator.EASE_BOTH)));
			}
			
			@Override
			public Animation animateEntrance() {
				return new Timeline(new KeyFrame(Duration.millis(320),new KeyValue(nextPage.opacityProperty(), 1, Interpolator.EASE_BOTH)));
			}
			
		};
		
		
		colorPane1.setOnMouseClicked((click)->{
			animation.animate();
		});

//		wizard.getChildren().add(nextPage);
		
		
		
		
		
		
		final Scene scene = new Scene(wizard, 800, 200);
		scene.getStylesheets().add(ButtonDemo.class.getResource("/css/jfoenix-design.css").toExternalForm());
		scene.getStylesheets().add(ButtonDemo.class.getResource("/css/jfoenix-components.css").toExternalForm());
		stage.setTitle("JFX Button Demo");
		stage.setScene(scene);
		stage.show();

	}

	public static void main(String[] args) { launch(args); }
	
}
