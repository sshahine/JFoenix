/*
 * JFoenix
 * Copyright (c) 2015, JFoenix and/or its affiliates., All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package com.jfoenix.skins;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import com.jfoenix.controls.JFXButton;

/**
 * @author sshahine
 *
 */

class JFXColorPalette extends Region {

	private static final int SQUARE_SIZE = 15;

	// package protected for testing purposes
	ColorPickerGrid colorPickerGrid;
	final JFXButton customColorLink = new JFXButton(JFXColorPickerSkin.getString("customColorLink"));
	JFXCustomColorPickerDialog customColorDialog = null;

	private ColorPicker colorPicker;
	private final GridPane customColorGrid = new GridPane();
	private final Separator separator = new Separator();
	private final Label customColorLabel = new Label(JFXColorPickerSkin.getString("customColorLabel"));
	
	private PopupControl popupControl;
	private ColorSquare focusedSquare;
	private ContextMenu contextMenu = null;

	private Color mouseDragColor = null;
	private boolean dragDetected = false;

	// Metrics for custom colors
	private int customColorNumber = 0;
	private int customColorRows = 0;
	private int customColorLastRowLength = 0;

	private final ColorSquare hoverSquare = new ColorSquare();

	public JFXColorPalette(final ColorPicker colorPicker) {
		getStyleClass().add("color-palette-region");
		this.colorPicker = colorPicker;
		colorPickerGrid = new ColorPickerGrid();
		colorPickerGrid.getChildren().get(0).requestFocus();
		customColorLabel.setAlignment(Pos.CENTER_LEFT);
		customColorLink.setPrefWidth(colorPickerGrid.prefWidth(-1));
		customColorLink.setAlignment(Pos.CENTER);
		customColorLink.setFocusTraversable(true);
		customColorLink.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent t) {
				if (customColorDialog == null) {
					customColorDialog = new JFXCustomColorPickerDialog(popupControl);
					customColorDialog.customColorProperty().addListener((ov, t1, t2) -> {
						colorPicker.setValue(customColorDialog.customColorProperty().get());
					});
					customColorDialog.setOnSave(() -> {
						Color customColor = customColorDialog.customColorProperty().get();
						buildCustomColors();
						colorPicker.getCustomColors().add(customColor);
						updateSelection(customColor);
						Event.fireEvent(colorPicker, new ActionEvent());
						colorPicker.hide();
					});
					customColorDialog.setOnUse(() -> {
						Event.fireEvent(colorPicker, new ActionEvent());
						colorPicker.hide();
					});
				}
				customColorDialog.setCurrentColor(colorPicker.valueProperty().get());
				if (popupControl != null) popupControl.setAutoHide(false);
				customColorDialog.show();
				customColorDialog.setOnHidden(event -> {
					if (popupControl != null) popupControl.setAutoHide(true);
				});
			}
		});

		initNavigation();
		customColorGrid.getStyleClass().add("color-picker-grid");
		customColorGrid.setVisible(false);
		buildCustomColors();
		colorPicker.getCustomColors().addListener(new ListChangeListener<Color>() {
			@Override public void onChanged(Change<? extends Color> change) {
				buildCustomColors();
			}
		});

		VBox paletteBox = new VBox();
		paletteBox.getStyleClass().add("color-palette");
		paletteBox.setStyle("-fx-background-color:WHITE; -fx-border-color:GRAY; -fx-border-radius:5px;");
		paletteBox.getChildren().addAll(colorPickerGrid, customColorLabel, customColorGrid, separator, customColorLink);

		hoverSquare.setMouseTransparent(true);
		hoverSquare.getStyleClass().addAll("hover-square");
		setFocusedSquare(null);

		getChildren().addAll(paletteBox, hoverSquare);
	}

	private void setFocusedSquare(ColorSquare square) {
		hoverSquare.setVisible(square != null);
		
		if (square == focusedSquare) {
			return;
		}
		focusedSquare = square;

		hoverSquare.setVisible(focusedSquare != null);
		if (focusedSquare == null) {
			return;
		}

		if (!focusedSquare.isFocused()) {
			focusedSquare.requestFocus();
		}

		hoverSquare.rectangle.setFill(focusedSquare.rectangle.getFill());

		Bounds b = square.localToScene(square.getLayoutBounds());

		double x = b.getMinX();
		double y = b.getMinY();

		double xAdjust;
		double scaleAdjust = hoverSquare.getScaleX() == 1.0 ? 0 : hoverSquare.getWidth() / 4.0;

		if (colorPicker.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT) {
			x = focusedSquare.getLayoutX();
			xAdjust = -focusedSquare.getWidth() + scaleAdjust;
		} else {
			xAdjust = focusedSquare.getWidth() / 2.0 + scaleAdjust;
		}

		hoverSquare.setLayoutX(snapPosition(x) - xAdjust);
		hoverSquare.setLayoutY(snapPosition(y) - focusedSquare.getHeight() / 2.0 + (hoverSquare.getScaleY() == 1.0 ? 0 : focusedSquare.getHeight() / 4.0));
	}

	private void buildCustomColors() {
		final ObservableList<Color> customColors = colorPicker.getCustomColors();
		customColorNumber = customColors.size();

		customColorGrid.getChildren().clear();
		if (customColors.isEmpty()) {
			customColorLabel.setVisible(false);
			customColorLabel.setManaged(false);
			customColorGrid.setVisible(false);
			customColorGrid.setManaged(false);
			return;
		} else {
			customColorLabel.setVisible(true);
			customColorLabel.setManaged(true);
			customColorGrid.setVisible(true);
			customColorGrid.setManaged(true);
			if (contextMenu == null) {
				MenuItem item = new MenuItem(JFXColorPickerSkin.getString("removeColor"));
				item.setOnAction(e -> {
					ColorSquare square = (ColorSquare)contextMenu.getOwnerNode();
					customColors.remove(square.rectangle.getFill());
					buildCustomColors();
				});
				contextMenu = new ContextMenu(item);
			}
		}

		int customColumnIndex = 0;
		int customRowIndex = 0;
		int remainingSquares = customColors.size() % NUM_OF_COLUMNS;
		int numEmpty = (remainingSquares == 0) ? 0 : NUM_OF_COLUMNS - remainingSquares;
		customColorLastRowLength = remainingSquares == 0 ? 12 : remainingSquares;

		for (int i = 0; i < customColors.size(); i++) {
			Color c = customColors.get(i);
			ColorSquare square = new ColorSquare(c, i, true);
			customColorGrid.add(square, customColumnIndex, customRowIndex);
			customColumnIndex++;
			if (customColumnIndex == NUM_OF_COLUMNS) {
				customColumnIndex = 0;
				customRowIndex++;
			}
		}
		for (int i = 0; i < numEmpty; i++) {
			ColorSquare emptySquare = new ColorSquare();
			customColorGrid.add(emptySquare, customColumnIndex, customRowIndex);
			customColumnIndex++;
		}
		customColorRows = customRowIndex + 1;
		requestLayout();

	}

	private void initNavigation() {
		setOnKeyPressed(ke -> {
			switch (ke.getCode()) {
			case SPACE:
			case ENTER:
				processSelectKey(ke);
				ke.consume();
				break;
			default: // no-op
			}
		});

//		setImpl_traversalEngine(new ParentTraversalEngine(this, new Algorithm() {
//			@Override
//			public Node select(Node owner, Direction dir, TraversalContext context) {
//				final Node subsequentNode = context.selectInSubtree(context.getRoot(), owner, dir);
//				switch (dir) {
//				case NEXT:
//				case NEXT_IN_LINE:
//				case PREVIOUS:
//					return subsequentNode;
//					// Here, we need to intercept the standard algorithm in a few cases to get the desired traversal
//					// For right or left direction we want to continue on the next or previous row respectively
//					// For up and down, the custom color panel might be skipped by the standard algorithm (if not wide enough
//					// to be between the current color and custom color button), so we need to include it in the path explicitly.
//				case LEFT:
//				case RIGHT:
//				case UP:
//				case DOWN:
//					if (owner instanceof ColorSquare) {
//						Node result =  processArrow((ColorSquare)owner, dir);
//						return result != null ? result : subsequentNode;
//					} else {
//						return subsequentNode;
//					}
//				}
//				return null;
//			}
//
//			private Node processArrow(ColorSquare owner, Direction dir) {
//				final int row = owner.index / NUM_OF_COLUMNS;
//				final int column = owner.index % NUM_OF_COLUMNS;
//
//				// Adjust the direction according to color picker orientation
//				dir = dir.getDirectionForNodeOrientation(colorPicker.getEffectiveNodeOrientation());
//				// This returns true for all the cases which we need to override
//				if (isAtBorder(dir, row, column, owner.isCustom)) {
//					// There's no other node in the direction from the square, so we need to continue on some other row
//					// or cycle
//					int subsequentRow = row;
//					int subsequentColumn = column;
//					boolean subSequentSquareCustom = owner.isCustom;
//					switch (dir) {
//					case LEFT:
//					case RIGHT:
//						// The next row is either the first or the last, except when cycling in custom colors, the last row
//						// might have different number of columns
//						if (owner.isCustom) {
//							subsequentRow = Math.floorMod(dir == Direction.LEFT ? row - 1 : row + 1, customColorRows);
//							subsequentColumn = dir == Direction.LEFT ? subsequentRow == customColorRows - 1 ?
//									customColorLastRowLength - 1 : NUM_OF_COLUMNS - 1 : 0;
//						} else {
//							subsequentRow = Math.floorMod(dir == Direction.LEFT ? row - 1 : row + 1, NUM_OF_ROWS);
//							subsequentColumn = dir == Direction.LEFT ? NUM_OF_COLUMNS - 1 : 0;
//						}
//						break;
//					case UP: // custom color are not handled here
//						subsequentRow = NUM_OF_ROWS - 1;
//						break;
//					case DOWN: // custom color are not handled here
//						if (customColorNumber > 0) {
//							subSequentSquareCustom = true;
//							subsequentRow = 0;
//							subsequentColumn = customColorRows > 1 ? column : Math.min(customColorLastRowLength - 1, column);
//							break;
//						} else {
//							return null; // Let the default algorith handle this
//						}
//
//					}
//					if (subSequentSquareCustom) {
//						return customColorGrid.getChildren().get(subsequentRow * NUM_OF_COLUMNS + subsequentColumn);
//					} else {
//						return colorPickerGrid.getChildren().get(subsequentRow * NUM_OF_COLUMNS + subsequentColumn);
//					}
//				}
//				return null;
//			}
//
//			private boolean isAtBorder(Direction dir, int row, int column, boolean custom) {
//				switch (dir) {
//				case LEFT:
//					return column == 0;
//				case RIGHT:
//					return custom && row == customColorRows - 1 ?
//							column == customColorLastRowLength - 1 : column == NUM_OF_COLUMNS - 1;
//				case UP:
//					return !custom && row == 0;
//				case DOWN:
//					return !custom && row == NUM_OF_ROWS - 1;
//				}
//				return false;
//			}
//
//			@Override
//			public Node selectFirst(TraversalContext context) {
//				return colorPickerGrid.getChildren().get(0);
//			}
//
//			@Override
//			public Node selectLast(TraversalContext context) {
//				return customColorLink;
//			}
//		}));
	}

	private void processSelectKey(KeyEvent ke) {
		if (focusedSquare != null) focusedSquare.selectColor(ke);
	}

	public void setPopupControl(PopupControl pc) {
		this.popupControl = pc;
	}

	public ColorPickerGrid getColorGrid() {
		return colorPickerGrid;
	}

	public boolean isCustomColorDialogShowing() {
		if (customColorDialog != null) return customColorDialog.isVisible();
		return false;
	}

	class ColorSquare extends StackPane {
		Rectangle rectangle;
		int index;
		boolean isEmpty;
		boolean isCustom;

		public ColorSquare() {
			this(null, -1, false);
		}

		public ColorSquare(Color color, int index) {
			this(color, index, false);
		}

		public ColorSquare(Color color, int index, boolean isCustom) {
			// Add style class to handle selected color square
			getStyleClass().add("color-square");
			if (color != null) {
				setFocusTraversable(true);

				focusedProperty().addListener((s, ov, nv) -> {
					setFocusedSquare(nv ? this : null);
				});

				addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
					setFocusedSquare(ColorSquare.this);
				});
				addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
					setFocusedSquare(null);
				});

				addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
					if (!dragDetected && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
						if (!isEmpty) {
							Color fill = (Color) rectangle.getFill();
							colorPicker.setValue(fill);
							colorPicker.fireEvent(new ActionEvent());
							updateSelection(fill);
							event.consume();
						}
						colorPicker.hide();
					} else if (event.getButton() == MouseButton.SECONDARY ||
							event.getButton() == MouseButton.MIDDLE) {
						// handle secondary/middle mouse button release event
						//                        if (isCustom && contextMenu != null) {
							//                            if (!contextMenu.isShowing()) {
								//                                contextMenu.show(ColorSquare.this, Side.RIGHT, 0, 0);
								//                                Utils.addMnemonics(contextMenu, ColorSquare.this.getScene(), colorPicker.impl_isShowMnemonics());
						//                            } else {
						//                                contextMenu.hide();
						//                                Utils.removeMnemonics(contextMenu, ColorSquare.this.getScene());
						//                            }
						//                        }
					}
				});
			}
			this.index = index;
			this.isCustom = isCustom;
			rectangle = new Rectangle(SQUARE_SIZE, SQUARE_SIZE);
			if (color == null) {
				rectangle.setFill(Color.WHITE);
				isEmpty = true;
			} else {
				rectangle.setFill(color);
			}
			
			rectangle.setStrokeType(StrokeType.INSIDE);

			String tooltipStr = JFXColorPickerSkin.tooltipString(color);
			Tooltip.install(this, new Tooltip((tooltipStr == null) ? "".toUpperCase() : tooltipStr.toUpperCase()));

			rectangle.getStyleClass().add("color-rect");

			getChildren().add(rectangle);
		}

		public void selectColor(KeyEvent event) {
			if (rectangle.getFill() != null) {
				if (rectangle.getFill() instanceof Color) {
					colorPicker.setValue((Color) rectangle.getFill());
					colorPicker.fireEvent(new ActionEvent());
				}
				event.consume();
			}
			colorPicker.hide();
		}
	}

	// The skin can update selection if colorpicker value changes..
	public void updateSelection(Color color) {
		setFocusedSquare(null);

		for (ColorSquare c : colorPickerGrid.getSquares()) {
			if (c.rectangle.getFill().equals(color)) {
				setFocusedSquare(c);
				return;
			}
		}
		// check custom colors
		for (Node n : customColorGrid.getChildren()) {
			ColorSquare c = (ColorSquare) n;
			if (c.rectangle.getFill().equals(color)) {
				setFocusedSquare(c);
				return;
			}
		}
	}

	class ColorPickerGrid extends GridPane {

		private final List<ColorSquare> squares;

		public ColorPickerGrid() {
			getStyleClass().add("color-picker-grid");
			setId("ColorCustomizerColorGrid");
			int columnIndex = 0, rowIndex = 0;
			squares = FXCollections.observableArrayList();
			final int numColors = RAW_VALUES.length / 3;
			Color[] colors = new Color[numColors];
			for (int i = 0; i < numColors; i++) {
				colors[i] = new Color(RAW_VALUES[(i * 3)] / 255,
						RAW_VALUES[(i * 3) + 1] / 255, RAW_VALUES[(i * 3) + 2] / 255,
						1.0);
				ColorSquare cs = new ColorSquare(colors[i], i);
				squares.add(cs);
			}

			for (ColorSquare square : squares) {
				add(square, columnIndex, rowIndex);
				columnIndex++;
				if (columnIndex == NUM_OF_COLUMNS) {
					columnIndex = 0;
					rowIndex++;
				}
			}
			setOnMouseDragged(t -> {
				if (!dragDetected) {
					dragDetected = true;
					mouseDragColor = colorPicker.getValue();
				}
				int xIndex = clamp(0,
						(int)t.getX()/(SQUARE_SIZE + 1), NUM_OF_COLUMNS - 1);
				int yIndex = clamp(0,
						(int)t.getY()/(SQUARE_SIZE + 1), NUM_OF_ROWS - 1);
				int index = xIndex + yIndex*NUM_OF_COLUMNS;
				colorPicker.setValue((Color) squares.get(index).rectangle.getFill());
				updateSelection(colorPicker.getValue());
			});
			addEventHandler(MouseEvent.MOUSE_RELEASED, t -> {
				if(colorPickerGrid.getBoundsInLocal().contains(t.getX(), t.getY())) {
					updateSelection(colorPicker.getValue());
					colorPicker.fireEvent(new ActionEvent());
					colorPicker.hide();
				} else {
					// restore color as mouse release happened outside the grid.
					if (mouseDragColor != null) {
						colorPicker.setValue(mouseDragColor);
						updateSelection(mouseDragColor);
					}
				}
				dragDetected = false;
			});
		}

		public List<ColorSquare> getSquares() {
			return squares;
		}

		@Override protected double computePrefWidth(double height) {
			return (SQUARE_SIZE + 1)*NUM_OF_COLUMNS;
		}

		@Override protected double computePrefHeight(double width) {
			return (SQUARE_SIZE + 1)*NUM_OF_ROWS;
		}
	}

	private static final int NUM_OF_COLUMNS = 10;
	private static double[] RAW_VALUES = {
		// WARNING: always make sure the number of colors is a divisable by NUM_OF_COLUMNS
		250, 250, 250, // first row
		245, 245, 245,
		238, 238, 238,
		224, 224, 224,
		189, 189, 189,
		158, 158, 158,
		117, 117, 117,
		97, 97, 97,
		66, 66, 66,
		33, 33, 33,
		// second row
		236, 239, 241,
		207, 216, 220,
		176, 190, 197,
		144, 164, 174,
		120, 144, 156,
		96, 125, 139,
		84, 110, 122,
		69, 90, 100,
		55, 71, 79,
		38, 50, 56,
		// third row
		255,235,238,
		255,205,210,
		239,154,154,
		229,115,115,
		239,83,80,
		244,67,54,
		229,57,53,
		211,47,47,
		198,40,40,
		183,28,28,
		// forth row
		252,228,236,
		248,187,208,
		244,143,177,
		240,98,146,
		236,64,122,
		233,30,99,
		216,27,96,
		194,24,91,
		173,20,87, 
		136,14,79,
		// fifth row
		243,229,245,
		225,190,231,
		206,147,216,
		186,104,200,
		171,71,188,
		156,39,176,
		142,36,170,
		123,31,162,
		106,27,154,
		74,20,140,
		// sixth row
		237,231,246,
		209,196,233,
		179,157,219,
		149,117,205,
		126,87,194,
		103,58,183,
		94,53,177,
		81,45,168,
		69,39,160,
		49,27,146,   	
		// seventh row
		232,234,246,
		197,202,233,
		159,168,218,
		121,134,203,
		92,107,192,
		63,81,181,
		57,73,171,
		48,63,159,
		40,53,147, 
		26,35,126,    	
		// eigth row
		227,242,253,
		187,222,251,
		144,202,249,
		100,181,246, 
		66,165,245,
		33,150,243,
		30,136,229,
		25,118,210,
		21,101,192,
		13,71,161,    	
		// ninth row
		225,245,254,
		179,229,252,
		129,212,250, 
		79,195,247,
		41,182,246, 
		3,169,244,
		3,155,229,
		2,136,209,
		2,119,189, 
		1,87,155,
		// tenth row    	
		224,247,250, 
		178,235,242, 
		128,222,234,
		77,208,225,
		38,198,218,
		0,188,212,
		0,172,193,
		0,151,167,
		0,131,143,
		0,96,100,
		// eleventh row
		224,242,241,
		178,223,219,
		128,203,196,
		77,182,172,
		38,166,154,
		0,150,136, 
		0,137,123, 
		0,121,107, 
		0,105,92, 
		0,77,64,
		// twelfth row    	
		232,245,233,
		200,230,201,
		165,214,167,
		129,199,132,
		102,187,106, 
		76,175,80,
		67,160,71, 
		56,142,60, 
		46,125,50, 
		27,94,32, 

		// thirteenth row
		241,248,233,
		220,237,200,
		197,225,165,
		174,213,129, 
		156,204,101,
		139,195,74,
		124,179,66, 
		104,159,56, 
		85,139,47, 
		51,105,30,  
		// fourteenth row
		249,251,231, 
		240,244,195, 
		230,238,156, 
		220,231,117, 
		212,225,87, 
		205,220,57, 
		192,202,51,
		175,180,43, 
		158,157,36, 
		130,119,23, 

		// fifteenth row
		255,253,231,
		255,249,196,
		255,245,157,
		255,241,118, 
		255,238,88,
		255,235,59,
		253,216,53,
		251,192,45,
		249,168,37,
		245,127,23,  

		// sixteenth row
		255,248,225,
		255,236,179,
		255,224,130,
		255,213,79,
		255,202,40,
		255,193,7,
		255,179,0,
		255,160,0,
		255,143,0,
		255,111,0,

		// seventeenth row
		255,243,224, 
		255,224,178, 
		255,204,128, 
		255,183,77,
		255,167,38, 
		255,152,0, 
		251,140,0, 
		245,124,0, 
		239,108,0, 
		230,81,0, 

		// eighteenth row
		251,233,231, 
		255,204,188, 
		255,171,145, 
		255,138,101, 
		255,112,67, 
		255,87,34, 
		244,81,30, 
		230,74,25, 
		216,67,21, 
		191,54,12, 

		// nineteenth row
		239,235,233, 
		215,204,200, 
		188,170,164, 
		161,136,127, 
		141,110,99, 
		121,85,72, 
		109,76,65, 
		93,64,55,
		78,52,46,
		62,39,35, 
	};

	private static final int NUM_OF_COLORS = RAW_VALUES.length / 3;
	private static final int NUM_OF_ROWS = NUM_OF_COLORS / NUM_OF_COLUMNS;
	
    private static int clamp(int min, int value, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
	
}