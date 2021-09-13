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

package com.jfoenix.controls.behavior;

import com.sun.javafx.scene.control.behavior.TreeTableCellBehavior;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;

/**
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXTreeTableCellBehavior<S, T> extends TreeTableCellBehavior<S, T> {

    public JFXTreeTableCellBehavior(TreeTableCell<S, T> control) {
        super(control);
    }

    @Override
    protected boolean handleDisclosureNode(double x, double y) {
        final TreeItem<S> treeItem = getControl().getTreeTableRow().getTreeItem();
        if (!treeItem.isLeaf()) {
            final Node disclosureNode = getControl().getTreeTableRow().getDisclosureNode();
            if (disclosureNode != null) {
                if (disclosureNode.getBoundsInParent().contains(x + disclosureNode.getTranslateX(), y)) {
                    if (treeItem != null) {
                        treeItem.setExpanded(!treeItem.isExpanded());
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
