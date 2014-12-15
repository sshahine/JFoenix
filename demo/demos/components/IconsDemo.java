package demos.components;

import com.cctintl.c3dfx.controls.C3DRippler;
import com.cctintl.c3dfx.controls.DepthManager;
import com.cctintl.c3dfx.controls.C3DRippler.RipplerPos;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import de.jensd.fx.fontawesome.Icon;

public class IconsDemo extends Application {

	public int i = 0;
	public int step = 1;
	
	@Override public void start(Stage stage) {

		FlowPane main = new FlowPane();
		main.setHgap(20);
		
		
		Icon l1 = new Icon("HEART");
		l1.getStyleClass().add("icon");
//		l1.setBorder(new Border(new BorderStroke(Color.BLUE,BorderStrokeStyle.SOLID,new CornerRadii(10), new BorderWidths(1))));
		DepthManager.setDepth(l1, 1);
		
		C3DRippler r = new C3DRippler(l1,RipplerPos.BACK);
		r.getStyleClass().add("icons-rippler");
		main.getChildren().add(r);
		
		
		Icon l2 = new Icon("STAR");
		l2.getStyleClass().add("icon");
//		l1.setBorder(new Border(new BorderStroke(Color.BLUE,BorderStrokeStyle.SOLID,new CornerRadii(10), new BorderWidths(1))));
		DepthManager.setDepth(l2, 1);
		main.getChildren().add(new C3DRippler(l2));
		
				
		StackPane pane = new StackPane();
		pane.getChildren().add(main);
		StackPane.setMargin(main, new Insets(100));
		pane.setStyle("-fx-background-color:WHITE");
		
		final Scene scene = new Scene(pane, 600, 400);
		scene.getStylesheets().add(InputDemo.class.getResource("css/styles.css").toExternalForm());
		stage.setTitle("JavaFX Ripple effect and shadows ");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();

	}

	public static void main(String[] args) { launch(args); }
}
