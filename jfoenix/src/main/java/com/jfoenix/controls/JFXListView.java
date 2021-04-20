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

package com.jfoenix.controls;

import com.jfoenix.skins.JFXListViewSkin;
import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.SizeConverter;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;

import java.util.*;

/**
 * Material design implementation of List View
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXListView<T> extends ListView<T> {

    /**
     * {@inheritDoc}
     */
    public JFXListView() {
        this.setCellFactory(listView -> new JFXListCell<>());
        initialize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXListViewSkin<>(this);
    }

    private ObjectProperty<Integer> depthProperty = new SimpleObjectProperty<>(0);

    public ObjectProperty<Integer> depthProperty() {
        return depthProperty;
    }

    public int getDepth() {
        return depthProperty.get();
    }

    public void setDepth(int depth) {
        depthProperty.set(depth);
    }

    private ReadOnlyDoubleWrapper currentVerticalGapProperty = new ReadOnlyDoubleWrapper();

    ReadOnlyDoubleProperty currentVerticalGapProperty() {
        return currentVerticalGapProperty.getReadOnlyProperty();
    }

    private void expand() {
        currentVerticalGapProperty.set(verticalGap.get());
    }

    private void collapse() {
        currentVerticalGapProperty.set(0);
    }

    /*
     * this only works if the items were labels / strings
     */
    private BooleanProperty showTooltip = new SimpleBooleanProperty(false);

    public final BooleanProperty showTooltipProperty() {
        return this.showTooltip;
    }

    public final boolean isShowTooltip() {
        return this.showTooltipProperty().get();
    }

    public final void setShowTooltip(final boolean showTooltip) {
        this.showTooltipProperty().set(showTooltip);
    }

    /***************************************************************************
     *                                                                         *
     * SubList Properties                                                      *
     *                                                                         *
     **************************************************************************/

    @Deprecated
    private ObjectProperty<Node> groupnode = new SimpleObjectProperty<>(new Label("GROUP"));

    @Deprecated
    public Node getGroupnode() {
        return groupnode.get();
    }

    @Deprecated
    public void setGroupnode(Node node) {
        this.groupnode.set(node);
    }

    /*
     *  selected index property that includes the sublists
     */
    @Deprecated
    private ReadOnlyObjectWrapper<Integer> overAllIndexProperty = new ReadOnlyObjectWrapper<>(-1);

    @Deprecated
    public ReadOnlyObjectProperty<Integer> overAllIndexProperty() {
        return overAllIndexProperty.getReadOnlyProperty();
    }

    // private sublists property
    @Deprecated
    private ObjectProperty<ObservableList<JFXListView<?>>> sublistsProperty = new SimpleObjectProperty<>(
        FXCollections.observableArrayList());
    @Deprecated
    private LinkedHashMap<Integer, JFXListView<?>> sublistsIndices = new LinkedHashMap<>();

    // this method shouldn't be called from user
    @Deprecated
    void addSublist(JFXListView<?> subList, int index) {
        if (!sublistsProperty.get().contains(subList)) {
            sublistsProperty.get().add(subList);
            sublistsIndices.put(index, subList);
            subList.getSelectionModel().selectedIndexProperty().addListener((o, oldVal, newVal) -> {
                if (newVal.intValue() != -1) {
                    updateOverAllSelectedIndex();
                }
            });
        }
    }

    private void updateOverAllSelectedIndex() {
        // if item from the list is selected
        if (this.getSelectionModel().getSelectedIndex() != -1) {
            int selectedIndex = this.getSelectionModel().getSelectedIndex();
            Iterator<Map.Entry<Integer, JFXListView<?>>> itr = sublistsIndices.entrySet().iterator();
            int preItemsSize = 0;
            while (itr.hasNext()) {
                Map.Entry<Integer, JFXListView<?>> entry = itr.next();
                if (entry.getKey() < selectedIndex) {
                    preItemsSize += entry.getValue().getItems().size() - 1;
                }
            }
            overAllIndexProperty.set(selectedIndex + preItemsSize);
        } else {
            Iterator<Map.Entry<Integer, JFXListView<?>>> itr = sublistsIndices.entrySet().iterator();
            ArrayList<Object> selectedList = new ArrayList<>();
            while (itr.hasNext()) {
                Map.Entry<Integer, JFXListView<?>> entry = itr.next();
                if (entry.getValue().getSelectionModel().getSelectedIndex() != -1) {
                    selectedList.add(entry.getKey());
                }
            }
            if (selectedList.size() > 0) {
                itr = sublistsIndices.entrySet().iterator();
                int preItemsSize = 0;
                while (itr.hasNext()) {
                    Map.Entry<Integer, JFXListView<?>> entry = itr.next();
                    if (entry.getKey() < ((Integer) selectedList.get(0))) {
                        preItemsSize += entry.getValue().getItems().size() - 1;
                    }
                }
                overAllIndexProperty.set(preItemsSize + (Integer) selectedList.get(0) + sublistsIndices.get(selectedList
                    .get(0))
                    .getSelectionModel()
                    .getSelectedIndex());
            } else {
                overAllIndexProperty.set(-1);
            }
        }
    }


    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    /**
     * Initialize the style class to 'jfx-list-view'.
     * <p>
     * This is the selector class from which CSS can be used to style
     * this control.
     */
    private static final String DEFAULT_STYLE_CLASS = "jfx-list-view";

    private void initialize() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
        expanded.addListener((o, oldVal, newVal) -> {
            if (newVal) {
                expand();
            } else {
                collapse();
            }
        });

        verticalGap.addListener((o, oldVal, newVal) -> {
            if (isExpanded()) {
                expand();
            } else {
                collapse();
            }
        });

        // handle selection model on the list ( FOR NOW : we only support single selection on the list if it contains sublists)
        sublistsProperty.get().addListener((ListChangeListener.Change<? extends JFXListView<?>> c) -> {
            while (c.next()) {
                if (c.wasAdded() || c.wasUpdated() || c.wasReplaced()) {
                    if (sublistsProperty.get().size() == 1) {
                        this.getSelectionModel()
                            .selectedItemProperty()
                            .addListener((o, oldVal, newVal) -> clearSelection(this));
                        // prevent selecting the sublist item by clicking the right mouse button
                        this.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
                    }
                    c.getAddedSubList()
                        .forEach(item -> item.getSelectionModel()
                            .selectedItemProperty()
                            .addListener((o, oldVal, newVal) -> clearSelection(item)));
                }
            }
        });

        // listen to index changes
        this.getSelectionModel().selectedIndexProperty().addListener((o, oldVal, newVal) -> {
            if (newVal.intValue() != -1) {
                updateOverAllSelectedIndex();
            }
        });
    }


    // allow single selection across the list and all sublits
    private boolean allowClear = true;

    private void clearSelection(JFXListView<?> selectedList) {
        if (allowClear) {
            allowClear = false;
            if (this != selectedList) {
                this.getSelectionModel().clearSelection();
            }
            for (int i = 0; i < sublistsProperty.get().size(); i++) {
                if (sublistsProperty.get().get(i) != selectedList) {
                    sublistsProperty.get().get(i).getSelectionModel().clearSelection();
                }
            }
            allowClear = true;
        }
    }

    /**
     * propagate mouse events to the parent node ( e.g. to allow dragging while clicking on the list)
     */
    public void propagateMouseEventsToParent() {
        this.addEventHandler(MouseEvent.ANY, e -> {
            e.consume();
            this.getParent().fireEvent(e);
        });
    }

    private StyleableDoubleProperty verticalGap = new SimpleStyleableDoubleProperty(StyleableProperties.VERTICAL_GAP,
        JFXListView.this,
        "verticalGap",
        0.0);

    public Double getVerticalGap() {
        return verticalGap == null ? 0 : verticalGap.get();
    }

    public StyleableDoubleProperty verticalGapProperty() {
        return this.verticalGap;
    }

    public void setVerticalGap(Double gap) {
        this.verticalGap.set(gap);
    }

    private StyleableBooleanProperty expanded = new SimpleStyleableBooleanProperty(StyleableProperties.EXPANDED,
        JFXListView.this,
        "expanded",
        false);

    public Boolean isExpanded() {
        return expanded != null && expanded.get();
    }

    public StyleableBooleanProperty expandedProperty() {
        return this.expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded.set(expanded);
    }

    private static class StyleableProperties {
        private static final CssMetaData<JFXListView<?>, Number> VERTICAL_GAP =
            new CssMetaData<JFXListView<?>, Number>("-jfx-vertical-gap",
                SizeConverter.getInstance(), 0) {
                @Override
                public boolean isSettable(JFXListView<?> control) {
                    return control.verticalGap == null || !control.verticalGap.isBound();
                }

                @Override
                public StyleableDoubleProperty getStyleableProperty(JFXListView<?> control) {
                    return control.verticalGapProperty();
                }
            };
        private static final CssMetaData<JFXListView<?>, Boolean> EXPANDED =
            new CssMetaData<JFXListView<?>, Boolean>("-jfx-expanded",
                BooleanConverter.getInstance(), false) {
                @Override
                public boolean isSettable(JFXListView<?> control) {
                    // it's only settable if the List is not shown yet
                    return control.getHeight() == 0 && (control.expanded == null || !control.expanded.isBound());
                }

                @Override
                public StyleableBooleanProperty getStyleableProperty(JFXListView<?> control) {
                    return control.expandedProperty();
                }
            };
        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<>(ListView.getClassCssMetaData());
            Collections.addAll(styleables, VERTICAL_GAP, EXPANDED);
            CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.CHILD_STYLEABLES;
    }

}
