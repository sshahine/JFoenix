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
package com.jfoenix.skins;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.effects.JFXDepthManager;
import com.sun.javafx.scene.control.skin.ListViewSkin;

import javafx.application.Platform;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.ListCell;

/**
 * <h1>Material Design ListView Skin</h1>
 *
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2016-03-09
 */
public class JFXListViewSkin<T> extends  ListViewSkin<T>{

    public JFXListViewSkin(final JFXListView<T> listView) {
        super(listView);
        JFXDepthManager.setDepth(flow, listView.depthProperty().get());
        listView.depthProperty().addListener((o,oldVal,newVal)->JFXDepthManager.setDepth(flow, newVal));
        listView.getItems().addListener((Change<? extends T> change)->{
        	new Thread(()->{
        		try {
					Thread.sleep(20);
				} catch (Exception e) {
					e.printStackTrace();
				}
        		Platform.runLater(()->getSkinnable().requestLayout());
        	}).start();
        });
//      flow.setCreateCell(flow1 -> JFXListViewSkin.this.createCell());        
    }
    
//    @Override protected void layoutChildren(final double x, final double y,
//            final double w, final double h) {
//    	super.layoutChildren(x, y, w, h);
////    	if (getItemCount() != 0) {
////    		 double estimatedHeight = estimateHeight();
////             if(flow.getCellCount() > 0 && estimatedHeight < getSkinnable().getPrefHeight())
////             	getSkinnable().setPrefHeight(estimatedHeight); 
////    	}
//    }
    
    @Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
    	if(getSkinnable().maxHeightProperty().isBound() || getSkinnable().getItems().size() <= 0) return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
    	if(getSkinnable().getMaxHeight() > 0) return getSkinnable().getMaxHeight();

    	double computedHeight = estimateHeight();
    	double height = super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);    	
    	if(height > computedHeight)
    		height = computedHeight;
    
        return height;
    }
    
    private double estimateHeight(){
    	// compute the border/padding for the list
    	double borderWidth = getSkinnable().snappedBottomInset() + getSkinnable().snappedTopInset();
//        if(getSkinnable().getBorder()!=null) {
//        	borderWidth += Math.ceil(getSkinnable().getBorder().getStrokes().get(0).getWidths().getTop());
//        	borderWidth += Math.ceil(getSkinnable().getBorder().getStrokes().get(0).getWidths().getBottom());
//        }
//        if(getSkinnable().getPadding()!=null){
//        	borderWidth += getSkinnable().getPadding().getTop();
//        	borderWidth += getSkinnable().getPadding().getBottom();
//        }
        // compute the gap between list cells
    	
    	JFXListView<T> listview = ((JFXListView<T>) getSkinnable());
    	double gap = listview.isExpanded()? ((JFXListView<T>) getSkinnable()).getVerticalGap() * (getSkinnable().getItems().size()) : 0;
        // compute the height of each list cell
    	double cellsHeight = 0;
    	for(int i = 0 ; i < flow.getCellCount(); i++){
    		ListCell<T> cell = flow.getCell(i);
//    		double cellBorderWidth = 0;
//    		if(flow.getCell(i).getBorder()!=null){
//    			cellBorderWidth += flow.getCell(i).getBorder().getStrokes().get(0).getWidths().getTop();
//    			cellBorderWidth += flow.getCell(i).getBorder().getStrokes().get(0).getWidths().getBottom();
//    		}
    		cellsHeight+= cell.getHeight();
    	}
//    	double cellsHeight = IntStream.range(0, flow.getCellCount()).mapToDouble(index->{
//    		double cellBorderWidth = 0;
//    		if(flow.getCell(index).getBorder()!=null){
//    			cellBorderWidth += flow.getCell(index).getBorder().getStrokes().get(0).getWidths().getTop();
//    			cellBorderWidth += flow.getCell(index).getBorder().getStrokes().get(0).getWidths().getBottom();
//    		}
//    		return flow.getCell(index).getHeight() + cellBorderWidth;
//    	}).sum();
    	return cellsHeight + gap + borderWidth;
    }
    
    
    
}
