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

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.sun.javafx.scene.control.skin.TreeTableRowSkin;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.TreeTableRow;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Shadi Shaheen
 */
public class JFXTreeTableRowSkin<T> extends TreeTableRowSkin<T> {

    private static final PseudoClass groupedClass = PseudoClass.getPseudoClass("grouped");

    static Map<Control, Double> disclosureWidthMap = null;

    public JFXTreeTableRowSkin(TreeTableRow<T> control) {
        super(control);

        if (disclosureWidthMap == null) {
            try {
                Field declaredField = getClass().getSuperclass()
                    .getSuperclass()
                    .getDeclaredField("maxDisclosureWidthMap");
                declaredField.setAccessible(true);
                disclosureWidthMap = (Map<Control, Double>) declaredField.get(this);
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
                ex.printStackTrace();
            }
        }

        // clear disclosure node indentation after grouping/ungrouping
        getSkinnable().getTreeTableView().rootProperty().addListener((o, oldVal, newVal) -> {
            disclosureWidthMap.remove(getSkinnable().getTreeTableView());
        });

        // allow custom skin to grouped rows
        getSkinnable().itemProperty().addListener(observable -> {
            T item = getSkinnable().getItem();
            pseudoClassStateChanged(groupedClass, item != null
                                                  && item instanceof RecursiveTreeObject
                                                  && item.getClass() == RecursiveTreeObject.class);
        });
    }

    @Override
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        super.layoutChildren(x, y, w, h);
        if (getSkinnable().getIndex() > -1 && getSkinnable().getTreeTableView()
                                                  .getTreeItem(getSkinnable().getIndex()) != null) {

            // disclosure row case
            if (getSkinnable().getTreeItem() != null && !getSkinnable().getTreeItem().isLeaf()) {
                Node arrow = ((Parent) getDisclosureNode()).getChildrenUnmodifiable().get(0);
                // relocating the disclosure node according to the grouping column
                final Parent arrowParent = arrow.getParent();
                if (((RecursiveTreeObject<?>) getSkinnable().getItem()).getGroupedColumn() != null) {
                    Node col = getChildren().get(getSkinnable().getTreeTableView()
                                                     .getTreeItemLevel(getSkinnable().getTreeItem()) + 1);
                    if (getSkinnable().getItem() instanceof RecursiveTreeObject) {
                        int index = getSkinnable().getTreeTableView()
                            .getColumns()
                            .indexOf(((RecursiveTreeObject<?>) getSkinnable().getItem()).getGroupedColumn());
                        //						getSkinnable().getTreeTableView().getColumns().get(index).getText();
                        col = getChildren().get(index + 1); // index + 2 , if the rippler was added
                    }
                    arrowParent.setTranslateX(col.getBoundsInParent().getMinX());
                    arrowParent.setLayoutX(0);
                } else {
                    arrowParent.setTranslateX(0);
                    arrowParent.setLayoutX(0);
                }
            }
        }
    }

    @Override
    protected double getIndentationPerLevel() {
        return super.getIndentationPerLevel();
    }
}
