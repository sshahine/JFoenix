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

package com.jfoenix.responsive;

import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Responsive handler will scan all nodes in the scene and add a certain
 * pseudo class (style class) to them according to the device ( screen size )
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXResponsiveHandler {

    public static final PseudoClass PSEUDO_CLASS_EX_SMALL = PseudoClass.getPseudoClass("extreme-small-device");
    public static final PseudoClass PSEUDO_CLASS_SMALL = PseudoClass.getPseudoClass("small-device");
    public static final PseudoClass PSEUDO_CLASS_MEDIUM = PseudoClass.getPseudoClass("medium-device");
    public static final PseudoClass PSEUDO_CLASS_LARGE = PseudoClass.getPseudoClass("large-device");

    /**
     * Construct a responsive handler for a specified Stage and css class.
     * <p>
     * Device css classes can be one of the following:
     * <ul>
     * <li>{@link JFXResponsiveHandler#PSEUDO_CLASS_EX_SMALL}</li>
     * <li>{@link JFXResponsiveHandler#PSEUDO_CLASS_LARGE}</li>
     * <li>{@link JFXResponsiveHandler#PSEUDO_CLASS_MEDIUM}</li>
     * <li>{@link JFXResponsiveHandler#PSEUDO_CLASS_SMALL}</li>
     * </ul>
     * <p>
     * <b>Note:</b> the css class must be chosen by the user according to a device
     * detection methodology
     *
     * @param stage       the JavaFX Application stage
     * @param pseudoClass css class for certain device
     */
    public JFXResponsiveHandler(Stage stage, PseudoClass pseudoClass) {
        scanAllNodes(stage.getScene().getRoot(), pseudoClass);
    }

    /**
     * scans all nodes in the scene and apply the css pseduoClass to them.
     *
     * @param parent      stage parent node
     * @param pseudoClass css class for certain device
     */
    private void scanAllNodes(Parent parent, PseudoClass pseudoClass) {
        parent.getChildrenUnmodifiable().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> c) {
                while (c.next()) {
                    if (!c.wasPermutated() && !c.wasUpdated()) {
                        for (Node addedNode : c.getAddedSubList()) {
                            if (addedNode instanceof Parent) {
                                scanAllNodes((Parent) addedNode, pseudoClass);
                            }
                        }
                    }
                }
            }
        });
        for (Node component : parent.getChildrenUnmodifiable()) {
            if (component instanceof Pane) {
                ((Pane) component).getChildren().addListener(new ListChangeListener<Node>() {
                    @Override
                    public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> c) {
                        while (c.next()) {
                            if (!c.wasPermutated() && !c.wasUpdated()) {
                                for (Node addedNode : c.getAddedSubList()) {
                                    if (addedNode instanceof Parent) {
                                        scanAllNodes((Parent) addedNode, pseudoClass);
                                    }
                                }
                            }
                        }
                    }
                });
                //if the component is a container, scan its children
                scanAllNodes((Pane) component, pseudoClass);
            } else if (component instanceof ScrollPane) {
                ((ScrollPane) component).contentProperty().addListener((o, oldVal, newVal) -> {
                    scanAllNodes((Parent) newVal, pseudoClass);
                });
                //if the component is a container, scan its children
                if (((ScrollPane) component).getContent() instanceof Parent) {

                    scanAllNodes((Parent) ((ScrollPane) component).getContent(), pseudoClass);
                }
            } else if (component instanceof Control) {
                //if the component is an instance of IInputControl, add to list
                component.pseudoClassStateChanged(PSEUDO_CLASS_EX_SMALL,
                    pseudoClass == PSEUDO_CLASS_EX_SMALL);
                component.pseudoClassStateChanged(PSEUDO_CLASS_SMALL, pseudoClass == PSEUDO_CLASS_SMALL);
                component.pseudoClassStateChanged(PSEUDO_CLASS_MEDIUM, pseudoClass == PSEUDO_CLASS_MEDIUM);
                component.pseudoClassStateChanged(PSEUDO_CLASS_LARGE, pseudoClass == PSEUDO_CLASS_LARGE);
            }
        }
    }


}
