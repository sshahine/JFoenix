package com.cctintl.c3dfx.demos.components;

import com.cctintl.c3dfx.controls.C3DHamburger;
import com.cctintl.c3dfx.controls.C3DPopupOLD;
import com.cctintl.c3dfx.controls.C3DRippler;
import com.cctintl.c3dfx.controls.C3DRippler.RipplerMask;
import com.cctintl.c3dfx.controls.C3DRippler.RipplerPos;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class PopupDemoOLD extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

		
		MenuItem item = new MenuItem("Java 1");
		MenuButton button = new MenuButton("Java Menu");
		button.getItems().setAll(item);

		C3DHamburger show = new C3DHamburger();
		show.setPadding(new Insets(10,5,10,5));
		C3DRippler r = new C3DRippler(show,RipplerMask.CIRCLE,RipplerPos.BACK);
		C3DPopupOLD menu = new C3DPopupOLD(r,primaryStage);
		Label l1 = new Label("TEST1");
		l1.setPadding(new Insets(10,30,10,30));
		l1.setMinSize(0, 0);
		Label l2 = new Label("TEST2");
		l2.setPadding(new Insets(10,30,10,30));
		l2.setMinSize(0, 0);
		Label l3 = new Label("TEST3");
		l3.setPadding(new Insets(10,30,10,30));
		l3.setMinSize(0, 0);
		menu.addMenuItem(l1);
		menu.addMenuItem(l2);
		menu.addMenuItem(l3);


		HBox pane = new HBox(100);
		pane.setStyle("-fx-background-color:WHITE");
		pane.getChildren().add(button);
		pane.getChildren().add(r);

		AnchorPane main = new AnchorPane();
		main.getChildren().add(pane);
		main.setStyle("-fx-background-color:WHITE");
		AnchorPane.setTopAnchor(pane, 100.0);
		AnchorPane.setLeftAnchor(pane, 100.0);

		final Scene scene = new Scene(main, 800, 800);
		scene.getStylesheets().add(DrawerDemo.class.getResource("css/styles.css").toExternalForm());

		primaryStage.setTitle("JavaFX Drawer");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();		
	}

	public static void main(String[] args) { launch(args); }

}
