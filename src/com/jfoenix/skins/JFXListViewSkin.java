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

import com.jfoenix.controls.JFXListView;
import com.jfoenix.effects.JFXDepthManager;
import com.sun.javafx.scene.control.skin.ListViewSkin;


public class JFXListViewSkin<T> extends  ListViewSkin<T>{

    public JFXListViewSkin(final JFXListView<T> listView) {
        super(listView);
        JFXDepthManager.setDepth(flow, listView.depthProperty().get());
        listView.depthProperty().addListener((o,oldVal,newVal)->JFXDepthManager.setDepth(flow, newVal));
        flow.setCreateCell(flow1 -> JFXListViewSkin.this.createCell());
    }
    
    @Override protected void layoutChildren(final double x, final double y,
            final double w, final double h) {
    	super.layoutChildren(x, y, w, h);
    	if (getItemCount() != 0) {
    		 double estimatedHeight = estimateHeight();
             if(flow.getCellCount() > 0 && estimatedHeight < getSkinnable().getPrefHeight())
             	getSkinnable().setPrefHeight(estimatedHeight); 
    	}
    }
    
    @Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
    	if (getSkinnable().getItems().size() <= 0) return 200;
    	// handel if the list has border
        return estimateHeight();
    }
    
    private double estimateHeight(){
    	// compute the border/padding for the list
    	double borderWidth = 0;
        if(getSkinnable().getBorder()!=null) {
        	borderWidth += Math.ceil(getSkinnable().getBorder().getStrokes().get(0).getWidths().getTop());
        	borderWidth += Math.ceil(getSkinnable().getBorder().getStrokes().get(0).getWidths().getBottom());
        }
        if(getSkinnable().getPadding()!=null){
        	borderWidth += getSkinnable().getPadding().getTop();
        	borderWidth += getSkinnable().getPadding().getBottom();
        }
        // compute the gap between list cells
    	double gap = ((JFXListView<T>) getSkinnable()).currentVerticalGapProperty().get() * (getSkinnable().getItems().size() - 1);
        // compute the height of each list cell
    	double cellsHeight = 0;
    	for(int i = 0 ; i < flow.getCellCount(); i++){
    		double cellBorderWidth = 0;
    		if(flow.getCell(i).getBorder()!=null){
    			cellBorderWidth += flow.getCell(i).getBorder().getStrokes().get(0).getWidths().getTop();
    			cellBorderWidth += flow.getCell(i).getBorder().getStrokes().get(0).getWidths().getBottom();
    		}
    		cellsHeight+= flow.getCell(i).getHeight() + cellBorderWidth;
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
