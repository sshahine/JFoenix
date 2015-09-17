package demos.components;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawer.DrawerDirection;
import com.jfoenix.controls.JFXDrawersStack;

public class DrawerDemo extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

		FlowPane content = new FlowPane();
		JFXButton leftButton = new JFXButton("LEFT");
		JFXButton topButton = new JFXButton("TOP");
		JFXButton rightButton = new JFXButton("RIGHT");
		JFXButton bottomButton = new JFXButton("BOTTOM");
		content.getChildren().addAll(leftButton, topButton, rightButton, bottomButton);
		content.setMaxSize(200, 200);
		
		
		JFXDrawer leftDrawer = new JFXDrawer();
		StackPane leftDrawerPane = new StackPane();
		leftDrawerPane.getStyleClass().add("red-400");
		leftDrawerPane.getChildren().add(new JFXButton("Left Content"));
		leftDrawer.setSidePane(leftDrawerPane);
		leftDrawer.setDefaultDrawerSize(250);
//		leftDrawer.setContent(content);	
		leftDrawer.setOverLayVisible(false);
		leftDrawer.setResizableOnDrag(true);
		
		
		JFXDrawer bottomDrawer = new JFXDrawer();
		StackPane bottomDrawerPane = new StackPane();
		bottomDrawerPane.getStyleClass().add("deep-purple-400");
		bottomDrawerPane.getChildren().add(new JFXButton("Bottom Content"));		
		bottomDrawer.setDirection(DrawerDirection.BOTTOM);		
		bottomDrawer.setDefaultDrawerSize(250);
		bottomDrawer.setSidePane(bottomDrawerPane);
//		bottomDrawer.setContent(leftDrawer);
		bottomDrawer.setOverLayVisible(false);
		bottomDrawer.setResizableOnDrag(true);
				
				
		JFXDrawer rightDrawer = new JFXDrawer();
		StackPane rightDrawerPane = new StackPane();
		rightDrawerPane.getStyleClass().add("blue-400");
		rightDrawerPane.getChildren().add(new JFXButton("Right Content"));
		rightDrawer.setDirection(DrawerDirection.RIGHT);		
		rightDrawer.setDefaultDrawerSize(250);
		rightDrawer.setSidePane(rightDrawerPane);
//		rightDrawer.setContent(bottomDrawer);
		rightDrawer.setOverLayVisible(false);
		rightDrawer.setResizableOnDrag(true);
		
		
		
		JFXDrawer topDrawer = new JFXDrawer();
		StackPane topDrawerPane = new StackPane();
		topDrawerPane.getStyleClass().add("green-400");
		topDrawerPane.getChildren().add(new JFXButton("Top Content"));
		topDrawer.setDirection(DrawerDirection.TOP);		
		topDrawer.setDefaultDrawerSize(250);
		topDrawer.setSidePane(topDrawerPane);
//		topDrawer.setContent(rightDrawer);
		topDrawer.setOverLayVisible(false);
		topDrawer.setResizableOnDrag(true);
		
		

		JFXDrawersStack drawersStack = new JFXDrawersStack();
		drawersStack.setContent(content);
		leftDrawer.setId("LEFT");
		rightDrawer.setId("RIGHT");
		bottomDrawer.setId("BOT");
		topDrawer.setId("TOP");
		drawersStack.addDrawer(leftDrawer);
		drawersStack.addDrawer(rightDrawer);
		drawersStack.addDrawer(bottomDrawer);
		drawersStack.addDrawer(topDrawer);
		
		leftButton.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
			drawersStack.toggle(leftDrawer);
		});
		bottomButton.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
			drawersStack.toggle(bottomDrawer);
		});		
		rightButton.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
			drawersStack.toggle(rightDrawer);
		});
		topButton.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
			drawersStack.toggle(topDrawer);
		});
		
		
		final Scene scene = new Scene(drawersStack, 800, 800);
		scene.getStylesheets().add(DrawerDemo.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());
		scene.getStylesheets().add(DrawerDemo.class.getResource("/resources/css/jfoenix-design.css").toExternalForm());

		primaryStage.setTitle("JFX Drawer Demo");
		primaryStage.setScene(scene);
		primaryStage.setResizable(true);
		primaryStage.show();		
		
	}

	public static void main(String[] args) { launch(args); }
	

}
