package com.cctintl.c3dfx.demos.components;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.cctintl.c3dfx.controls.C3DHamburger;
import com.cctintl.c3dfx.transitions.hamburger.HamburgerBackArrowBasicTransition;
import com.cctintl.c3dfx.transitions.hamburger.HamburgerBasicCloseTransition;
import com.cctintl.c3dfx.transitions.hamburger.HamburgerNextArrowBasicTransition;
import com.cctintl.c3dfx.transitions.hamburger.HamburgerSlideCloseTransition;

public class HamburgerDemo extends Application {

	
	@Override public void start(Stage stage) {

		
		FlowPane main = new FlowPane();
		main.setVgap(20);
		main.setHgap(20);
		
		
		C3DHamburger h1 = new C3DHamburger();
		HamburgerSlideCloseTransition burgerTask = new HamburgerSlideCloseTransition(h1);
		h1.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
			burgerTask.setRate(burgerTask.getRate()*-1);
			burgerTask.play();
		});
		
		C3DHamburger h2 = new C3DHamburger();
		HamburgerBasicCloseTransition burgerTask1 = new HamburgerBasicCloseTransition(h2);
		h2.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
			burgerTask1.setRate(burgerTask1.getRate()*-1);
			burgerTask1.play();
		});
		
		C3DHamburger h3 = new C3DHamburger();
		HamburgerBackArrowBasicTransition burgerTask2 = new HamburgerBackArrowBasicTransition(h3);
		h3.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
			burgerTask2.setRate(burgerTask2.getRate()*-1);
			burgerTask2.play();
		});
		
		C3DHamburger h4 = new C3DHamburger();
		HamburgerNextArrowBasicTransition burgerTask3 = new HamburgerNextArrowBasicTransition(h4);
		h4.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
			burgerTask3.setRate(burgerTask3.getRate()*-1);
			burgerTask3.play();
		});
		
		
		main.getChildren().add(h1);
		main.getChildren().add(h2);
		main.getChildren().add(h3);
		main.getChildren().add(h4);
		

		StackPane pane = new StackPane();
		pane.getChildren().add(main);
		StackPane.setMargin(main, new Insets(60));
		pane.setStyle("-fx-background-color:WHITE");
		
		final Scene scene = new Scene(pane, 200, 200);
		scene.getStylesheets().add(HamburgerDemo.class.getResource("css/styles.css").toExternalForm());
		stage.setTitle("JavaFX Burgers :) ");
		stage.setScene(scene);
		stage.setResizable(false);

		stage.show();

	}

	public static void main(String[] args) { launch(args); }

}
