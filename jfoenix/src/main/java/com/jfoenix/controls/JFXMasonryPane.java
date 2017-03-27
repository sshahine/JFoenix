/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.jfoenix.controls;

import com.jfoenix.transitions.CachedTransition;
import javafx.animation.*;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.BoundingBox;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A JFXMasonryPane implements asymmetrical grid layoutMode, it places the child nodes according to 
 * one of the modes:
 * 
 * <h3>Masonry Layout</h3> Nodes will be added one after another, first in the horizontal direction
 * , then vertically. sort of like a mason fitting stones in a wall.
 * 
 * <h3>Bin Packing Layout(First Fit)</h3> it works similar to masonry layoutMode, however it tries to 
 * fill the empty gaps caused in masonry layoutMode.
 *  
 * <b>Note:</b> childs that doesn't fit in the grid will be hidden.
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2016-05-24
 *
 */
public class JFXMasonryPane extends Pane {

	/***************************************************************************
	 *                                                                         *
	 * Private Fields                                                          *
	 *                                                                         *
	 **************************************************************************/	
	private boolean performingLayout = false;
	// these variables are computed when layoutChildren is called
	private int[][] matrix;
	private HashMap<Region, Transition> animationMap = null;
	private ParallelTransition trans = new ParallelTransition();
	private boolean valid = false;
//	private GridPane root = new GridPane();
//	String[] colors = {"-fx-border-color:#EEEEEE", "-fx-border-color:RED;","-fx-border-color:BLUE; ","-fx-border-color:GREEN;", "-fx-border-color:PURPLE;"};
	private List<BoundingBox> oldBoxes;

	/***************************************************************************
	 *                                                                         *
	 * Constructor                                                             *
	 *                                                                         *
	 **************************************************************************/	
	/**
	 * Constructs a new JFXMasonryPane
	 */
	public JFXMasonryPane() {
		this.widthProperty().addListener((o,oldVal,newVal)->{valid = false;});
		this.heightProperty().addListener((o,oldVal,newVal)->{valid = false;});
		ChangeListener<? super Number> layoutListener = (o,oldVal,newVal)->{
			valid = false;
			this.requestLayout();
		};
		this.cellWidthProperty().addListener(layoutListener);
		this.cellHeightProperty().addListener(layoutListener);
		this.hSpacingProperty().addListener(layoutListener);
		this.vSpacingProperty().addListener(layoutListener);
		this.limitColumnProperty().addListener(layoutListener);
		this.limitRowProperty().addListener(layoutListener);
		this.getChildren().addListener((Change<? extends Node> c)->{
			valid = false;
			matrix = null;
			this.requestLayout();
		});
	}
	/***************************************************************************
	 *                                                                         *
	 * Override/Inherited methods                                              *
	 *                                                                         *
	 **************************************************************************/	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void layoutChildren() {
		performingLayout = true;
		if(!valid){

			int col, row;
			col = (int) Math.floor(this.getWidth()/ (getCellWidth() + 2*getHSpacing()));
			col = getLimitColumn() != -1 && col > getLimitColumn()? getLimitColumn() : col;

			if(matrix!=null && col == matrix[0].length) {
				performingLayout = false;
				return;
			}
			//(int) Math.floor(this.getHeight() / (cellH + 2*vSpacing));
			row = 100;
			row = getLimitRow() != -1 && row > getLimitRow() ? getLimitRow() : row;

			matrix = new int[row][col];
			double minWidth = -1;
			double minHeight = -1;

			List<BoundingBox> newBoxes;
			List<Region> childs = new ArrayList<>();
			for(int i = 0 ; i < getChildren().size(); i++)
				if(getChildren().get(i) instanceof Region) childs.add((Region) getChildren().get(i));
			newBoxes = layoutMode.get().fillGrid(matrix, childs, getCellWidth() , getCellHeight() , row, col, getHSpacing(), getVSpacing()); 

			if(newBoxes == null){
				performingLayout = false;
				return;
			}
			for (int i = 0; i < getChildren().size() && i < newBoxes.size(); i++) {
				Region block = (Region) getChildren().get(i);
				if(!(block instanceof GridPane)){
					double blockX, blockY, blockWidth, blockHeight;
					if(newBoxes.get(i)!=null){
						blockX = newBoxes.get(i).getMinY()*getCellWidth() + ((newBoxes.get(i).getMinY()+1)*2-1)*getHSpacing();
						blockY = newBoxes.get(i).getMinX()* getCellHeight() + ((newBoxes.get(i).getMinX()+1)*2-1)*getVSpacing();
						blockWidth = newBoxes.get(i).getWidth()*getCellWidth() + (newBoxes.get(i).getWidth()-1)*2*getHSpacing();
						blockHeight = newBoxes.get(i).getHeight()* getCellHeight() + (newBoxes.get(i).getHeight()-1)*2*getVSpacing();
					}else{
						blockX = block.getLayoutX();
						blockY = block.getLayoutY();
						blockWidth = -1;
						blockHeight = -1;
					}
					
					if(animationMap == null){
						// init static children
						block.setLayoutX(blockX);
						block.setLayoutY(blockY);						
						block.setPrefSize(blockWidth, blockHeight);
						block.resizeRelocate(blockX, blockY , blockWidth, blockHeight);
					}else{
						if(oldBoxes == null || i >= oldBoxes.size()){
							// handle new children
							block.setOpacity(0);
							block.setLayoutX(blockX);
							block.setLayoutY(blockY);						
							block.setPrefSize(blockWidth, blockHeight);
							block.resizeRelocate(blockX, blockY , blockWidth, blockHeight);							
						}
						
						if(newBoxes.get(i)!=null){
							// handle children repositioning
							animationMap.put(block, new CachedTransition(block, new Timeline(new KeyFrame(Duration.millis(2000), 
									new KeyValue(block.opacityProperty(), 1, Interpolator.LINEAR),
									new KeyValue(block.layoutXProperty(), blockX, Interpolator.LINEAR),
									new KeyValue(block.layoutYProperty(), blockY , Interpolator.LINEAR)))){{
										setCycleDuration(Duration.seconds(0.320));
										setDelay(Duration.seconds(0));
										setOnFinished((finish)->{
											block.setLayoutX(blockX);
											block.setLayoutY(blockY);
											block.setOpacity(1);
										});
									}});
							
						} else {
							// handle children is being hidden ( cause it can't fit in the pane )
							animationMap.put(block, new CachedTransition(block, new Timeline(new KeyFrame(Duration.millis(2000), 
									new KeyValue(block.opacityProperty(), 0, Interpolator.LINEAR),
									new KeyValue(block.layoutXProperty(), blockX, Interpolator.LINEAR),
									new KeyValue(block.layoutYProperty(), blockY , Interpolator.LINEAR)))){{
										setCycleDuration(Duration.seconds(0.320));
										setDelay(Duration.seconds(0));	
										setOnFinished((finish)->{
											block.setLayoutX(blockX);
											block.setLayoutY(blockY);
											block.setOpacity(0);
										});
									}});
						}

					}
					if(newBoxes.get(i)!=null){
						if(blockX + blockWidth> minWidth ) minWidth = blockX + blockWidth;
						if(blockY + blockHeight > minHeight ) minHeight = blockY + blockHeight;
					}
				}
			}
			this.setMinSize(minWidth , minHeight);
			if(animationMap == null) animationMap = new HashMap<>();

			trans.stop();
			ParallelTransition newTransition = new ParallelTransition();		
			newTransition.getChildren().addAll(animationMap.values());
			newTransition.play();
			trans = newTransition;
			oldBoxes = newBoxes;

			// FOR DEGBBUGING
			
//						root.getChildren().clear();		
//						for(int y = 0; y < matrix.length; y++){
//							for(int x = 0; x < matrix[0].length; x++){
//			
//								// Create a new TextField in each Iteration
//								Label tf = new Label();
//								tf.setStyle(matrix[y][x] == 0 ? colors[0] : colors[matrix[y][x]%4+1]);
//								tf.setMinWidth(getCellWidth());
//								tf.setMinHeight(getCellHeight());
//								tf.setAlignment(Pos.CENTER);
//								tf.setText(matrix[y][x] + "");
//								// Iterate the Index using the loops
//								root.setRowIndex(tf,y);
//								root.setColumnIndex(tf,x);    
//								root.setMargin(tf, new Insets(getVSpacing(),getHSpacing(),getVSpacing(),getHSpacing()));
//								root.getChildren().add(tf);
//							}
//						}
			valid = true;
		}

		// FOR DEGBBUGING
//				if(!getChildren().contains(root)) getChildren().add(root);
//				root.resizeRelocate(0, 0, this.getWidth(), this.getHeight());

		performingLayout = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void requestLayout() {
		if (performingLayout) { return; }
		super.requestLayout();
	}

	/***************************************************************************
	 *                                                                         *
	 * Properties                                                              *
	 *                                                                         *
	 **************************************************************************/
	/**
	 * the layout mode of Masonry Pane
	 */
	private ObjectProperty<LayoutMode> layoutMode = new SimpleObjectProperty<>(LayoutMode.MASONRY);
	public final ObjectProperty<LayoutMode> layoutModeProperty() {
		return this.layoutMode;
	}
	/**
	 * @return the LayoutMode of masonry pane
	 */
	public final LayoutMode getLayoutMode() {
		return this.layoutModeProperty().get();
	}
	/**
	 * sets the layout mode
	 * @param layoutMode to be used, either MASONRY or BIN_PACKING
	 */
	public final void setLayoutMode(final LayoutMode layoutMode) {
		this.layoutModeProperty().set(layoutMode);
	}

	
	
	/**
	 * the cell width of masonry grid
	 */
	private DoubleProperty cellWidth = new SimpleDoubleProperty(70);
	public final DoubleProperty cellWidthProperty() {
		return this.cellWidth;
	}
	/**
	 * @return the cell width of the masonry pane grid
	 */
	public final double getCellWidth() {
		return this.cellWidthProperty().get();
	}
	/**
	 * sets the cell width of the masonry pane grid
	 * @param cellWidth of the grid
	 */
	public final void setCellWidth(final double cellWidth) {
		this.cellWidthProperty().set(cellWidth);
	}

	
	
	/**
	 * the cell height of masonry grid
	 */
	private DoubleProperty cellHeight = new SimpleDoubleProperty(70);
	public final DoubleProperty cellHeightProperty() {
		return this.cellHeight;
	}
	/**
	 * @return the cell height of the masonry pane grid
	 */
	public final double getCellHeight() {
		return this.cellHeightProperty().get();
	}
	/**
	 * sets the cell height of the masonry pane grid
	 * @param cellHeight of the grid
	 */
	public final void setCellHeight(final double cellHeight) {
		this.cellHeightProperty().set(cellHeight);
	}

	
	
	/**
	 * horizontal spacing between nodes in grid 
	 */
	private DoubleProperty hSpacing = new SimpleDoubleProperty(5);
	public final DoubleProperty hSpacingProperty() {
		return this.hSpacing;
	}
	/**
	 * @return the horizontal spacing between nodes in the grid
	 */
	public final double getHSpacing() {
		return this.hSpacingProperty().get();
	}
	/**
	 * sets the horizontal spacing in the grid
	 * @param spacing horizontal spacing
	 */
	public final void setHSpacing(final double spacing) {
		this.hSpacingProperty().set(spacing);
	}

	
	
	/**
	 * vertical spacing between nodes in the grid 
	 */
	private DoubleProperty vSpacing = new SimpleDoubleProperty(5);
	public final DoubleProperty vSpacingProperty() {
		return this.vSpacing;
	}
	/**
	 * @return the vertical spacing between nodes in the grid
	 */
	public final double getVSpacing() {
		return this.vSpacingProperty().get();
	}
	/**
	 * sets the vertical spacing in the grid
	 * @param spacing vertical spacing
	 */
	public final void setVSpacing(final double spacing) {
		this.vSpacingProperty().set(spacing);
	}

	
	
	/**
	 * limit the grid columns to certain number
	 */
	private IntegerProperty limitColumn = new SimpleIntegerProperty(-1);
	public final IntegerProperty limitColumnProperty() {
		return this.limitColumn;
	}
	/**
	 * @return -1 if no limit on grid columns, else returns the maximum number of columns
	 * to be used in the grid
	 */
	public final int getLimitColumn() {
		return this.limitColumnProperty().get();
	}
	/**
	 * sets the column limit to be used in the grid
	 * @param limitColumn number of colummns to be used in the grid
	 */
	public final void setLimitColumn(final int limitColumn) {
		this.limitColumnProperty().set(limitColumn);
	}

	
	
	/**
	 * limit the grid rows to certain number
	 */
	private IntegerProperty limitRow = new SimpleIntegerProperty(-1);
	public final IntegerProperty limitRowProperty() {
		return this.limitRow;
	}
	/**
	 * @return -1 if no limit on grid rows, else returns the maximum number of rows
	 * to be used in the grid
	 */
	public final int getLimitRow() {
		return this.limitRowProperty().get();
	}
	/**
	 * sets the rows limit to be used in the grid
	 * @param limitRow number of rows to be used in the grid
	 */
	public final void setLimitRow(final int limitRow) {
		this.limitRowProperty().set(limitRow);
	}


	/***************************************************************************
	 *                                                                         *
	 * Layout Modes                                                            *
	 *                                                                         *
	 **************************************************************************/	

	public static abstract class LayoutMode {
		public static final MasonryLayout MASONRY = new MasonryLayout();
		public static final BinPackingLayout BIN_PACKING = new BinPackingLayout();
		
		protected abstract List<BoundingBox> fillGrid(int[][] matrix, List<Region> children,  double cellWidth, double cellHeight, int limitRow, int limitCol, double gutterX, double gutterY);

		/**
		 * returns the available box at the cell (x,y) of the grid that fits the block if existed
		 * @param x
		 * @param y
		 * @param block
		 * @return
		 */
		protected BoundingBox getFreeArea(int[][] matrix, int x, int y, Region block, double cellWidth, double cellHeight, int limitRow, int limitCol, double gutterX, double gutterY){
			double blockHeight = getBLockHeight(block);
			double blockWidth = getBLockWidth(block);
			
			int rowsNeeded = (int)Math.ceil(blockHeight/(cellHeight + gutterY));
			if(cellHeight*rowsNeeded + (rowsNeeded - 1)*2*gutterY < blockHeight) rowsNeeded++;		
			int maxRow = Math.min(x + rowsNeeded, limitRow);
			
			int colsNeeded = (int)Math.ceil(blockWidth/(cellWidth + gutterX));
			if(cellWidth*colsNeeded + (colsNeeded - 1)*2*gutterX < blockWidth) colsNeeded++;			
			int maxCol = Math.min(y + colsNeeded, limitCol);
			
			int minRow = maxRow;
			int minCol = maxCol;
			for(int i = x; i < minRow; i++){
				for(int j = y; j< maxCol; j++){
					if(matrix[i][j] !=0){
						if(y < j && j < minCol) minCol = j;
					}
				}
			}
			for(int i = x; i < maxRow; i++){
				for(int j = y; j< minCol; j++){
					if(matrix[i][j] !=0){
						if(x < i && i < minRow) minRow = i;
					}
				}
			}    	
			return new BoundingBox(x, y, minCol - y, minRow - x );    	
		}

		protected double getBLockWidth(Region region){
			if(region.getMinWidth()!= -1) return region.getMinWidth();
			if(region.getPrefWidth() != USE_COMPUTED_SIZE) return region.getPrefWidth();
			else return region.prefWidth(-1);
		}

		protected double getBLockHeight(Region region){
			if(region.getMinHeight()!= -1) return region.getMinHeight();
			if(region.getPrefHeight() != USE_COMPUTED_SIZE) return region.getPrefHeight();
			else return region.prefHeight(getBLockWidth(region));
		}

		protected boolean validWidth(BoundingBox box , Region region, double cellW, double gutterX, double gutterY){
			boolean valid = false;
			if(region.getMinWidth() != -1 && box.getWidth()*cellW + (box.getWidth()-1)*2*gutterX < region.getMinWidth()) return false;

			if(region.getPrefWidth() == USE_COMPUTED_SIZE && box.getWidth()*cellW + (box.getWidth()-1)*2*gutterX >= region.prefWidth(-1))
				valid = true;
			if(region.getPrefWidth() != USE_COMPUTED_SIZE && box.getWidth()*cellW + (box.getWidth()-1)*2*gutterX >= region.getPrefWidth())
				valid = true;
			return valid;		
		}

		protected boolean validHeight(BoundingBox box , Region region, double cellH, double gutterX, double gutterY){
			boolean valid = false;
			if(region.getMinHeight() != -1 && box.getHeight()*cellH + (box.getHeight()-1)*2*gutterY < region.getMinHeight()) return false;

			if(region.getPrefHeight() == USE_COMPUTED_SIZE && box.getHeight()*cellH + (box.getHeight()-1)*2*gutterY >= region.prefHeight(region.prefWidth(-1)))
				valid = true;
			if(region.getPrefHeight() != USE_COMPUTED_SIZE && box.getHeight()*cellH + (box.getHeight()-1)*2*gutterY >= region.getPrefHeight())
				valid = true;
			return valid;		
		}

		protected int[][] fillMatrix(int[][] matrix, int id, double row, double col, double width, double height){
			//			int maxCol = (int) col;
			//			int maxRow = (int) row;
			for(int x = (int)row; x < row + height;x++){
				for(int y = (int)col; y < col + width;y++){
					matrix[x][y] = id;
					//					if(++y > maxCol) maxCol = y;
				}   
				//				if(++x > maxRow) maxRow = x;
			}
			return matrix;
		}

	}

	/***************************************************************************
	 *                                                                         *
	 * Masonry Layout                                                          *
	 *                                                                         *
	 **************************************************************************/

	private static class MasonryLayout extends LayoutMode {
		@Override
		public List<BoundingBox> fillGrid(int[][] matrix, List<Region> children,  double cellWidth, double cellHeight, int limitRow, int limitCol, double gutterX, double gutterY) {
			int row = matrix.length;
			if(row <= 0) return null;
			int col = matrix[0].length;
			List<BoundingBox> boxes = new ArrayList<>();

			for (int b = 0; b < children.size(); b++) {
				Region block = (Region) children.get(b);
				// for debugging purpose
//								if(!(block instanceof GridPane)){
				for (int i = 0; i < row;i++) {						
					int old = boxes.size();
					for(int j = 0 ; j < col; j++){
						if(matrix[i][j] != 0) continue;

						// masonry condition
						boolean isValidCell = true;
						for(int k = i+1 ; k < row ; k++){
							if(matrix[k][j] !=0) {
								isValidCell = false;
								break;
							}
						}
						if(!isValidCell) continue;

						BoundingBox box = getFreeArea(matrix, i, j, block, cellWidth, cellHeight, limitRow, limitCol, gutterX, gutterY);
						if(!validWidth(box, block, cellWidth, gutterX, gutterY) || !validHeight(box,block, cellHeight, gutterX, gutterY))
							continue;
						matrix = fillMatrix(matrix, b+1, box.getMinX(), box.getMinY(), box.getWidth(), box.getHeight());
						boxes.add(box);
						break;
					}
					if(boxes.size()!=old) break;
					if(i == row - 1){
						boxes.add(null);
					}
				}
//								}
			}
			return boxes;
		}
	}

	/***************************************************************************
	 *                                                                         *
	 * Bin Packing Layout                                                      *
	 *                                                                         *
	 **************************************************************************/
	private static class BinPackingLayout extends LayoutMode {
		@Override
		public List<BoundingBox> fillGrid(int[][] matrix, List<Region> children, double cellWidth, double cellHeight, int limitRow, int limitCol, double gutterX, double gutterY) {
			int row = matrix.length;
			if(row <= 0) return null;
			int col = matrix[0].length;
			List<BoundingBox> boxes = new ArrayList<>();

			for (int b = 0; b < children.size(); b++) {
				Region block = children.get(b);
//								if(!(block instanceof GridPane)){
				for (int i = 0; i < row;i++) {
					int old = boxes.size();
					for(int j = 0 ; j < col; j++){
						if(matrix[i][j] != 0) continue;
						BoundingBox box = getFreeArea(matrix, i, j, block, cellWidth, cellHeight, limitRow, limitCol, gutterX, gutterY);							
						if(!validWidth(box, block, cellWidth, gutterX, gutterY) || !validHeight(box,block, cellHeight, gutterX, gutterY))
							continue;
						matrix = fillMatrix(matrix, b+1, box.getMinX(), box.getMinY(), box.getWidth(), box.getHeight());
						boxes.add(box);
						break;
					}
					if(boxes.size()!=old) break;
					if(i == row - 1){
						boxes.add(null);
					}
				}
//								}
			}
			return boxes;
		}
	}

}
