package customui.components;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import com.fxexperience.javafx.animation.CachedTimelineTransition;

public class Drawer extends StackPane {

	private StackPane shadowedPane;
	private StackPane sidePane;
	private Node content;
	private Transition transition;

	public Drawer(Node content, double drawerWidth){
		super();

		shadowedPane = new StackPane();
		shadowedPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.1), CornerRadii.EMPTY, Insets.EMPTY)));
		shadowedPane.setOpacity(0);
		shadowedPane.setVisible(false);

		sidePane = new StackPane();
		sidePane.setMaxWidth(drawerWidth);
		sidePane.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 1), CornerRadii.EMPTY, Insets.EMPTY)));
		sidePane.setTranslateX(-1 * sidePane.maxWidthProperty().getValue());
		DepthManager.setDepth(sidePane, 2);

		this.content = content;		

		StackPane.setAlignment(sidePane, Pos.CENTER_LEFT);


		this.getChildren().add(this.content);		
		this.getChildren().add(shadowedPane);
		this.getChildren().add(sidePane);

		transition = new DrawerTransition();	
		shadowedPane.setOnMouseClicked((e) -> {
			transition.setRate(-1);
			transition.play();
		});		

	}

	public Transition getTransition(){
		return this.transition;
	}



	private class DrawerTransition extends CachedTimelineTransition{
		public DrawerTransition() {
			super(shadowedPane, new Timeline(
					new KeyFrame(
							Duration.ZERO,       
							new KeyValue(sidePane.translateXProperty(), -1 * sidePane.maxWidthProperty().getValue(),Interpolator.EASE_BOTH),
							new KeyValue(shadowedPane.visibleProperty(), false ,Interpolator.EASE_BOTH),
							new KeyValue(shadowedPane.opacityProperty(), 0,Interpolator.EASE_BOTH)
							),
							new KeyFrame(Duration.millis(100),
									new KeyValue(shadowedPane.visibleProperty(), true ,Interpolator.EASE_BOTH)
									),
									new KeyFrame(Duration.millis(600),
											new KeyValue(shadowedPane.opacityProperty(), 1,Interpolator.EASE_BOTH),
											new KeyValue(sidePane.translateXProperty(), 0 , Interpolator.EASE_BOTH)									
											)
					)
					);
			// reduce the number to increase the shifting , increase number to reduce shifting
			setCycleDuration(Duration.seconds(0.7));
			setDelay(Duration.seconds(0));
		}
	}


}

