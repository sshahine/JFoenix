package demos.components;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.cctintl.jfx.controls.JFXRippler;
import com.cctintl.jfx.controls.JFXRippler.RipplerPos;
import com.cctintl.jfx.effects.DepthManager;

import de.jensd.fx.fontawesome.Icon;

public class IconsDemo extends Application {

	public int i = 0;
	public int step = 1;
	
	@Override public void start(Stage stage) {

		FlowPane main = new FlowPane();
		main.setHgap(20);
		
//		Label l1 = GlyphsDude.createIconLabel(FontAwesomeIcon.HEART, "", "2em", "0", ContentDisplay.CENTER);
//		l1.getStyleClass().add("icon"); 
		
		Icon l1 = new Icon("HEART");
		l1.getStyleClass().add("icon");
//		l1.setBorder(new Border(new BorderStroke(Color.BLUE,BorderStrokeStyle.SOLID,new CornerRadii(10), new BorderWidths(1))));
		DepthManager.setDepth(l1, 1);
		
		JFXRippler r = new JFXRippler(l1,RipplerPos.BACK);
		r.getStyleClass().add("icons-rippler");
		main.getChildren().add(r);
		
		
		Icon l2 = new Icon("STAR");
		l2.getStyleClass().add("icon");
//		l1.setBorder(new Border(new BorderStroke(Color.BLUE,BorderStrokeStyle.SOLID,new CornerRadii(10), new BorderWidths(1))));
		DepthManager.setDepth(l2, 1);
		main.getChildren().add(new JFXRippler(l2));
		
				
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
