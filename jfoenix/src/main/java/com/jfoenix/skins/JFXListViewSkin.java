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
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXListViewSkin<T> extends ListViewSkin<T> {

    public JFXListViewSkin(final JFXListView<T> listView) {
        super(listView);
        JFXDepthManager.setDepth(flow, listView.depthProperty().get());
        listView.depthProperty().addListener((o, oldVal, newVal) -> JFXDepthManager.setDepth(flow, newVal));
        listView.getItems().addListener((Change<? extends T> change) -> new Thread(() -> {
            try {
                Thread.sleep(20);
            } catch (InterruptedException intEx) {
                intEx.printStackTrace();
            }
            Platform.runLater(() -> getSkinnable().requestLayout());
        }).start());
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (getSkinnable().maxHeightProperty().isBound() || getSkinnable().getItems().size() <= 0) {
            return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
        }

        double computedHeight = estimateHeight();
        double height = super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
        if (height > computedHeight) {
            height = computedHeight;
        }

        if (getSkinnable().getMaxHeight() > 0 && computedHeight > getSkinnable().getMaxHeight()) {
            return getSkinnable().getMaxHeight();
        }

        return height;
    }

    private double estimateHeight() {
        // compute the border/padding for the list
        double borderWidth = getSkinnable().snappedBottomInset() + getSkinnable().snappedTopInset();
        // compute the gap between list cells

        JFXListView<T> listview = (JFXListView<T>) getSkinnable();
        double gap = listview.isExpanded() ? ((JFXListView<T>) getSkinnable()).getVerticalGap() * (getSkinnable().getItems()
            .size()) : 0;
        // compute the height of each list cell
        double cellsHeight = 0;
        for (int i = 0; i < flow.getCellCount(); i++) {
            ListCell<T> cell = flow.getCell(i);

            cellsHeight += cell.getHeight();
        }
        return cellsHeight + gap + borderWidth;
    }

}
