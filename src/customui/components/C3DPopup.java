package customui.components;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

import com.fxexperience.javafx.animation.CachedTimelineTransition;


public class C3DPopup extends Popup {

	private VBox items = new VBox();
	private AnchorPane itemsContainer;
	private StackPane content;

	private Transition transition ;
	private Scale scaleTransform = new Scale(0,0,20,20);
	private double xLocation = 0;
	private double yLocation = 0;

	private double startScale = 0.2;

	public C3DPopup(Node source, Window window) {
		super();

		// create components
		itemsContainer = new AnchorPane();
		itemsContainer.getChildren().add(items);				

		content = new StackPane();
		content.getChildren().add(itemsContainer);
		content.getTransforms().add(scaleTransform);
		content.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5), null)));		
		DepthManager.setDepth(content, 2);
		
		this.getContent().setAll(content);
		setAutoHide(true);
		
		
		// add listeners
		source.setOnMouseClicked((e)->{
			DropShadow shadowEffect = (DropShadow)content.getEffect();
			Bounds location = source.localToScreen(source.boundsInLocalProperty().get());
			xLocation = location.getMinX() -shadowEffect.radiusProperty().get() + shadowEffect.offsetXProperty().get();
			yLocation = location.getMinY() -shadowEffect.radiusProperty().get() + shadowEffect.offsetYProperty().get();			
			this.setX(xLocation);
			this.setY(yLocation);
			this.show(window);
		});

		showingProperty().addListener((o,oldVal,newVal)->{
			if(!newVal){
				transition.stop();
				scaleTransform.setX(0);
				scaleTransform.setY(0);
				items.setVisible(false);
			}
		});


	}

	public void addMenuItem(Node item){
		StackPane itemContainer = new StackPane();
		itemContainer.getChildren().add(item);
		itemsContainer.widthProperty().addListener((o,oldVal,newVal)->{itemContainer.setPrefWidth((double) newVal);});
		Rippler rippler = new Rippler(itemContainer);
		this.items.getChildren().add(rippler);
		if(this.items.getChildren().size()==1) VBox.setMargin(rippler, new Insets(5,0,0,0));
		else VBox.setMargin(rippler, new Insets(0,0,5,0));
	}


	@Override
	public void show(Window owner) {
		super.show(owner);		
		if(transition == null) transition = new PopupTransition() ; 
		transition.play();
	}


	private class PopupTransition extends CachedTimelineTransition {


		public PopupTransition() {
			super(itemsContainer, new Timeline(
					new KeyFrame(
							Duration.ZERO,       
							new KeyValue(scaleTransform.xProperty(), startScale ,Interpolator.EASE_BOTH),
							new KeyValue(scaleTransform.yProperty(), startScale ,Interpolator.EASE_BOTH),
							new KeyValue(content.opacityProperty(), 0 ,Interpolator.EASE_BOTH),
							new KeyValue(items.visibleProperty(), false , Interpolator.EASE_BOTH)
							),
							new KeyFrame(Duration.millis(300),					
									new KeyValue(content.opacityProperty(), 1 ,Interpolator.EASE_BOTH)
									),
							new KeyFrame(Duration.millis(1000),					
									new KeyValue(scaleTransform.xProperty(), 1 ,Interpolator.EASE_BOTH),
									new KeyValue(scaleTransform.yProperty(), 1 ,Interpolator.EASE_BOTH),									
									new KeyValue(items.visibleProperty(), true , Interpolator.EASE_BOTH)
									)
					)
					);
			// reduce the number to increase the shifting , increase number to reduce shifting
			setCycleDuration(Duration.seconds(0.4));
			setDelay(Duration.seconds(0));
		}

	}

}
