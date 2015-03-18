package com.cctintl.c3dfx.skins;


import java.security.AccessController;
import java.security.PrivilegedAction;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.FocusModel;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import com.cctintl.c3dfx.controls.C3DListView;
import com.cctintl.c3dfx.controls.DepthManager;
import com.cctintl.c3dfx.javacomponents.VirtualContainerBase;
import com.sun.javafx.scene.control.behavior.ListViewBehavior;
import com.sun.javafx.scene.control.skin.resources.ControlResources;


public class C3DListViewSkin<T> extends  VirtualContainerBase<ListView<T>, ListViewBehavior<T>, ListCell<T>> {
    
    /**
     * Region placed over the top of the flow (and possibly the header row) if
     * there is no data.
     */
    // FIXME this should not be a StackPane
    private StackPane placeholderRegion;
    private Node placeholderNode;
//    private Label placeholderLabel;
    private static final String EMPTY_LIST_TEXT = ControlResources.getString("ListView.noContent");

    // RT-34744 : IS_PANNABLE will be false unless
    // com.sun.javafx.scene.control.skin.ListViewSkin.pannable
    // is set to true. This is done in order to make ListView functional
    // on embedded systems with touch screens which do not generate scroll
    // events for touch drag gestures.
    private static final boolean IS_PANNABLE =
            AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> Boolean.getBoolean("com.sun.javafx.scene.control.skin.ListViewSkin.pannable"));

    private ObservableList<T> listViewItems;

    public C3DListViewSkin(final C3DListView<T> listView) {
        super(listView, new ListViewBehavior<T>(listView));

        DepthManager.setDepth(flow, listView.depthProperty().get());
        listView.depthProperty().addListener((o,oldVal,newVal)->DepthManager.setDepth(flow, newVal));
        
        updateListViewItems();

        // init the VirtualFlow
        flow.setId("virtual-flow");
        flow.setPannable(IS_PANNABLE);
        flow.setVertical(getSkinnable().getOrientation() == Orientation.VERTICAL);
        flow.setCreateCell(flow1 -> C3DListViewSkin.this.createCell());
        flow.setFixedCellSize(listView.getFixedCellSize());
        getChildren().add(flow);
        
        EventHandler<MouseEvent> ml = event -> {
            // RT-15127: cancel editing on scroll. This is a bit extreme
            // (we are cancelling editing on touching the scrollbars).
            // This can be improved at a later date.
            if (listView.getEditingIndex() > -1) {
                listView.edit(-1);
            }

            // This ensures that the list maintains the focus, even when the vbar
            // and hbar controls inside the flow are clicked. Without this, the
            // focus border will not be shown when the user interacts with the
            // scrollbars, and more importantly, keyboard navigation won't be
            // available to the user.
            if (listView.isFocusTraversable()) {
                listView.requestFocus();
            }
        };
        flow.getVbar().addEventFilter(MouseEvent.MOUSE_PRESSED, ml);
        flow.getHbar().addEventFilter(MouseEvent.MOUSE_PRESSED, ml);
        
        updateRowCount();

        // init the behavior 'closures'
        getBehavior().setOnFocusPreviousRow(() -> { onFocusPreviousCell(); });
        getBehavior().setOnFocusNextRow(() -> { onFocusNextCell(); });
        getBehavior().setOnMoveToFirstCell(() -> { onMoveToFirstCell(); });
        getBehavior().setOnMoveToLastCell(() -> { onMoveToLastCell(); });
        getBehavior().setOnScrollPageDown(isFocusDriven -> onScrollPageDown(isFocusDriven));
        getBehavior().setOnScrollPageUp(isFocusDriven -> onScrollPageUp(isFocusDriven));
        getBehavior().setOnSelectPreviousRow(() -> { onSelectPreviousCell(); });
        getBehavior().setOnSelectNextRow(() -> { onSelectNextCell(); });

        // Register listeners
        registerChangeListener(listView.itemsProperty(), "ITEMS");
        registerChangeListener(listView.orientationProperty(), "ORIENTATION");
        registerChangeListener(listView.cellFactoryProperty(), "CELL_FACTORY");
        registerChangeListener(listView.parentProperty(), "PARENT");
        registerChangeListener(listView.placeholderProperty(), "PLACEHOLDER");
        registerChangeListener(listView.fixedCellSizeProperty(), "FIXED_CELL_SIZE");
    }
    
    @Override protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if ("ITEMS".equals(p)) {
            updateListViewItems();
        } else if ("ORIENTATION".equals(p)) {
            flow.setVertical(getSkinnable().getOrientation() == Orientation.VERTICAL);
        } else if ("CELL_FACTORY".equals(p)) {
            flow.recreateCells();
        } else if ("PARENT".equals(p)) {
            if (getSkinnable().getParent() != null && getSkinnable().isVisible()) {
                getSkinnable().requestLayout();
            }
        } else if ("PLACEHOLDER".equals(p)) {
            updatePlaceholderRegionVisibility();
        } else if ("FIXED_CELL_SIZE".equals(p)) {
            flow.setFixedCellSize(getSkinnable().getFixedCellSize());
        }
    }

    private final ListChangeListener<T> listViewItemsListener = new ListChangeListener<T>() {
        @Override public void onChanged(Change<? extends T> c) {
            while (c.next()) {
                if (c.wasReplaced()) {
                    // RT-28397: Support for when an item is replaced with itself (but
                    // updated internal values that should be shown visually).
                    // This code was updated for RT-36714 to not update all cells,
                    // just those affected by the change
                    for (int i = c.getFrom(); i < c.getTo(); i++) {
                        flow.setCellDirty(i);
                    }

                    break;
                } else if (c.getRemovedSize() == itemCount) {
                    // RT-22463: If the user clears out an items list then we
                    // should reset all cells (in particular their contained
                    // items) such that a subsequent addition to the list of
                    // an item which equals the old item (but is rendered
                    // differently) still displays as expected (i.e. with the
                    // updated display, not the old display).
                    itemCount = 0;
                    break;
                }
            }
            
            rowCountDirty = true;
            getSkinnable().requestLayout();
        }
    };
    
    private final WeakListChangeListener<T> weakListViewItemsListener =
            new WeakListChangeListener<T>(listViewItemsListener);

    public void updateListViewItems() {
        if (listViewItems != null) {
            listViewItems.removeListener(weakListViewItemsListener);
        }

        this.listViewItems = getSkinnable().getItems();

        if (listViewItems != null) {
            listViewItems.addListener(weakListViewItemsListener);
        }

        rowCountDirty = true;
        getSkinnable().requestLayout();
    }
    
    private int itemCount = -1;

    @Override public int getItemCount() {
//        return listViewItems == null ? 0 : listViewItems.size();
        return itemCount;
    }
    
    private boolean needCellsRebuilt = true;
    private boolean needCellsReconfigured = false;

    @Override protected void updateRowCount() {
        if (flow == null) return;
        
        int oldCount = itemCount;
        int newCount = listViewItems == null ? 0 : listViewItems.size();
        
        itemCount = newCount;
        
        flow.setCellCount(newCount);
        
        updatePlaceholderRegionVisibility();
        if (newCount != oldCount) {
            needCellsRebuilt = true;
        } else {
            needCellsReconfigured = true;
        }
    }
    
    protected final void updatePlaceholderRegionVisibility() {
        boolean visible = getItemCount() == 0;
        
        if (visible) {
            placeholderNode = getSkinnable().getPlaceholder();
            if (placeholderNode == null && (EMPTY_LIST_TEXT != null && ! EMPTY_LIST_TEXT.isEmpty())) {
                placeholderNode = new Label();
                ((Label)placeholderNode).setText(EMPTY_LIST_TEXT);
            }

            if (placeholderNode != null) {
                if (placeholderRegion == null) {
                    placeholderRegion = new StackPane();
                    placeholderRegion.getStyleClass().setAll("placeholder");
                    getChildren().add(placeholderRegion);
                }

                placeholderRegion.getChildren().setAll(placeholderNode);
            }
        }

        flow.setVisible(! visible);
        if (placeholderRegion != null) {
            placeholderRegion.setVisible(visible);
        }
    }

    @Override public ListCell<T> createCell() {
        ListCell<T> cell;
        if (getSkinnable().getCellFactory() != null) {
            cell = getSkinnable().getCellFactory().call(getSkinnable());
        } else {
            cell = createDefaultCellImpl();
        }

        cell.updateListView(getSkinnable());

        return cell;
    }

    private static <T> ListCell<T> createDefaultCellImpl() {
        return new ListCell<T>() {
            @Override public void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (item instanceof Node) {
                    setText(null);
                    Node currentNode = getGraphic();
                    Node newNode = (Node) item;
                    if (currentNode == null || ! currentNode.equals(newNode)) {
                        setGraphic(newNode);
                    }
                } else {
                    /**
                     * This label is used if the item associated with this cell is to be
                     * represented as a String. While we will lazily instantiate it
                     * we never clear it, being more afraid of object churn than a minor
                     * "leak" (which will not become a "major" leak).
                     */
                    setText(item == null ? "null" : item.toString());
                    setGraphic(null);
                }
            }
        };
    }

    @Override protected void layoutChildren(final double x, final double y,
            final double w, final double h) {
        super.layoutChildren(x, y, w, h);
        
        if (needCellsRebuilt) {
            flow.rebuildCells();
        } else if (needCellsReconfigured) {
            flow.reconfigureCells();
        } 
        
        needCellsRebuilt = false;
        needCellsReconfigured = false;
        
        if (getItemCount() == 0) {
            // show message overlay instead of empty listview
            if (placeholderRegion != null) {
                placeholderRegion.setVisible(w > 0 && h > 0);
                placeholderRegion.resizeRelocate(x, y, w, h);
            }
        } else {
            flow.resizeRelocate(x, y, w, h);
            double borderWidth = 0;
            if(getSkinnable().getBorder()!=null)
            	borderWidth = getSkinnable().getBorder().getStrokes().get(0).getWidths().getTop();
            
            // FIXME, CHANGE THE HEIGHT if 3D is active or not
            if(flow.getCellCount() > 0)
            	getSkinnable().setPrefHeight(estimateHeight(borderWidth));
        }
    }
    
    private double estimateHeight(double borderWidth ){
    	return (flow.getCell(0).getHeight() + ((C3DListView<T>) getSkinnable()).currentVerticalGapProperty().get()) * getSkinnable().getItems().size() + ((C3DListView<T>) getSkinnable()).getCellVerticalMargin() - ((C3DListView<T>) getSkinnable()).currentVerticalGapProperty().get() -3 + borderWidth;
    }
    
    @Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        checkState();

        if (getItemCount() == 0) {
            if (placeholderRegion == null) {
                updatePlaceholderRegionVisibility();
            }
            if (placeholderRegion != null) {
                return placeholderRegion.prefWidth(height) + leftInset + rightInset;
            }
        }

        return computePrefHeight(-1, topInset, rightInset, bottomInset, leftInset) * 0.618033987;
    }

    @Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
    	if (getSkinnable().getItems().size() <= 0) return 200;
    	// handel if the list has border
    	double borderWidth = 0;
        if(getSkinnable().getBorder()!=null)
        	borderWidth = getSkinnable().getBorder().getStrokes().get(0).getWidths().getTop();
        return estimateHeight(borderWidth);
    }
    
    private void onFocusPreviousCell() {
        FocusModel<T> fm = getSkinnable().getFocusModel();
        if (fm == null) return;
        flow.show(fm.getFocusedIndex());
    }

    private void onFocusNextCell() {
        FocusModel<T> fm = getSkinnable().getFocusModel();
        if (fm == null) return;
        flow.show(fm.getFocusedIndex());
    }

    private void onSelectPreviousCell() {
        SelectionModel<T> sm = getSkinnable().getSelectionModel();
        if (sm == null) return;

        int pos = sm.getSelectedIndex();
        flow.show(pos);

        // Fix for RT-11299
        IndexedCell<T> cell = flow.getFirstVisibleCell();
        if (cell == null || pos < cell.getIndex()) {
            flow.setPosition(pos / (double) getItemCount());
        }
    }

    private void onSelectNextCell() {
        SelectionModel<T> sm = getSkinnable().getSelectionModel();
        if (sm == null) return;

        int pos = sm.getSelectedIndex();
        flow.show(pos);

        // Fix for RT-11299
        ListCell<T> cell = flow.getLastVisibleCell();
        if (cell == null || cell.getIndex() < pos) {
            flow.setPosition(pos / (double) getItemCount());
        }
    }

    private void onMoveToFirstCell() {
        flow.show(0);
        flow.setPosition(0);
    }

    private void onMoveToLastCell() {
//        SelectionModel sm = getSkinnable().getSelectionModel();
//        if (sm == null) return;
//
        int endPos = getItemCount() - 1;
//        sm.select(endPos);
        flow.show(endPos);
        flow.setPosition(1);
    }

    /**
     * Function used to scroll the container down by one 'page', although
     * if this is a horizontal container, then the scrolling will be to the right.
     */
    private int onScrollPageDown(boolean isFocusDriven) {
        ListCell<T> lastVisibleCell = flow.getLastVisibleCellWithinViewPort();
        if (lastVisibleCell == null) return -1;

        final SelectionModel<T> sm = getSkinnable().getSelectionModel();
        final FocusModel<T> fm = getSkinnable().getFocusModel();
        if (sm == null || fm == null) return -1;

        int lastVisibleCellIndex = lastVisibleCell.getIndex();

//        boolean isSelected = sm.isSelected(lastVisibleCellIndex) || fm.isFocused(lastVisibleCellIndex) || lastVisibleCellIndex == anchor;
        // isSelected represents focus OR selection
        boolean isSelected = false;
        if (isFocusDriven) {
            isSelected = lastVisibleCell.isFocused() || fm.isFocused(lastVisibleCellIndex);
        } else {
            isSelected = lastVisibleCell.isSelected() || sm.isSelected(lastVisibleCellIndex);
        }

        if (isSelected) {
            boolean isLeadIndex = (isFocusDriven && fm.getFocusedIndex() == lastVisibleCellIndex)
                               || (! isFocusDriven && sm.getSelectedIndex() == lastVisibleCellIndex);

            if (isLeadIndex) {
                // if the last visible cell is selected, we want to shift that cell up
                // to be the top-most cell, or at least as far to the top as we can go.
                flow.showAsFirst(lastVisibleCell);

                ListCell<T> newLastVisibleCell = flow.getLastVisibleCellWithinViewPort();
                lastVisibleCell = newLastVisibleCell == null ? lastVisibleCell : newLastVisibleCell;
            }
        } else {
            // if the selection is not on the 'bottom' most cell, we firstly move
            // the selection down to that, without scrolling the contents, so
            // this is a no-op
        }

        int newSelectionIndex = lastVisibleCell.getIndex();
        flow.show(lastVisibleCell);
        return newSelectionIndex;
    }

    /**
     * Function used to scroll the container up by one 'page', although
     * if this is a horizontal container, then the scrolling will be to the left.
     */
    private int onScrollPageUp(boolean isFocusDriven) {
        ListCell<T> firstVisibleCell = flow.getFirstVisibleCellWithinViewPort();
        if (firstVisibleCell == null) return -1;

        final SelectionModel<T> sm = getSkinnable().getSelectionModel();
        final FocusModel<T> fm = getSkinnable().getFocusModel();
        if (sm == null || fm == null) return -1;

        int firstVisibleCellIndex = firstVisibleCell.getIndex();

        // isSelected represents focus OR selection
        boolean isSelected = false;
        if (isFocusDriven) {
            isSelected = firstVisibleCell.isFocused() || fm.isFocused(firstVisibleCellIndex);
        } else {
            isSelected = firstVisibleCell.isSelected() || sm.isSelected(firstVisibleCellIndex);
        }

        if (isSelected) {
            boolean isLeadIndex = (isFocusDriven && fm.getFocusedIndex() == firstVisibleCellIndex)
                               || (! isFocusDriven && sm.getSelectedIndex() == firstVisibleCellIndex);

            if (isLeadIndex) {
                // if the first visible cell is selected, we want to shift that cell down
                // to be the bottom-most cell, or at least as far to the bottom as we can go.
                flow.showAsLast(firstVisibleCell);

                ListCell<T> newFirstVisibleCell = flow.getFirstVisibleCellWithinViewPort();
                firstVisibleCell = newFirstVisibleCell == null ? firstVisibleCell : newFirstVisibleCell;
            }
        } else {
            // if the selection is not on the 'top' most cell, we firstly move
            // the selection up to that, without scrolling the contents, so
            // this is a no-op
        }

        int newSelectionIndex = firstVisibleCell.getIndex();
        flow.show(firstVisibleCell);
        return newSelectionIndex;
    }

//    @Override
//    public Object accGetAttribute(Attribute attribute, Object... parameters) {
//        switch (attribute) {
//            case FOCUS_ITEM: {
//                FocusModel<?> fm = getSkinnable().getFocusModel();
//                int focusedIndex = fm.getFocusedIndex();
//                if (focusedIndex == -1) {
//                    if (placeholderRegion != null && placeholderRegion.isVisible()) {
//                        return placeholderRegion.getChildren().get(0);
//                    }
//                    if (getItemCount() > 0) {
//                        focusedIndex = 0;
//                    } else {
//                        return null;
//                    }
//                }
//                return flow.getPrivateCell(focusedIndex);
//            }
//            case ROW_AT_INDEX: {
//                Integer rowIndex = (Integer)parameters[0];
//                if (rowIndex == null) return null;
//                if (0 <= rowIndex && rowIndex < getItemCount()) {
//                    return flow.getPrivateCell(rowIndex);
//                }
//                return null;
//            }
//            case SELECTED_ROWS: {
//                MultipleSelectionModel<T> sm = getSkinnable().getSelectionModel();
//                ObservableList<Integer> indices = sm.getSelectedIndices();
//                List<Node> selection = new ArrayList<>(indices.size());
//                for (int i : indices) {
//                    ListCell<T> row = flow.getPrivateCell(i);
//                    if (row != null) selection.add(row);
//                }
//                return FXCollections.observableArrayList(selection);
//            }
//            case VERTICAL_SCROLLBAR: return flow.getVbar();
//            case HORIZONTAL_SCROLLBAR: return flow.getHbar();
//            default: return super.accGetAttribute(attribute, parameters);
//        }
//    }
//
//    @Override
//    public void accExecuteAction(Action action, Object... parameters) {
//        switch (action) {
//            case SCROLL_TO_INDEX: {
//                Integer index = (Integer)parameters[0];
//                if (index != null) flow.show(index);
//                break;
//            }
//            default: super.accExecuteAction(action, parameters);
//        }
//    }
}
