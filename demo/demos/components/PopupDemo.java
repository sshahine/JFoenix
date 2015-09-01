package demos.components;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.cctintl.jfx.controls.JFXHamburger;
import com.cctintl.jfx.controls.JFXListView;
import com.cctintl.jfx.controls.JFXPopup;
import com.cctintl.jfx.controls.JFXRippler;
import com.cctintl.jfx.controls.JFXPopup.PopupHPosition;
import com.cctintl.jfx.controls.JFXPopup.PopupVPosition;
import com.cctintl.jfx.controls.JFXRippler.RipplerMask;
import com.cctintl.jfx.controls.JFXRippler.RipplerPos;

public class PopupDemo extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

		
		MenuItem item = new MenuItem("Java 1");
		MenuButton button = new MenuButton("Java Menu");
		button.getItems().setAll(item);

		JFXHamburger show = new JFXHamburger();
		show.setPadding(new Insets(10,5,10,5));
		JFXRippler r = new JFXRippler(show,RipplerMask.CIRCLE,RipplerPos.BACK);

		JFXListView<Label> list = new JFXListView<Label>();
		list.getItems().add(new Label("SSS"));
		list.getItems().add(new Label("SSS1"));
		list.getItems().add(new Label("SSS2"));
		list.getItems().add(new Label("SSS3"));
		list.getItems().add(new Label("SSS4"));
		list.getItems().add(new Label("SSS5"));
		list.getItems().add(new Label("SSS6"));
		list.getItems().add(new Label("SSS7"));
		
		AnchorPane container = new AnchorPane();
		container.getChildren().add(r);
		AnchorPane.setLeftAnchor(r, 200.0);
		AnchorPane.setTopAnchor(r, 210.0);
		
		StackPane main = new StackPane();
		main.getChildren().add(container);
		
		JFXPopup popup = new JFXPopup();
		popup.setContent(list);
		popup.setPopupContainer(main);
		popup.setSource(r);
		r.setOnMouseClicked((e)-> popup.show(PopupVPosition.TOP, PopupHPosition.LEFT));
		
		final Scene scene = new Scene(main, 800, 800);
		scene.getStylesheets().add(DrawerDemo.class.getResource("css/styles.css").toExternalForm());

		primaryStage.setTitle("JavaFX Drawer");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();		
	}

	public static void main(String[] args) { launch(args); }

}
