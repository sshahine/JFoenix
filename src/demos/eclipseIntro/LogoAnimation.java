package demos.eclipseIntro;


import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class LogoAnimation extends Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Group g = FXMLLoader.load(LogoAnimation.class.getResource("eclipse.fxml"));
		
		Group text2 = (Group) g.lookup("#text_clipse");
		text2.setTranslateX(-220);
		
		Group logo = (Group) g.lookup("#logo");
		logo.setOpacity(0);
		
		TranslateTransition tt = new TranslateTransition(new Duration(3000), text2);
		tt.setFromX(-220);
		tt.setToX(0);
		
		FadeTransition ft = new FadeTransition(new Duration(2000), logo);
		ft.setFromValue(0);
		ft.setToValue(1.0);
		
		ScaleTransition st = new ScaleTransition(new Duration(2000),logo);
		st.setFromX(0);
		st.setToX(1);
		
		SequentialTransition t = new SequentialTransition(tt,new ParallelTransition(ft,st));
		t.setDelay(new Duration(2000));
		t.setAutoReverse(true);
		t.setCycleCount(Animation.INDEFINITE);
		t.play();
		
		
		
		StackPane p = new StackPane();
		p.getChildren().add(g);
		
		Scene s = new Scene(p);
		s.setFill(Color.TRANSPARENT);
		
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setWidth(657);
		primaryStage.setHeight(237);
		primaryStage.setScene(s);
		primaryStage.show();
	}

}
