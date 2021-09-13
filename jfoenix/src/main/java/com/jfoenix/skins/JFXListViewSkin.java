/*
 * Copyright (c) 2016 JFoenix
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.jfoenix.skins;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.effects.JFXDepthManager;
import com.sun.javafx.scene.control.skin.ListViewSkin;
import javafx.application.Platform;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Region;

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
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return 200;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        final int itemsCount = getSkinnable().getItems().size();
        if (getSkinnable().maxHeightProperty().isBound() || itemsCount <= 0) {
            return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
        }

        final double fixedCellSize = getSkinnable().getFixedCellSize();
        double computedHeight = fixedCellSize != Region.USE_COMPUTED_SIZE ?
            fixedCellSize * itemsCount + snapVerticalInsets() : estimateHeight();
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
        double borderWidth = snapVerticalInsets();
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

    private double snapVerticalInsets() {
        return getSkinnable().snappedBottomInset() + getSkinnable().snappedTopInset();
    }

}
