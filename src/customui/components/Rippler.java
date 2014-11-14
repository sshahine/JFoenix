package customui.components;

import java.util.List;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class Rippler extends StackPane {

	public static enum RipplerPos{FRONT, BACK};
	public static enum RipplerMask{CIRCLE, RECT};

	protected Pane ripplerPane;
	private RipplerMask maskType = RipplerMask.RECT ;
	protected RipplerPos pos = RipplerPos.FRONT;
	private boolean enabled = true;
	protected Node control;
	private ObjectProperty<Paint> color = new SimpleObjectProperty<Paint>(Color.rgb(0, 200, 255));
	private double rippleRadius = 150;
	protected final RippleGenerator rippler;
	
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

		rippler = new RippleGenerator();
		ripplerPane = new C3DAnchorPane();
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
		initListeners();
	}	
	
	public Paint getColor(){
		return this.color.get();
	}

	public ObjectProperty<Paint> colorProperty(){
		return this.color;
	}
	public void setColor(Paint color){
		this.color.set(color);
	}

	public void setEnabled(boolean enable){
		this.enabled = enable;
	}

	// methods that can be changed by extending the rippler class
	protected Shape getMask(){
		Shape mask = new Rectangle(ripplerPane.getWidth() - 0.1,ripplerPane.getHeight() - 0.1); // -0.1 to prevent resizing the anchor pane
		if(maskType.equals(Rippler.RipplerMask.CIRCLE))
			mask = new Circle(ripplerPane.getWidth()/2 , ripplerPane.getHeight()/2, (ripplerPane.getWidth()/2) - 0.1, Color.BLUE);	
		return mask;
	}
	
	protected void initListeners(){
		ripplerPane.setOnMousePressed((event) -> {
			createRipple(event.getX(),event.getY());
			if(this.pos == RipplerPos.FRONT)
				this.control.fireEvent(event);
		});
		ripplerPane.setOnMouseReleased((event) -> {
			if(this.pos == RipplerPos.FRONT)
				this.control.fireEvent(event);
		});
		ripplerPane.setOnMouseClicked((event) -> {
			if(this.pos == RipplerPos.FRONT )
				this.control.fireEvent(event);
		});
	}
	
	protected void createRipple(double x, double y){
		rippler.setGeneratorCenterX(x);
		rippler.setGeneratorCenterY(y);
		rippler.createRipple();
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
		private OverLayRipple overlayRect;

		public void createRipple() {
			if(enabled){
				// create overlay once then change its color later 
				if(overlayRect == null){
					overlayRect = new OverLayRipple();
					overlayRect.setClip(getMask());
					getChildren().add(overlayRect);
				}
				overlayRect.setFill(new Color(((Color)color.get()).getRed(), ((Color)color.get()).getGreen(), ((Color)color.get()).getBlue(),0.2));

				// create the ripple effect

				final Ripple ripple = new Ripple(generatorCenterX, generatorCenterY);				
				ripple.setClip(getMask());
				getChildren().add(ripple);			

				overlayRect.animation.setRate(1);
				overlayRect.animation.play();
				ripple.inAnimation.play();

				// create fade out transition for the ripple
				ripplerPane.setOnMouseReleased((e)->{
					overlayRect.animation.setRate(-1);
					overlayRect.animation.play();
					ripple.inAnimation.pause();
					double fadeOutRadious = rippleRadius + 20;
					if(ripple.radiusProperty().get() < rippleRadius*0.5)
						fadeOutRadious = rippleRadius;

					Timeline outAnimation = new Timeline(
							new KeyFrame(Duration.seconds(0.3),
									new KeyValue(ripple.radiusProperty(), fadeOutRadious ,Interpolator.LINEAR),
									new KeyValue(ripple.opacityProperty(), 0, Interpolator.LINEAR)
									));
					outAnimation.play();
					outAnimation.setOnFinished((event)->{
						getChildren().remove(ripple);	
					});
				});

			}
		}

		public void setGeneratorCenterX(double generatorCenterX) {
			this.generatorCenterX = generatorCenterX;
		}

		public void setGeneratorCenterY(double generatorCenterY) {
			this.generatorCenterY = generatorCenterY;
		}

		private class OverLayRipple extends Rectangle{
			Timeline animation = new Timeline(
					new KeyFrame(Duration.ZERO,
							new KeyValue(opacityProperty(),  0,Interpolator.EASE_BOTH)
							),new KeyFrame(Duration.seconds(0.2),
									new KeyValue(opacityProperty(), 1,Interpolator.EASE_BOTH)
									));
			public OverLayRipple() {
				super(ripplerPane.getWidth() - 0.1,ripplerPane.getHeight() - 0.1);
			}
		}

		private class Ripple extends Circle {

			Timeline inAnimation = new Timeline(
					new KeyFrame(Duration.ZERO,
							new KeyValue(radiusProperty(),  0,Interpolator.LINEAR),
							new KeyValue(opacityProperty(), 1,Interpolator.LINEAR)
							),new KeyFrame(Duration.seconds(0.3), 
									new KeyValue(radiusProperty(),  rippleRadius ,Interpolator.LINEAR)					
									));

			private Ripple(double centerX, double centerY) {
				super(centerX, centerY, 0, null);	
				if(color.get() instanceof Color){
					Color circleColor = new Color(((Color)color.get()).getRed(), ((Color)color.get()).getGreen(), ((Color)color.get()).getBlue(),0.3);
					setStroke(circleColor);
					setFill(circleColor);
				}else{
					setStroke(color.get());
					setFill(color.get());
				}
			}
		}
	}

	private class C3DAnchorPane extends AnchorPane{

		@Override protected double computeMinWidth(double height) {
			return computeWidth(true, height);
		}

		@Override protected double computeMinHeight(double width) {
			return computeHeight(true, width);
		}

		@Override protected double computePrefWidth(double height) {
			return computeWidth(false, height);
		}

		@Override protected double computePrefHeight(double width) {
			return computeHeight(false, width);
		}

		private double computeWidth(final boolean minimum, final double height) {
			double max = 0;
			double contentHeight = height != -1 ? height - getInsets().getTop() - getInsets().getBottom() : -1;
			final List<Node> children = getManagedChildren();
			for (Node child : children) {
				Double leftAnchor = getLeftAnchor(child);
				Double rightAnchor = getRightAnchor(child);

				double left = leftAnchor != null? leftAnchor :
					(rightAnchor != null? 0 : child.getLayoutBounds().getMinX() + child.getLayoutX());
				double right = rightAnchor != null? rightAnchor : 0;
				double childHeight = -1;
				if (child.getContentBias() == Orientation.VERTICAL && contentHeight != -1) {
					// The width depends on the node's height!
					childHeight = computeChildHeight(child, getTopAnchor(child), getBottomAnchor(child), contentHeight, -1);
				}
				max = Math.max(max, left + (minimum && leftAnchor != null && rightAnchor != null?
						child.minWidth(childHeight) : child.prefWidth(childHeight)) + right);
			}

			final Insets insets = getInsets();
			return insets.getLeft() + max + insets.getRight();
		}

		private double computeHeight(final boolean minimum, final double width) {
			double max = 0;
			double contentWidth = width != -1 ? width - getInsets().getLeft()- getInsets().getRight() : -1;
			final List<Node> children = getManagedChildren();
			for (Node child : children) {
				Double topAnchor = getTopAnchor(child);
				Double bottomAnchor = getBottomAnchor(child);

				double top = topAnchor != null? topAnchor :
					(bottomAnchor != null? 0 : child.getLayoutBounds().getMinY() + child.getLayoutY());
				double bottom = bottomAnchor != null? bottomAnchor : 0;
				double childWidth = -1;
				if (child.getContentBias() == Orientation.HORIZONTAL && contentWidth != -1) {
					childWidth = computeChildWidth(child, getLeftAnchor(child), getRightAnchor(child), contentWidth, -1);
				}
				max = Math.max(max, top + (minimum && topAnchor != null && bottomAnchor != null?
						child.minHeight(childWidth) : child.prefHeight(childWidth)) + bottom);
			}

			final Insets insets = getInsets();
			return insets.getTop() + max + insets.getBottom();
		}
		private double computeChildWidth(Node child, Double leftAnchor, Double rightAnchor, double areaWidth, double height) {
			if (leftAnchor != null && rightAnchor != null && child.isResizable()) {
				final Insets insets = getInsets();
				return areaWidth - insets.getLeft() - insets.getRight() - leftAnchor - rightAnchor;
			}
			return computeChildPrefAreaWidth(child, -1, Insets.EMPTY, height, true);
		}

		private double computeChildHeight(Node child, Double topAnchor, Double bottomAnchor, double areaHeight, double width) {
			if (topAnchor != null && bottomAnchor != null && child.isResizable()) {
				final Insets insets = getInsets();
				return areaHeight - insets.getTop() - insets.getBottom() - topAnchor - bottomAnchor;
			}
			return computeChildPrefAreaHeight(child, -1, Insets.EMPTY, width);
		}


		private double computeChildPrefAreaWidth(Node child, double baselineComplement, Insets margin, double height, boolean fillHeight) {
			final boolean snap = isSnapToPixel();
			double left = margin != null? snapSpace(margin.getLeft(), snap) : 0;
			double right = margin != null? snapSpace(margin.getRight(), snap) : 0;
			double alt = -1;
			if (height != -1 && child.isResizable() && child.getContentBias() == Orientation.VERTICAL) { // width depends on height
				double top = margin != null? snapSpace(margin.getTop(), snap) : 0;
				double bottom = margin != null? snapSpace(margin.getBottom(), snap) : 0;
				double bo = child.getBaselineOffset();
				final double contentHeight = bo == BASELINE_OFFSET_SAME_AS_HEIGHT && baselineComplement != -1 ?
						height - top - bottom - baselineComplement :
							height - top - bottom;
				if (fillHeight) {
					alt = snapSize(boundedSize(
							child.minHeight(-1), contentHeight,
							child.maxHeight(-1)));
				} else {
					alt = snapSize(boundedSize(
							child.minHeight(-1),
							child.prefHeight(-1),
							Math.min(child.maxHeight(-1), contentHeight)));
				}
			}
			return left + snapSize(boundedSize(child.minWidth(alt), child.prefWidth(alt), child.maxWidth(alt))) + right;
		}

		private double computeChildPrefAreaHeight(Node child, double prefBaselineComplement, Insets margin, double width) {
			final boolean snap = isSnapToPixel();
			double top = margin != null? snapSpace(margin.getTop(), snap) : 0;
			double bottom = margin != null? snapSpace(margin.getBottom(), snap) : 0;

			double alt = -1;
			if (child.isResizable() && child.getContentBias() == Orientation.HORIZONTAL) { // height depends on width
				double left = margin != null ? snapSpace(margin.getLeft(), snap) : 0;
				double right = margin != null ? snapSpace(margin.getRight(), snap) : 0;
				alt = snapSize(boundedSize(
						child.minWidth(-1), width != -1 ? width - left - right
								: child.prefWidth(-1), child.maxWidth(-1)));
			}

			if (prefBaselineComplement != -1) {
				double baseline = child.getBaselineOffset();
				if (child.isResizable() && baseline == BASELINE_OFFSET_SAME_AS_HEIGHT) {
					// When baseline is same as height, the preferred height of the node will be above the baseline, so we need to add
					// the preferred complement to it
					return top + snapSize(boundedSize(child.minHeight(alt), child.prefHeight(alt), child.maxHeight(alt))) + bottom
							+ prefBaselineComplement;
				} else {
					// For all other Nodes, it's just their baseline and the complement.
					// Note that the complement already contain the Node's preferred (or fixed) height
					return top + baseline + prefBaselineComplement + bottom;
				}
			} else {
				return top + snapSize(boundedSize(child.minHeight(alt), child.prefHeight(alt), child.maxHeight(alt))) + bottom;
			}
		}


		private double snapSpace(double value, boolean snapToPixel) {
			return snapToPixel ? Math.round(value) : value;
		}
		private double boundedSize(double min, double pref, double max) {
			double a = pref >= min ? pref : min;
			double b = min >= max ? min : max;
			return a <= b ? a : b;
		}

	}


}
