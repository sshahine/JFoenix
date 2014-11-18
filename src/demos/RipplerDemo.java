package demos;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import customui.components.DepthManager;
import customui.components.C3DRippler;

public class RipplerDemo extends Application {

	public int i = 0;
	public int step = 1;
	
	@Override public void start(Stage stage) {

		//TODO drop shadow changes the width and hegith thus need to be considered
		
		FlowPane main = new FlowPane();
		main.setVgap(20);
		main.setHgap(20);
		
		Label l = new Label("Click Me");
		l.setStyle("-fx-background-color:WHITE;");
		l.setPadding(new Insets(20));
		C3DRippler lrippler = new C3DRippler(l);
		lrippler.setEnabled(false);
		main.getChildren().add(lrippler);
		
		lrippler.setOnMousePressed((e) -> {
			if(i == 5) step = -1;	
			else if (i == 0) step = 1;
			DepthManager.setDepth(l, i+=step % DepthManager.getLevels());
		});
		
		Label l1 = new Label("TEST");
		l1.setStyle("-fx-background-color:WHITE;");
		DepthManager.setDepth(l1, 1);
		l1.setPadding(new Insets(20));
		main.getChildren().add(new C3DRippler(l1));
		
		Label l2 = new Label("TEST1");
		DepthManager.setDepth(l2, 2);
		l2.setStyle("-fx-background-color:WHITE;");
		l2.setPadding(new Insets(20));
		main.getChildren().add(new C3DRippler(l2));
		
		Label l3 = new Label("TEST2");
		DepthManager.setDepth(l3, 3);
		l3.setStyle("-fx-background-color:WHITE;");
		l3.setPadding(new Insets(20));
		main.getChildren().add(new C3DRippler(l3));
		
		
		Label l4 = new Label("TEST3");
		DepthManager.setDepth(l4, 4);
		l4.setStyle("-fx-background-color:WHITE;");
		l4.setPadding(new Insets(20));
		main.getChildren().add(new C3DRippler(l4));
		
		Label l5 = new Label("TEST4");
		DepthManager.setDepth(l5, 5);
		l5.setStyle("-fx-background-color:WHITE;");
		l5.setPadding(new Insets(20));
		main.getChildren().add(new C3DRippler(l5));
		

		StackPane pane = new StackPane();
		pane.getChildren().add(main);
		StackPane.setMargin(main, new Insets(100));
		pane.setStyle("-fx-background-color:WHITE");
		
		final Scene scene = new Scene(pane, 600, 400);

		stage.setTitle("JavaFX Ripple effect and shadows ");
		stage.setScene(scene);
		stage.setResizable(false);

		stage.show();

	}

	public static void main(String[] args) { launch(args); }
}
