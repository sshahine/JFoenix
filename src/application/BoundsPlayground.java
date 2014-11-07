package application;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/** Demo for understanding JavaFX Layout Bounds */
public class BoundsPlayground extends Application {
	final ObservableList<Shape> shapes = FXCollections.observableArrayList();
	final ObservableList<ShapePair> intersections = FXCollections.observableArrayList();
	ObjectProperty<BoundsType> selectedBoundsType = new SimpleObjectProperty<BoundsType>(BoundsType.LAYOUT_BOUNDS);

	public static void main(String[] args) { launch(args); }
	@Override public void start(Stage stage) {
		stage.setTitle("Bounds Playground");
		// define some objects to manipulate on the scene.
		Circle greenCircle = new Circle(100, 100, 50, Color.FORESTGREEN); greenCircle.setId("Green Circle");
		Circle redCircle = new Circle(300, 200, 50, Color.FIREBRICK); redCircle.setId("Red Circle");

		Line line = new Line(25, 300, 375, 200); line.setId("Line");
		line.setStrokeLineCap(StrokeLineCap.ROUND);
		line.setStroke(Color.MIDNIGHTBLUE);
		line.setStrokeWidth(5);

		final Anchor anchor1 = new Anchor("Anchor 1", line.startXProperty(), line.startYProperty());
		final Anchor anchor2 = new Anchor("Anchor 2", line.endXProperty(), line.endYProperty());

		final Group group = new Group(greenCircle, redCircle, line, anchor1, anchor2);

		// monitor intersections of shapes in the scene.
		for (Node node : group.getChildrenUnmodifiable()) {
			if (node instanceof Shape) {
				shapes.add((Shape) node);
			}
		}
		testIntersections();

		// enable dragging for the scene objects.
		Circle[] circles = { greenCircle, redCircle, anchor1, anchor2 };
		for (Circle circle : circles) {
			enableDrag(circle);
			circle.centerXProperty().addListener(new ChangeListener<Number>() {
				@Override public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
					testIntersections();
				}
			});
			circle.centerYProperty().addListener(new ChangeListener<Number>() {
				@Override public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
					testIntersections();
				}
			});
		}

		// define an overlay to show the layout bounds of the scene's shapes.
		Group layoutBoundsOverlay = new Group();
		layoutBoundsOverlay.setMouseTransparent(true);
		for (Shape shape : shapes) {
			if (!(shape instanceof Anchor)) {
				layoutBoundsOverlay.getChildren().add(new BoundsDisplay(shape));
			}
		}
		// layout the scene.
		final StackPane background = new StackPane();
		background.setStyle("-fx-background-color: cornsilk;");
		final Scene scene = new Scene(new Group(background, group, layoutBoundsOverlay), 600, 500);
		background.prefHeightProperty().bind(scene.heightProperty());
		background.prefWidthProperty().bind(scene.widthProperty());
		stage.setScene(scene);
		stage.show();

		createUtilityWindow(stage, layoutBoundsOverlay, new Shape[] { greenCircle, redCircle });
	}

	// update the list of intersections.
	private void testIntersections() {
		intersections.clear();
		// for each shape test it's intersection with all other shapes.
		for (Shape src : shapes) {
			for (Shape dest : shapes) {
				ShapePair pair = new ShapePair(src, dest);
				if ((!(pair.a instanceof Anchor) && !(pair.b instanceof Anchor))
						&& !intersections.contains(pair)
						&& pair.intersects(selectedBoundsType.get())) {
					intersections.add(pair);
				}
			}
		}
	}

	// make a node movable by dragging it around with the mouse.
	private void enableDrag(final Circle circle) {
		final Delta dragDelta = new Delta();
		circle.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent mouseEvent) {
				// record a delta distance for the drag and drop operation.
				dragDelta.x = circle.getCenterX() - mouseEvent.getX();
				dragDelta.y = circle.getCenterY() - mouseEvent.getY();
				circle.getScene().setCursor(Cursor.MOVE);
			}
		});
		circle.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent mouseEvent) {
				circle.getScene().setCursor(Cursor.HAND);
			}
		});
		circle.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent mouseEvent) {
				circle.setCenterX(mouseEvent.getX() + dragDelta.x);
				circle.setCenterY(mouseEvent.getY() + dragDelta.y);
			}
		});
		circle.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent mouseEvent) {
				if (!mouseEvent.isPrimaryButtonDown()) {
					circle.getScene().setCursor(Cursor.HAND);
				}
			}
		});
		circle.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent mouseEvent) {
				if (!mouseEvent.isPrimaryButtonDown()) {
					circle.getScene().setCursor(Cursor.DEFAULT);
				}
			}
		});
	}

	// a helper enumeration of the various types of bounds we can work with.
	enum BoundsType { LAYOUT_BOUNDS, BOUNDS_IN_LOCAL, BOUNDS_IN_PARENT }

	// a translucent overlay display rectangle to show the bounds of a Shape.
	class BoundsDisplay extends Rectangle {
		// the shape to which the bounds display has been type.
		final Shape monitoredShape;
		private ChangeListener<Bounds> boundsChangeListener;

		BoundsDisplay(final Shape shape) {
			setFill(Color.LIGHTGRAY.deriveColor(1, 1, 1, 0.35));
			setStroke(Color.LIGHTGRAY.deriveColor(1, 1, 1, 0.5));
			setStrokeType(StrokeType.INSIDE);
			setStrokeWidth(3);
			monitoredShape = shape;
			monitorBounds(BoundsType.LAYOUT_BOUNDS);
		}

		// set the type of the shape's bounds to monitor for the bounds display.
		void monitorBounds(final BoundsType boundsType) {
			// remove the shape's previous boundsType.
			if (boundsChangeListener != null) {
				final ReadOnlyObjectProperty<Bounds> oldBounds;
				switch (selectedBoundsType.get()) {
				case LAYOUT_BOUNDS: oldBounds = monitoredShape.layoutBoundsProperty(); break;
				case BOUNDS_IN_LOCAL: oldBounds = monitoredShape.boundsInLocalProperty(); break;
				case BOUNDS_IN_PARENT: oldBounds = monitoredShape.boundsInParentProperty(); break;
				default: oldBounds = null;
				}
				if (oldBounds != null) {
					oldBounds.removeListener(boundsChangeListener);
				}
			}

			// determine the shape's bounds for the given boundsType.
			final ReadOnlyObjectProperty<Bounds> bounds;
			switch (boundsType) {
			case LAYOUT_BOUNDS: bounds = monitoredShape.layoutBoundsProperty(); break;
			case BOUNDS_IN_LOCAL: bounds = monitoredShape.boundsInLocalProperty(); break;
			case BOUNDS_IN_PARENT: bounds = monitoredShape.boundsInParentProperty(); break;
			default: bounds = null;
			}

			// set the visual bounds display based upon the new bounds and keep it in sync.
			updateBoundsDisplay(bounds.get());

			// keep the visual bounds display based upon the new bounds and keep it in sync.
			boundsChangeListener = new ChangeListener<Bounds>() {
				@Override public void changed(ObservableValue<? extends Bounds> observableValue, Bounds oldBounds, Bounds newBounds) {
					updateBoundsDisplay(newBounds);
				}
			};
			bounds.addListener(boundsChangeListener);
		}

		// update this bounds display to match a new set of bounds.
		private void updateBoundsDisplay(Bounds newBounds) {
			setX(newBounds.getMinX());
			setY(newBounds.getMinY());
			setWidth(newBounds.getWidth());
			setHeight(newBounds.getHeight());
		}
	}
	// an anchor displayed around a point.
	class Anchor extends Circle {
		Anchor(String id, DoubleProperty x, DoubleProperty y) {
			super(x.get(), y.get(), 10);
			setId(id);
			setFill(Color.GOLD.deriveColor(1, 1, 1, 0.5));
			setStroke(Color.GOLD);
			setStrokeWidth(2);
			setStrokeType(StrokeType.OUTSIDE);
			x.bind(centerXProperty());
			y.bind(centerYProperty());
		}
	}
	// records relative x and y co-ordinates.
	class Delta { double x, y; }
	// records a pair of (possibly) intersecting shapes.
	class ShapePair {
		private Shape a, b;
		public ShapePair(Shape src, Shape dest) {
			this.a = src; this.b = dest;
		}

		public boolean intersects(BoundsType boundsType) {
			if (a == b) return false;
			a.intersects(b.getBoundsInLocal());
			switch (boundsType) {
			case LAYOUT_BOUNDS: return a.getLayoutBounds().intersects(b.getLayoutBounds());
			case BOUNDS_IN_LOCAL: return a.getBoundsInLocal().intersects(b.getBoundsInLocal());
			case BOUNDS_IN_PARENT: return a.getBoundsInParent().intersects(b.getBoundsInParent());
			default: return false;
			}
		}

		@Override public String toString() {
			return a.getId() + " : " + b.getId();
		}

		@Override
		public boolean equals(Object other) {
			ShapePair o = (ShapePair) other;
			return o != null && ((a == o.a && b == o.b) || (a == o.b) && (b == o.a));
		}

		@Override
		public int hashCode() {
			int result = a != null ? a.hashCode() : 0;
			result = 31 * result + (b != null ? b.hashCode() : 0);
			return result;
		}
	}

	// define a utility stage for reporting intersections.
	private void createUtilityWindow(Stage stage, final Group boundsOverlay, final Shape[] transformableShapes) {
		final Stage reportingStage = new Stage();
		reportingStage.setTitle("Control Panel");
		reportingStage.initStyle(StageStyle.UTILITY);
		reportingStage.setX(stage.getX() + stage.getWidth());
		reportingStage.setY(stage.getY());

		// define content for the intersections utility panel.
		final ListView<ShapePair> intersectionView = new ListView<ShapePair>(intersections);
		final Label instructions = new Label(
				"Click on any circle in the scene to the left to drag it around."
				);
		instructions.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
		instructions.setStyle("-fx-font-weight: bold; -fx-text-fill: darkgreen;");

		final Label intersectionInstructions = new Label(
				"Any intersecting bounds in the scene will be reported below."
				);
		instructions.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

		// add the ability to set a translate value for the circles.
		final CheckBox translateNodes = new CheckBox("Translate circles");
		translateNodes.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean doTranslate) {
				if (doTranslate) {
					for (Shape shape : transformableShapes) {
						shape.setTranslateY(100);
						testIntersections();
					}
				} else {
					for (Shape shape : transformableShapes) {
						shape.setTranslateY(0);
						testIntersections();
					}
				}
			}
		});
		translateNodes.selectedProperty().set(false);

		// add the ability to add an effect to the circles.
		final Label modifyInstructions = new Label(
				"Modify visual display aspects."
				);
		modifyInstructions.setStyle("-fx-font-weight: bold;");
		modifyInstructions.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
		final CheckBox effectNodes = new CheckBox("Add an effect to circles");
		effectNodes.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean doTranslate) {
				if (doTranslate) {
					for (Shape shape : transformableShapes) {
						shape.setEffect(new DropShadow());
						testIntersections();
					}
				} else {
					for (Shape shape : transformableShapes) {
						shape.setEffect(null);
						testIntersections();
					}
				}
			}
		});
		effectNodes.selectedProperty().set(true);

		// add the ability to add a stroke to the circles.
		final CheckBox strokeNodes = new CheckBox("Add outside strokes to circles");
		strokeNodes.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean doTranslate) {
				if (doTranslate) {
					for (Shape shape : transformableShapes) {
						shape.setStroke(Color.LIGHTSEAGREEN);
						shape.setStrokeWidth(10);
						testIntersections();
					}
				} else {
					for (Shape shape : transformableShapes) {
						shape.setStrokeWidth(0);
						testIntersections();
					}
				}
			}
		});
		strokeNodes.selectedProperty().set(true);
		// add the ability to show or hide the layout bounds overlay.
		final Label showBoundsInstructions = new Label(
				"The gray squares represent layout bounds."
				);
		showBoundsInstructions.setStyle("-fx-font-weight: bold;");
		showBoundsInstructions.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
		final CheckBox showBounds = new CheckBox("Show Bounds");
		boundsOverlay.visibleProperty().bind(showBounds.selectedProperty());
		showBounds.selectedProperty().set(true);

		// create a container for the display control checkboxes.
		VBox displayChecks = new VBox(10);
		displayChecks.getChildren().addAll(modifyInstructions, translateNodes, effectNodes, strokeNodes, showBoundsInstructions, showBounds);

		// create a toggle group for the bounds type to use.
		ToggleGroup boundsToggleGroup = new ToggleGroup();
		final RadioButton useLayoutBounds = new RadioButton("Use Layout Bounds");
		final RadioButton useBoundsInLocal = new RadioButton("Use Bounds in Local");
		final RadioButton useBoundsInParent = new RadioButton("Use Bounds in Parent");
		useLayoutBounds.setToggleGroup(boundsToggleGroup);
		useBoundsInLocal.setToggleGroup(boundsToggleGroup);
		useBoundsInParent.setToggleGroup(boundsToggleGroup);
		VBox boundsToggles = new VBox(10);
		boundsToggles.getChildren().addAll(useLayoutBounds, useBoundsInLocal, useBoundsInParent);

		// change the layout bounds display depending on which bounds type has been selected.
		useLayoutBounds.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean isSelected) {
				if (isSelected) {
					for (Node overlay : boundsOverlay.getChildren()) {
						((BoundsDisplay) overlay).monitorBounds(BoundsType.LAYOUT_BOUNDS);
					}
					selectedBoundsType.set(BoundsType.LAYOUT_BOUNDS);
					testIntersections();
				}
			}
		});
		useBoundsInLocal.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean isSelected) {
				if (isSelected) {
					for (Node overlay : boundsOverlay.getChildren()) {
						((BoundsDisplay) overlay).monitorBounds(BoundsType.BOUNDS_IN_LOCAL);
					}
					selectedBoundsType.set(BoundsType.BOUNDS_IN_LOCAL);
					testIntersections();
				}
			}
		});
		useBoundsInParent.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean isSelected) {
				if (isSelected) {
					for (Node overlay : boundsOverlay.getChildren()) {
						((BoundsDisplay) overlay).monitorBounds(BoundsType.BOUNDS_IN_PARENT);
					}
					selectedBoundsType.set(BoundsType.BOUNDS_IN_PARENT);
					testIntersections();
				}
			}
		});
		useLayoutBounds.selectedProperty().set(true);

		WebView boundsExplanation = new WebView();
		boundsExplanation.getEngine().loadContent(
				"<html><body bgcolor='darkseagreen' fgcolor='lightgrey' style='font-size:12px'><dl>" +
						"<dt><b>Layout Bounds</b></dt><dd>The boundary of the shape.</dd><br/>" +
						"<dt><b>Bounds in Local</b></dt><dd>The boundary of the shape and effect.</dd><br/>" +
						"<dt><b>Bounds in Parent</b></dt><dd>The boundary of the shape, effect and transforms.<br/>The co-ordinates of what you see.</dd>" +
						"</dl></body></html>"
				);
		boundsExplanation.setPrefWidth(100);
		boundsExplanation.setMinHeight(130);
		boundsExplanation.setMaxHeight(130);
		boundsExplanation.setStyle("-fx-background-color: transparent");

		// layout the utility pane.
		VBox utilityLayout = new VBox(10);
		utilityLayout.setStyle("-fx-padding:10; -fx-background-color: linear-gradient(to bottom, lightblue, derive(lightblue, 20%));");
		utilityLayout.getChildren().addAll(instructions, intersectionInstructions, intersectionView, displayChecks, boundsToggles, boundsExplanation);
		utilityLayout.setPrefHeight(530);
		reportingStage.setScene(new Scene(utilityLayout));
		reportingStage.show();

		// ensure the utility window closes when the main app window closes.
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override public void handle(WindowEvent windowEvent) {
				reportingStage.close();
			}
		});
	}
}