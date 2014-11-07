package customui.components;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class Rippler extends StackPane {


	public static enum RipplerPos{FRONT, BACK};
	public static enum RipplerMask{CIRCLE, RECT};
	
	private AnchorPane ripplerPane;
	private RipplerMask maskType = RipplerMask.RECT ;
	private RipplerPos pos = RipplerPos.FRONT ;
	private boolean enabled = true;
	private Node control;
	private ObjectProperty<Color> color = new SimpleObjectProperty<Color>(Color.rgb(0, 200, 255));
	
	public Rippler(Node control){
		this(control, RipplerMask.RECT, RipplerPos.FRONT);
	}
	
	public Rippler(Node control, RipplerMask mask){
		this(control, mask, RipplerPos.FRONT);
		this.maskType = mask;
	}
	
	public Rippler(Node control, RipplerMask mask,  RipplerPos pos){
		super();
		
		this.control = control;
		this.maskType = mask;
		this.pos = pos;
		
		// create rippler panels
		
		final RippleGenerator rippler = new RippleGenerator();
		ripplerPane = new AnchorPane();
		ripplerPane.getChildren().add(rippler);
				
		if(this.pos == RipplerPos.BACK)  ripplerPane.getChildren().add(this.control);
		else this.getChildren().add(this.control);
		
		this.getChildren().add(ripplerPane);

		if(this.control instanceof Control){
			((Control)this.control).widthProperty().addListener((o,oldVal,newVal) -> {ripplerPane.setMaxWidth((double) newVal);});
			((Control)this.control).heightProperty().addListener((o,oldVal,newVal) -> {ripplerPane.setMaxHeight((double) newVal);});
		}else if(this.control instanceof Pane){
			((Pane)this.control).widthProperty().addListener((o,oldVal,newVal) -> {ripplerPane.setMaxWidth((double) newVal);});
			((Pane)this.control).heightProperty().addListener((o,oldVal,newVal) -> {ripplerPane.setMaxHeight((double) newVal);});
		}
		
		// add listeners
		ripplerPane.setOnMousePressed((event) -> {
			rippler.setGeneratorCenterX(event.getX());
			rippler.setGeneratorCenterY(event.getY());
			rippler.createRipple();
			rippler.startGenerating();
		});

		ripplerPane.setOnMouseDragged((event) -> {
			rippler.setGeneratorCenterX(event.getX());
			rippler.setGeneratorCenterY(event.getY());

		});

		ripplerPane.setOnMouseReleased((event) -> {
			rippler.stopGenerating();
			for(Node node: Rippler.this.getChildren()){
				if(!(node instanceof AnchorPane))
					node.fireEvent(event);	          
			}	        
		});
	}	
		
	
	public Color getColor(){
		return this.color.get();
	}
	
	public ObjectProperty<Color> colorProperty(){
		return this.color;
	}
	public void setColor(Color color){
		this.color.set(color);
	}
	
	public void setEnabled(boolean enable){
		this.enabled = enable;
	}
	
	/**
	 * Generates ripples on the screen every 0.3 seconds or whenever
	 * the createRipple method is called. Ripples grow and fade out
	 * over 0.6 seconds
	 */
	// the effect generator
	class RippleGenerator extends Group {

		private double generatorCenterX = 0;
		private double generatorCenterY = 0;


		private Timeline generate = new Timeline(
				new KeyFrame(Duration.seconds(0.3), new EventHandler<ActionEvent>() {
					@Override public void handle(ActionEvent event) {
						createRipple();
					}
				}
						)
				);

		public RippleGenerator() {
			generate.setCycleCount(Timeline.INDEFINITE);
		}

		public void createRipple() {
			if(enabled){
				final Ripple ripple = new Ripple(generatorCenterX, generatorCenterY);				
				Shape mask = new Rectangle(ripplerPane.getWidth()-0.1,ripplerPane.getHeight()-0.1); // -0.1 to prevent resizing the anchor pane
				if(maskType.equals(Rippler.RipplerMask.CIRCLE)){
					mask = new Circle(ripplerPane.getWidth()/2 , ripplerPane.getHeight()/2, (ripplerPane.getWidth()/2) - 0.1 , Color.BLUE);	
				}
				ripple.setClip(mask);
				getChildren().add(ripple);
				ripple.animation.play();
				ripple.animation.setOnFinished((e) -> {getChildren().remove(ripple);ripple.animation.stop(); });
			}
		}

		public void startGenerating() {
			generate.play();
		}

		public void stopGenerating() {
			generate.stop();
		}

		public void setGeneratorCenterX(double generatorCenterX) {
			this.generatorCenterX = generatorCenterX;
		}

		public void setGeneratorCenterY(double generatorCenterY) {
			this.generatorCenterY = generatorCenterY;
		}

		private class Ripple extends Circle {
			Timeline animation = new Timeline(
					new KeyFrame(Duration.ZERO, new KeyValue(radiusProperty(),  0)),
					new KeyFrame(Duration.seconds(0.2), new KeyValue(opacityProperty(), 1)),
					new KeyFrame(Duration.seconds(0.6), new KeyValue(radiusProperty(),  100)),
					new KeyFrame(Duration.seconds(0.6), new KeyValue(opacityProperty(), 0))
					);

			private Ripple(double centerX, double centerY) {
				super(centerX, centerY, 0, null);				
				Color circleColor = new Color(color.get().getRed(), color.get().getGreen(), color.get().getBlue(),0.3);
				setStroke(circleColor);
				setFill(circleColor);
			}
		}


	}

}
