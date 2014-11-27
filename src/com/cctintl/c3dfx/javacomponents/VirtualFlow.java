package com.cctintl.c3dfx.javacomponents;

/*
 * Copyright (c) 2010, 2014, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 */
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Cell;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import javafx.util.Duration;
import sun.util.logging.PlatformLogger;

import com.sun.javafx.scene.control.Logging;
import com.sun.javafx.scene.control.skin.Utils;
import com.sun.javafx.scene.traversal.Algorithm;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import com.sun.javafx.scene.traversal.TraversalContext;

/**
 * Implementation of a virtualized container using a cell based mechanism.
 */
public class VirtualFlow<T extends IndexedCell<?>> extends Region {

    /**
     * Scroll events may request to scroll about a number of "lines". We first
     * decide how big one "line" is - for fixed cell size it's clear,
     * for variable cell size we settle on a single number so that the scrolling
     * speed is consistent. Now if the line is so big that
     * MIN_SCROLLING_LINES_PER_PAGE of them don't fit into one page, we make
     * them smaller to prevent the scrolling step to be too big (perhaps
     * even more than one page).
     */
    private static final int MIN_SCROLLING_LINES_PER_PAGE = 8;
    protected final static boolean IS_TOUCH_SUPPORTED = Platform.isSupported(ConditionalFeature.INPUT_TOUCH);
    
    private boolean touchDetected = false;
    private boolean mouseDown = false;

    /**
     * There are two main complicating factors in the implementation of the
     * VirtualFlow, which are made even more complicated due to the performance
     * sensitive nature of this code. The first factor is the actual
     * virtualization mechanism, wired together with the PositionMapper.
     * The second complicating factor is the desire to do minimal layout
     * and minimal updates to CSS.
     *
     * Since the layout mechanism runs at most once per pulse, we want to hook
     * into this mechanism for minimal recomputation. Whenever a layout pass
     * is run we record the width/height that the virtual flow was last laid
     * out to. In subsequent passes, if the width/height has not changed then
     * we know we only have to rebuild the cells. If the width or height has
     * changed, then we can make appropriate decisions based on whether the
     * width / height has been reduced or expanded.
     *
     * In various places, if requestLayout is called it is generally just
     * used to indicate that some form of layout needs to happen (either the
     * entire thing has to be reconstructed, or just the cells need to be
     * reconstructed, generally).
     *
     * The accumCell is a special cell which is used in some computations
     * when an actual cell for that item isn't currently available. However,
     * the accumCell must be cleared whenever the cellFactory function is
     * changed because we need to use the cells that come from the new factory.
     *
     * In addition to storing the lastWidth and lastHeight, we also store the
     * number of cells that existed last time we performed a layout. In this
     * way if the number of cells change, we can request a layout and when it
     * occurs we can tell that the number of cells has changed and react
     * accordingly.
     *
     * Because the VirtualFlow can be laid out horizontally or vertically a
     * naming problem is present when trying to conceptualize and implement
     * the flow. In particular, the words "width" and "height" are not
     * precise when describing the unit of measure along the "virtualized"
     * axis and the "orthogonal" axis. For example, the height of a cell when
     * the flow is vertical is the magnitude along the "virtualized axis",
     * and the width is along the axis orthogonal to it.
     *
     * Since "height" and "width" are not reliable terms, we use the words
     * "length" and "breadth" to describe the magnitude of a cell along
     * the virtualized axis and orthogonal axis. For example, in a vertical
     * flow, the height=length and the width=breadth. In a horizontal axis,
     * the height=breadth and the width=length.
     *
     * These terms are somewhat arbitrary, but chosen so that when reading
     * most of the below code you can think in just one dimension, with
     * helper functions converting width/height in to length/breadth, while
     * also being different from width/height so as not to get confused with
     * the actual width/height of a cell.
     */
    /**
     * Indicates the primary direction of virtualization. If true, then the
     * primary direction of virtualization is vertical, meaning that cells will
     * stack vertically on top of each other. If false, then they will stack
     * horizontally next to each other.
     */
    private BooleanProperty vertical;
    public final void setVertical(boolean value) {
        verticalProperty().set(value);
    }

    public final boolean isVertical() {
        return vertical == null ? true : vertical.get();
    }

    public final BooleanProperty verticalProperty() {
        if (vertical == null) {
            vertical = new BooleanPropertyBase(true) {
                @Override protected void invalidated() {                    
                    pile.clear();
                    sheetChildren.clear();
                    cells.clear();
                    lastWidth = lastHeight = -1;
                    setMaxPrefBreadth(-1);
                    setViewportBreadth(0);
                    setViewportLength(0);
                    lastPosition = 0;
                    hbar.setValue(0);
                    vbar.setValue(0);
                    setPosition(0.0f);
                    setNeedsLayout(true);
                    requestLayout();
                }

                @Override
                public Object getBean() {
                    return VirtualFlow.this;
                }

                @Override
                public String getName() {
                    return "vertical";
                }
            };
        }
        return vertical;
    }

    /**
     * Indicates whether the VirtualFlow viewport is capable of being panned
     * by the user (either via the mouse or touch events).
     */
    private boolean pannable = true;
    public boolean isPannable() { return pannable; }
    public void setPannable(boolean value) { this.pannable = value; }

    /**
     * Indicates the number of cells that should be in the flow. The user of
     * the VirtualFlow must set this appropriately. When the cell count changes
     * the VirtualFlow responds by updating the visuals. If the items backing
     * the cells change, but the count has not changed, you must call the
     * reconfigureCells() function to update the visuals.
     */
    private int cellCount;
    public int getCellCount() { return cellCount; }
    public void setCellCount(int i) {
        int oldCount = cellCount;
        this.cellCount = i;
        
        boolean countChanged = oldCount != cellCount;

        // ensure that the virtual scrollbar adjusts in size based on the current
        // cell count.
        if (countChanged) {
            VirtualScrollBar lengthBar = isVertical() ? vbar : hbar;
            lengthBar.setMax(i);
        }

        // I decided *not* to reset maxPrefBreadth here for the following
        // situation. Suppose I have 30 cells and then I add 10 more. Just
        // because I added 10 more doesn't mean the max pref should be
        // reset. Suppose the first 3 cells were extra long, and I was
        // scrolled down such that they weren't visible. If I were to reset
        // maxPrefBreadth when subsequent cells were added or removed, then the
        // scroll bars would erroneously reset as well. So I do not reset
        // the maxPrefBreadth here.

        // Fix for RT-12512, RT-14301 and RT-14864.
        // Without this, the VirtualFlow length-wise scrollbar would not change
        // as expected. This would leave items unable to be shown, as they
        // would exist outside of the visible area, even when the scrollbar
        // was at its maximum position.
        // FIXME this should be only executed on the pulse, so this will likely
        // lead to performance degradation until it is handled properly.
        if (countChanged) {
            layoutChildren();

            // Fix for RT-13965: Without this line of code, the number of items in
            // the sheet would constantly grow, leaking memory for the life of the
            // application. This was especially apparent when the total number of
            // cells changes - regardless of whether it became bigger or smaller.
            sheetChildren.clear();

            Parent parent = getParent();
            if (parent != null) parent.requestLayout();
        }
        // TODO suppose I had 100 cells and I added 100 more. Further
        // suppose I was scrolled to the bottom when that happened. I
        // actually want to update the position of the mapper such that
        // the view remains "stable".
    }

    /**
     * The position of the VirtualFlow within its list of cells. This is a value
     * between 0 and 1.
     */
    private double position;

    public double getPosition() {
        return position;
    }

    public void setPosition(double newPosition) {
        boolean needsUpdate = this.position != newPosition;
        this.position = com.sun.javafx.Utils.clamp(0, newPosition, 1);;
        if (needsUpdate) {
            requestLayout();
        }
    }
    
    /**
     * For optimisation purposes, some use cases can trade dynamic cell length
     * for speed - if fixedCellSize is greater than zero we'll use that rather
     * than determine it by querying the cell itself.
     */
    private double fixedCellSize = 0;
    private boolean fixedCellSizeEnabled = false;

    public void setFixedCellSize(final double value) {
        this.fixedCellSize = value;
        this.fixedCellSizeEnabled = fixedCellSize > 0;
        needsCellsLayout = true;
        layoutChildren();
    }
    
    /**
     * Callback which is invoked whenever the VirtualFlow needs a new
     * IndexedCell. The VirtualFlow attempts to reuse cells whenever possible
     * and only creates the minimal number of cells necessary.
     */
    private Callback<VirtualFlow, T> createCell;
    public Callback<VirtualFlow, T> getCreateCell() { return createCell; }
    public void setCreateCell(Callback<VirtualFlow, T> cc) {
        this.createCell = cc;

        if (createCell != null) {
            accumCell = null;
            setNeedsLayout(true);
            recreateCells();
            if (getParent() != null) getParent().requestLayout();
        }
    }

    /**
     * The maximum preferred size in the non-virtual direction. For example,
     * if vertical, then this is the max pref width of all cells encountered.
     * <p>
     * In general, this is the largest preferred size in the non-virtual
     * direction that we have ever encountered. We don't reduce this size
     * unless instructed to do so, so as to reduce the amount of scroll bar
     * jitter. The access on this variable is package ONLY FOR TESTING.
     */
    private double maxPrefBreadth;
    protected final void setMaxPrefBreadth(double value) {
        this.maxPrefBreadth = value;
    }
    protected final double getMaxPrefBreadth() {
        return maxPrefBreadth;
    }

    /**
     * The breadth of the viewport portion of the VirtualFlow as computed during
     * the layout pass. In a vertical flow this would be the same as the clip
     * view width. In a horizontal flow this is the clip view height.
     * The access on this variable is package ONLY FOR TESTING.
     */
    private double viewportBreadth;
    protected final void setViewportBreadth(double value) {
        this.viewportBreadth = value;
    }
    protected final double getViewportBreadth() {
        return viewportBreadth;
    }

    /**
     * The length of the viewport portion of the VirtualFlow as computed
     * during the layout pass. In a vertical flow this would be the same as the
     * clip view height. In a horizontal flow this is the clip view width.
     * The access on this variable is package ONLY FOR TESTING.
     */
    private double viewportLength;
    void setViewportLength(double value) {
        this.viewportLength = value;
    }
    protected double getViewportLength() {
        return viewportLength;
    }


    /**
     * The width of the VirtualFlow the last time it was laid out. We
     * use this information for several fast paths during the layout pass.
     */
    double lastWidth = -1;

    /**
     * The height of the VirtualFlow the last time it was laid out. We
     * use this information for several fast paths during the layout pass.
     */
    double lastHeight = -1;

    /**
     * The number of "virtual" cells in the flow the last time it was laid out.
     * For example, there may have been 1000 virtual cells, but only 20 actual
     * cells created and in use. In that case, lastCellCount would be 1000.
     */
    int lastCellCount = 0;

    /**
     * We remember the last value for vertical the last time we laid out the
     * flow. If vertical has changed, we will want to change the max & value
     * for the different scroll bars. Since we do all the scroll bar update
     * work in the layoutChildren function, we need to know what the old value for
     * vertical was.
     */
    boolean lastVertical;

    /**
     * The position last time we laid out. If none of the lastXXX vars have
     * changed respective to their values in layoutChildren, then we can just punt
     * out of the method (I hope...)
     */
    double lastPosition;

    /**
     * The breadth of the first visible cell last time we laid out.
     */
    double lastCellBreadth = -1;

    /**
     * The length of the first visible cell last time we laid out.
     */
    double lastCellLength = -1;

    /**
     * The list of cells representing those cells which actually make up the
     * current view. The cells are ordered such that the first cell in this
     * list is the first in the view, and the last cell is the last in the
     * view. When pixel scrolling, the list is simply shifted and items drop
     * off the beginning or the end, depending on the order of scrolling.
     * <p>
     * This is package private ONLY FOR TESTING
     */
    final ArrayLinkedList<T> cells = new ArrayLinkedList<T>();
    protected List<T> getCells() {
        return cells;
    }

    /**
     * A structure containing cells that can be reused later. These are cells
     * that at one time were needed to populate the view, but now are no longer
     * needed. We keep them here until they are needed again.
     * <p>
     * This is package private ONLY FOR TESTING
     */
    final ArrayLinkedList<T> pile = new ArrayLinkedList<T>();

    /**
     * A special cell used to accumulate bounds, such that we reduce object
     * churn. This cell must be recreated whenever the cell factory function
     * changes. This has package access ONLY for testing.
     */
    T accumCell;

    /**
     * This group is used for holding the 'accumCell'. 'accumCell' must
     * be added to the skin for it to be styled. Otherwise, it doesn't
     * report the correct width/height leading to issues when scrolling
     * the flow
     */
    Group accumCellParent;

    /**
     * The group which holds the cells.
     */
    final Group sheet;
    
    final ObservableList<Node> sheetChildren;
    
    /**
     * The scroll bar used for scrolling horizontally. This has package access
     * ONLY for testing.
     */
    private VirtualScrollBar hbar = new VirtualScrollBar(this);

    public final VirtualScrollBar getHbar() {
        return hbar;
    }
    /**
     * The scroll bar used to scrolling vertically. This has package access
     * ONLY for testing.
     */
    private VirtualScrollBar vbar = new VirtualScrollBar(this);

    public final VirtualScrollBar getVbar() {
        return vbar;
    }

    /**
     * Control in which the cell's sheet is placed and forms the viewport. The
     * viewportBreadth and viewportLength are simply the dimensions of the
     * clipView. This has package access ONLY for testing.
     */
    ClippedContainer clipView;

    /**
     * When both the horizontal and vertical scroll bars are visible,
     * we have to 'fill in' the bottom right corner where the two scroll bars
     * meet. This is handled by this corner region. This has package access
     * ONLY for testing.
     */
    StackPane corner;

    // used for panning the virtual flow
    private double lastX;
    private double lastY;
    private boolean isPanning = false;

    public VirtualFlow() {
        getStyleClass().add("virtual-flow");
        setId("virtual-flow");

        // initContent
        // --- sheet
        sheet = new Group();
        sheet.getStyleClass().add("sheet");
        sheet.setAutoSizeChildren(false);
        
        sheetChildren = sheet.getChildren();

        // --- clipView
        clipView = new ClippedContainer(this);
        clipView.setNode(sheet);
        getChildren().add(clipView);

        // --- accumCellParent
        accumCellParent = new Group();
        accumCellParent.setVisible(false);
        getChildren().add(accumCellParent);

        
        /*
        ** don't allow the ScrollBar to handle the ScrollEvent,
        ** In a VirtualFlow a vertical scroll should scroll on the vertical only,
        ** whereas in a horizontal ScrollBar it can scroll horizontally.
        */ 
        final EventDispatcher blockEventDispatcher = (event, tail) -> event;
        // block ScrollEvent from being passed down to scrollbar's skin
        final EventDispatcher oldHsbEventDispatcher = hbar.getEventDispatcher();
        hbar.setEventDispatcher((event, tail) -> {
            if (event.getEventType() == ScrollEvent.SCROLL &&
                    !((ScrollEvent)event).isDirect()) {
                tail = tail.prepend(blockEventDispatcher);
                tail = tail.prepend(oldHsbEventDispatcher);
                return tail.dispatchEvent(event);
            }
            return oldHsbEventDispatcher.dispatchEvent(event, tail);
        });
        // block ScrollEvent from being passed down to scrollbar's skin
        final EventDispatcher oldVsbEventDispatcher = vbar.getEventDispatcher();
        vbar.setEventDispatcher((event, tail) -> {
            if (event.getEventType() == ScrollEvent.SCROLL &&
                    !((ScrollEvent)event).isDirect()) {
                tail = tail.prepend(blockEventDispatcher);
                tail = tail.prepend(oldVsbEventDispatcher);
                return tail.dispatchEvent(event);
            }
            return oldVsbEventDispatcher.dispatchEvent(event, tail);
        });
        /*
        ** listen for ScrollEvents over the whole of the VirtualFlow
        ** area, the above dispatcher having removed the ScrollBars
        ** scroll event handling.
        */
        setOnScroll(new EventHandler<javafx.scene.input.ScrollEvent>() {
            @Override public void handle(ScrollEvent event) {
                if (IS_TOUCH_SUPPORTED) {
                    if (touchDetected == false &&  mouseDown == false ) {
                        startSBReleasedAnimation();
                    }
                }
                /*
                ** calculate the delta in the direction of the flow.
                */ 
                double virtualDelta = 0.0;
                if (isVertical()) {
                    switch(event.getTextDeltaYUnits()) {
                        case PAGES:
                            virtualDelta = event.getTextDeltaY() * lastHeight;
                            break;
                        case LINES:
                            double lineSize;
                            if (fixedCellSizeEnabled) {
                                lineSize = fixedCellSize;
                            } else {
                                // For the scrolling to be reasonably consistent
                                // we set the lineSize to the average size
                                // of all currently loaded lines.
                                T lastCell = cells.getLast();
                                lineSize =
                                        (getCellPosition(lastCell)
                                            + getCellLength(lastCell)
                                            - getCellPosition(cells.getFirst()))
                                        / cells.size();
                            }

                            if (lastHeight / lineSize < MIN_SCROLLING_LINES_PER_PAGE) {
                                lineSize = lastHeight / MIN_SCROLLING_LINES_PER_PAGE;
                            }

                            virtualDelta = event.getTextDeltaY() * lineSize;
                            break;
                        case NONE:
                            virtualDelta = event.getDeltaY();
                    }
                } else { // horizontal
                    switch(event.getTextDeltaXUnits()) {
                        case CHARACTERS:
                            // can we get character size here?
                            // for now, fall through to pixel values
                        case NONE:
                            double dx = event.getDeltaX();
                            double dy = event.getDeltaY();

                            virtualDelta = (Math.abs(dx) > Math.abs(dy) ? dx : dy);
                    }
                }

                if (virtualDelta != 0.0) { 
                    /*
                    ** only consume it if we use it
                    */
                    double result = adjustPixels(-virtualDelta);
                    if (result != 0.0) {
                        event.consume();
                    }
                }

                ScrollBar nonVirtualBar = isVertical() ? hbar : vbar;
                if (needBreadthBar) {
                    double nonVirtualDelta = isVertical() ? event.getDeltaX() : event.getDeltaY();
                    if (nonVirtualDelta != 0.0) {
                        double newValue = nonVirtualBar.getValue() - nonVirtualDelta;
                        if (newValue < nonVirtualBar.getMin()) {
                            nonVirtualBar.setValue(nonVirtualBar.getMin());
                        } else if (newValue > nonVirtualBar.getMax()) {
                            nonVirtualBar.setValue(nonVirtualBar.getMax());
                        } else {
                            nonVirtualBar.setValue(newValue);
                        }
                        event.consume();
                    }
                }
            }
        });


        addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                mouseDown = true;
                if (IS_TOUCH_SUPPORTED) {
                    scrollBarOn();
                }
                if (isFocusTraversable()) {
                    // We check here to see if the current focus owner is within
                    // this VirtualFlow, and if so we back-off from requesting
                    // focus back to the VirtualFlow itself. This is particularly
                    // relevant given the bug identified in RT-32869. In this
                    // particular case TextInputControl was clearing selection
                    // when the focus on the TextField changed, meaning that the
                    // right-click context menu was not showing the correct
                    // options as there was no selection in the TextField.
                    boolean doFocusRequest = true;
                    Node focusOwner = getScene().getFocusOwner();
                    if (focusOwner != null) {
                        Parent parent = focusOwner.getParent();
                        while (parent != null) {
                            if (parent.equals(VirtualFlow.this)) {
                                doFocusRequest = false;
                                break;
                            }
                            parent = parent.getParent();
                        }
                    }

                    if (doFocusRequest) {
                        requestFocus();
                    }
                }

                lastX = e.getX();
                lastY = e.getY();

                // determine whether the user has push down on the virtual flow,
                // or whether it is the scrollbar. This is done to prevent
                // mouse events being 'doubled up' when dragging the scrollbar
                // thumb - it has the side-effect of also starting the panning
                // code, leading to flicker
                isPanning = ! (vbar.getBoundsInParent().contains(e.getX(), e.getY())
                        || hbar.getBoundsInParent().contains(e.getX(), e.getY()));
            }
        });
        addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            mouseDown = false;
            if (IS_TOUCH_SUPPORTED) {
                startSBReleasedAnimation();
            }
        });
        addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            if (IS_TOUCH_SUPPORTED) {
                scrollBarOn();
            }
            if (! isPanning || ! isPannable()) return;

            // With panning enabled, we support panning in both vertical
            // and horizontal directions, regardless of the fact that
            // VirtualFlow is virtual in only one direction.
            double xDelta = lastX - e.getX();
            double yDelta = lastY - e.getY();

            // figure out the distance that the mouse moved in the virtual
            // direction, and then perform the movement along that axis
            // virtualDelta will contain the amount we actually did move
            double virtualDelta = isVertical() ? yDelta : xDelta;
            double actual = adjustPixels(virtualDelta);
            if (actual != 0) {
                // update last* here, as we know we've just adjusted the
                // scrollbar. This means we don't get the situation where a
                // user presses-and-drags a long way past the min or max
                // values, only to change directions and see the scrollbar
                // start moving immediately.
                if (isVertical()) lastY = e.getY();
                else lastX = e.getX();
            }

            // similarly, we do the same in the non-virtual direction
            double nonVirtualDelta = isVertical() ? xDelta : yDelta;
            ScrollBar nonVirtualBar = isVertical() ? hbar : vbar;
            if (nonVirtualBar.isVisible()) {
                double newValue = nonVirtualBar.getValue() + nonVirtualDelta;
                if (newValue < nonVirtualBar.getMin()) {
                    nonVirtualBar.setValue(nonVirtualBar.getMin());
                } else if (newValue > nonVirtualBar.getMax()) {
                    nonVirtualBar.setValue(nonVirtualBar.getMax());
                } else {
                    nonVirtualBar.setValue(newValue);

                    // same as the last* comment above
                    if (isVertical()) lastX = e.getX();
                    else lastY = e.getY();
                }
            }
        });

        /*
         * We place the scrollbars _above_ the rectangle, such that the drag
         * operations often used in conjunction with scrollbars aren't
         * misinterpreted as drag operations on the rectangle as well (which
         * would be the case if the scrollbars were underneath it as the
         * rectangle itself doesn't block the mouse.
         */
        // --- vbar
        vbar.setOrientation(Orientation.VERTICAL);
        vbar.addEventHandler(MouseEvent.ANY, event -> {
            event.consume();
        });
        getChildren().add(vbar);

        // --- hbar
        hbar.setOrientation(Orientation.HORIZONTAL);
        hbar.addEventHandler(MouseEvent.ANY, event -> {
            event.consume();
        });
        getChildren().add(hbar);

        // --- corner
        corner = new StackPane();
        corner.getStyleClass().setAll("corner");
        getChildren().add(corner);
        
        
        
        // initBinds
        // clipView binds
        InvalidationListener listenerX = valueModel -> {
            updateHbar();
        };
        verticalProperty().addListener(listenerX);
        hbar.valueProperty().addListener(listenerX);
        hbar.visibleProperty().addListener(listenerX);

//        ChangeListener listenerY = new ChangeListener() {
//            @Override public void handle(Bean bean, PropertyReference property) {
//                clipView.setClipY(isVertical() ? 0 : vbar.getValue());
//            }
//        };
//        addChangedListener(VERTICAL, listenerY);
//        vbar.addChangedListener(ScrollBar.VALUE, listenerY);

        ChangeListener<Number> listenerY = (ov, t, t1) -> {
            clipView.setClipY(isVertical() ? 0 : vbar.getValue());
        };
        vbar.valueProperty().addListener(listenerY);
        
        super.heightProperty().addListener((observable, oldHeight, newHeight) -> {
            // Fix for RT-8480, where the VirtualFlow does not show its content
            // after changing size to 0 and back.
            if (oldHeight.doubleValue() == 0 && newHeight.doubleValue() > 0) {
                recreateCells();
            }
        });


        /*
        ** there are certain animations that need to know if the touch is
        ** happening.....
        */
        setOnTouchPressed(e -> {
            touchDetected = true;
            scrollBarOn();
        });

        setOnTouchReleased(e -> {
            touchDetected = false;
            startSBReleasedAnimation();
        });

        setImpl_traversalEngine(new ParentTraversalEngine(this, new Algorithm() {

            Node selectNextAfterIndex(int index, TraversalContext context) {
                T nextCell;
                while ((nextCell = getVisibleCell(++index)) != null) {
                    if (nextCell.isFocusTraversable()) {
                        return nextCell;
                    }
                    Node n = context.selectFirstInParent(nextCell);
                    if (n != null) {
                        return n;
                    }
                }
                return null;
            }

            Node selectPreviousBeforeIndex(int index, TraversalContext context) {
                T prevCell;
                while ((prevCell = getVisibleCell(--index)) != null) {
                    Node prev = context.selectLastInParent(prevCell);
                    if (prev != null) {
                        return prev;
                    }
                    if (prevCell.isFocusTraversable()) {
                        return prevCell;
                    }
                }
                return null;
            }

            @Override
            public Node select(Node owner, Direction dir, TraversalContext context) {
                T cell;
                if (cells.isEmpty()) return null;
                if (cells.contains(owner)) {
                    cell = (T) owner;
                } else {
                    cell = findOwnerCell(owner);
                    Node next = context.selectInSubtree(cell, owner, dir);
                    if (next != null) {
                        return next;
                    }
                    if (dir == Direction.NEXT) dir = Direction.NEXT_IN_LINE;
                }
                int cellIndex = cell.getIndex();
                switch(dir) {
                    case PREVIOUS:
                        return selectPreviousBeforeIndex(cellIndex, context);
                    case NEXT:
                        Node n = context.selectFirstInParent(cell);
                        if (n != null) {
                            return n;
                        }
                        // Intentional fall-through
                    case NEXT_IN_LINE:
                        return selectNextAfterIndex(cellIndex, context);
                }
                return null;
            }

            private T findOwnerCell(Node owner) {
                Parent p = owner.getParent();
                while (!cells.contains(p)) {
                    p = p.getParent();
                }
                return (T)p;
            }

            @Override
            public Node selectFirst(TraversalContext context) {
                T firstCell = cells.getFirst();
                if (firstCell == null) return null;
                if (firstCell.isFocusTraversable()) return firstCell;
                Node n = context.selectFirstInParent(firstCell);
                if (n != null) {
                    return n;
                }
                return selectNextAfterIndex(firstCell.getIndex(), context);
            }

            @Override
            public Node selectLast(TraversalContext context) {
                T lastCell = cells.getLast();
                if (lastCell == null) return null;
                Node p = context.selectLastInParent(lastCell);
                if (p != null) {
                    return p;
                }
                if (lastCell.isFocusTraversable()) return lastCell;
                return selectPreviousBeforeIndex(lastCell.getIndex(), context);
            }
        }));

    }

    void updateHbar() {
        // Bring the clipView.clipX back to 0 if control is vertical or
        // the hbar isn't visible (fix for RT-11666)
        if (! isVisible() || getScene() == null) return;

        if (isVertical()) {
            if (hbar.isVisible()) {
                clipView.setClipX(hbar.getValue());
            } else {
                // all cells are now less than the width of the flow,
                // so we should shift the hbar/clip such that
                // everything is visible in the viewport.
                clipView.setClipX(0);
                hbar.setValue(0);
            }
        }
    }

    /***************************************************************************
     *                                                                         *
     *                          Layout Functionality                           *
     *                                                                         *
     **************************************************************************/

    /**
     * Overridden to implement somewhat more efficient support for layout. The
     * VirtualFlow can generally be considered as being unmanaged, in that
     * whenever the position changes, or other such things change, we need
     * to perform a layout but there is no reason to notify the parent. However
     * when things change which may impact the preferred size (such as
     * vertical, createCell, and configCell) then we need to notify the
     * parent.
     */
    @Override public void requestLayout() {
        // isNeedsLayout() is commented out due to RT-21417. This does not
        // appear to impact performance (indeed, it may help), and resolves the
        // issue identified in RT-21417.
        setNeedsLayout(true);
    }
    
    @Override protected void layoutChildren() {
        if (needsRecreateCells) {
            lastWidth = -1;
            lastHeight = -1;
            releaseCell(accumCell);
//            accumCell = null;
//            accumCellParent.getChildren().clear();
            sheet.getChildren().clear();
            for (int i = 0, max = cells.size(); i < max; i++) {
                cells.get(i).updateIndex(-1);
            }
            cells.clear();
            pile.clear();
            releaseAllPrivateCells();
        } else if (needsRebuildCells) {
            lastWidth = -1;
            lastHeight = -1;
            releaseCell(accumCell);
            for (int i=0; i<cells.size(); i++) {
                cells.get(i).updateIndex(-1);
            }
            addAllToPile();
            releaseAllPrivateCells();
        } else if (needsReconfigureCells) {
            setMaxPrefBreadth(-1);
            lastWidth = -1;
            lastHeight = -1;
        }

        if (! dirtyCells.isEmpty()) {
            int index;
            final int cellsSize = cells.size();
            while ((index = dirtyCells.nextSetBit(0)) != -1 && index < cellsSize) {
                T cell = cells.get(index);
                // updateIndex(-1) works for TableView, but breaks ListView.
                // For now, the TableView just does not use the dirtyCells API
//                cell.updateIndex(-1);
                if (cell != null) {
                    cell.requestLayout();
                }
                dirtyCells.clear(index);
            }

            setMaxPrefBreadth(-1);
            lastWidth = -1;
            lastHeight = -1;
        }

        final boolean hasSizeChange = sizeChanged;
        boolean recreatedOrRebuilt = needsRebuildCells || needsRecreateCells || sizeChanged;

        needsRecreateCells = false;
        needsReconfigureCells = false;
        needsRebuildCells = false;
        sizeChanged = false;

        if (needsCellsLayout) {
            for (int i = 0, max = cells.size(); i < max; i++) {
                Cell<?> cell = cells.get(i);
                if (cell != null) {
                    cell.requestLayout();
                }
            }
            needsCellsLayout = false;

            // yes, we return here - if needsCellsLayout was set to true, we
            // only did it to do the above - not rerun the entire layout.
            return;
        }

        final double width = getWidth();
        final double height = getHeight();
        final boolean isVertical = isVertical();
        final double position = getPosition();

        // if the width and/or height is 0, then there is no point doing
        // any of this work. In particular, this can happen during startup
        if (width <= 0 || height <= 0) {
            addAllToPile();
            lastWidth = width;
            lastHeight = height;
            hbar.setVisible(false);
            vbar.setVisible(false);
            corner.setVisible(false);
            return;
        }

        // we check if any of the cells in the cells list need layout. This is a
        // sign that they are perhaps animating their sizes. Without this check,
        // we may not perform a layout here, meaning that the cell will likely
        // 'jump' (in height normally) when the user drags the virtual thumb as
        // that is the first time the layout would occur otherwise.
        boolean cellNeedsLayout = false;
        boolean thumbNeedsLayout = false;

        if (IS_TOUCH_SUPPORTED) {
            if ((tempVisibility == true && (hbar.isVisible() == false || vbar.isVisible() == false)) ||
                (tempVisibility == false && (hbar.isVisible() == true || vbar.isVisible() == true))) {
                thumbNeedsLayout = true;
            }
        }

        if (!cellNeedsLayout) {
            for (int i = 0; i < cells.size(); i++) {
                Cell<?> cell = cells.get(i);
                cellNeedsLayout = cell.isNeedsLayout();
                if (cellNeedsLayout) break;
            }
        }


        T firstCell = getFirstVisibleCell();

        // If no cells need layout, we check other criteria to see if this
        // layout call is even necessary. If it is found that no layout is
        // needed, we just punt.
        if (! cellNeedsLayout && !thumbNeedsLayout) {
            boolean cellSizeChanged = false;
            if (firstCell != null) {
                double breadth = getCellBreadth(firstCell);
                double length = getCellLength(firstCell);
                cellSizeChanged = (breadth != lastCellBreadth) || (length != lastCellLength);
                lastCellBreadth = breadth;
                lastCellLength = length;
            }

            if (width == lastWidth &&
                height == lastHeight &&
                cellCount == lastCellCount &&
                isVertical == lastVertical &&
                position == lastPosition &&
                ! cellSizeChanged)
            {
                // TODO this happens to work around the problem tested by
                // testCellLayout_LayoutWithoutChangingThingsUsesCellsInSameOrderAsBefore
                // but isn't a proper solution. Really what we need to do is, when
                // laying out cells, we need to make sure that if a cell is pressed
                // AND we are doing a full rebuild then we need to make sure we
                // use that cell in the same physical location as before so that
                // it gets the mouse release event.
                return;
            }
        }

        /*
         * This function may get called under a variety of circumstances.
         * It will determine what has changed from the last time it was laid
         * out, and will then take one of several execution paths based on
         * what has changed so as to perform minimal layout work and also to
         * give the expected behavior. One or more of the following may have
         * happened:
         *
         *  1) width/height has changed
         *      - If the width and/or height has been reduced (but neither of
         *        them has been expanded), then we simply have to reposition and
         *        resize the scroll bars
         *      - If the width (in the vertical case) has expanded, then we
         *        need to resize the existing cells and reposition and resize
         *        the scroll bars
         *      - If the height (in the vertical case) has expanded, then we
         *        need to resize and reposition the scroll bars and add
         *        any trailing cells
         *
         *  2) cell count has changed
         *      - If the number of cells is bigger, or it is smaller but not
         *        so small as to move the position then we can just update the
         *        cells in place without performing layout and update the
         *        scroll bars.
         *      - If the number of cells has been reduced and it affects the
         *        position, then move the position and rebuild all the cells
         *        and update the scroll bars
         *
         *  3) size of the cell has changed
         *      - If the size changed in the virtual direction (ie: height
         *        in the case of vertical) then layout the cells, adding
         *        trailing cells as necessary and updating the scroll bars
         *      - If the size changed in the non virtual direction (ie: width
         *        in the case of vertical) then simply adjust the widths of
         *        the cells as appropriate and adjust the scroll bars
         *
         *  4) vertical changed, cells is empty, maxPrefBreadth == -1, etc
         *      - Full rebuild.
         *
         * Each of the conditions really resolves to several of a handful of
         * possible outcomes:
         *  a) reposition & rebuild scroll bars
         *  b) resize cells in non-virtual direction
         *  c) add trailing cells
         *  d) update cells
         *  e) resize cells in the virtual direction
         *  f) all of the above
         *
         * So this function first determines what outcomes need to occur, and
         * then will execute all the ones that really need to happen. Every code
         * path ends up touching the "reposition & rebuild scroll bars" outcome,
         * so that one will be executed every time.
         */
        boolean needTrailingCells = false;
        boolean rebuild = cellNeedsLayout  ||
                isVertical != lastVertical ||
                cells.isEmpty()            ||
                getMaxPrefBreadth() == -1  ||
                position != lastPosition   ||
                cellCount != lastCellCount ||
                hasSizeChange ||
                (isVertical && height < lastHeight) || (! isVertical && width < lastWidth);

        if (!rebuild) {
            // Check if maxPrefBreadth didn't change
            double maxPrefBreadth = getMaxPrefBreadth();
            boolean foundMax = false;
            for (int i = 0; i < cells.size(); ++i) {
                double breadth = getCellBreadth(cells.get(i));
                if (maxPrefBreadth == breadth) {
                    foundMax = true;
                } else if (breadth > maxPrefBreadth) {
                    rebuild = true;
                    break;
                }
            }
            if (!foundMax) { // All values were lower
                rebuild = true;
            }
        }

        if (! rebuild) {
            if ((isVertical && height > lastHeight) || (! isVertical && width > lastWidth)) {
                // resized in the virtual direction
                needTrailingCells = true;
            }
        }

        initViewport();

        // Get the index of the "current" cell
        int currentIndex = computeCurrentIndex();
        if (lastCellCount != cellCount) {
            // The cell count has changed. We want to keep the viewport
            // stable if possible. If position was 0 or 1, we want to keep
            // the position in the same place. If the new cell count is >=
            // the currentIndex, then we will adjust the position to be 1.
            // Otherwise, our goal is to leave the index of the cell at the
            // top consistent, with the same translation etc.
            if (position == 0 || position == 1) {
                // Update the item count
//                setItemCount(cellCount);
            } else if (currentIndex >= cellCount) {
                setPosition(1.0f);
//                setItemCount(cellCount);
            } else if (firstCell != null) {
                double firstCellOffset = getCellPosition(firstCell);
                int firstCellIndex = getCellIndex(firstCell);
//                setItemCount(cellCount);
                adjustPositionToIndex(firstCellIndex);
                double viewportTopToCellTop = -computeOffsetForCell(firstCellIndex);
                adjustByPixelAmount(viewportTopToCellTop - firstCellOffset);
            }

            // Update the current index
            currentIndex = computeCurrentIndex();
        }

        if (rebuild) {
            setMaxPrefBreadth(-1);
            // Start by dumping all the cells into the pile
            addAllToPile();

            // The distance from the top of the viewport to the top of the
            // cell for the current index.
            double offset = -computeViewportOffset(getPosition());

            // Add all the leading and trailing cells (the call to add leading
            // cells will add the current cell as well -- that is, the one that
            // represents the current position on the mapper).
            addLeadingCells(currentIndex, offset);

            // Force filling of space with empty cells if necessary
            addTrailingCells(true);
        } else if (needTrailingCells) {
            addTrailingCells(true);
        }

        computeBarVisiblity();
        updateScrollBarsAndCells(recreatedOrRebuilt);

        lastWidth = getWidth();
        lastHeight = getHeight();
        lastCellCount = getCellCount();
        lastVertical = isVertical();
        lastPosition = getPosition();

        cleanPile();
    }

    /**
     * Adds all the cells prior to and including the given currentIndex, until
     * no more can be added without falling off the flow. The startOffset
     * indicates the distance from the leading edge (top) of the viewport to
     * the leading edge (top) of the currentIndex.
     */
    protected void addLeadingCells(int currentIndex, double startOffset) {
        // The offset will keep track of the distance from the top of the
        // viewport to the top of the current index. We will increment it
        // as we lay out leading cells.
        double offset = startOffset;
        // The index is the absolute index of the cell being laid out
        int index = currentIndex;

        // Offset should really be the bottom of the current index
        boolean first = true; // first time in, we just fudge the offset and let
                              // it be the top of the current index then redefine
                              // it as the bottom of the current index thereafter
        // while we have not yet laid out so many cells that they would fall
        // off the flow, we will continue to create and add cells. The
        // offset is our indication of whether we can lay out additional
        // cells. If the offset is ever < 0, except in the case of the very
        // first cell, then we must quit.
        T cell = null;

        while (index >= 0 && (offset > 0 || first)) {
            cell = getAvailableCell(index);
            setCellIndex(cell, index);
            resizeCellSize(cell); // resize must be after config
            cells.addFirst(cell);

            // A little gross but better than alternatives because it reduces
            // the number of times we have to update a cell or compute its
            // size. The first time into this loop "offset" is actually the
            // top of the current index. On all subsequent visits, it is the
            // bottom of the current index.
            if (first) {
                first = false;
            } else {
                offset -= getCellLength(cell);
            }

            // Position the cell, and update the maxPrefBreadth variable as we go.
            positionCell(cell, offset);
            setMaxPrefBreadth(Math.max(getMaxPrefBreadth(), getCellBreadth(cell)));
            cell.setVisible(true);
            --index;
        }

        // There are times when after laying out the cells we discover that
        // the top of the first cell which represents index 0 is below the top
        // of the viewport. In these cases, we have to adjust the cells up
        // and reset the mapper position. This might happen when items got
        // removed at the top or when the viewport size increased.
        cell = cells.getFirst();
        int firstIndex = getCellIndex(cell);
        double firstCellPos = getCellPosition(cell);
        if (firstIndex == 0 && firstCellPos > 0) {
            setPosition(0.0f);
            offset = 0;
            for (int i = 0; i < cells.size(); i++) {
                cell = cells.get(i);
                positionCell(cell, offset);
                offset += getCellLength(cell);
            }
        }
    }

    /**
     * Adds all the trailing cells that come <em>after</em> the last index in
     * the cells ObservableList.
     */
    protected boolean addTrailingCells(boolean fillEmptyCells) {
        // If cells is empty then addLeadingCells bailed for some reason and
        // we're hosed, so just punt
        if (cells.isEmpty()) return false;
        
        // While we have not yet laid out so many cells that they would fall
        // off the flow, so we will continue to create and add cells. When the
        // offset becomes greater than the width/height of the flow, then we
        // know we cannot add any more cells.
        T startCell = cells.getLast();
        double offset = getCellPosition(startCell) + getCellLength(startCell);
        int index = getCellIndex(startCell) + 1;
        boolean filledWithNonEmpty = index <= cellCount;

        final double viewportLength = getViewportLength();

        // Fix for RT-37421, which was a regression caused by RT-36556
        if (offset < 0 && !fillEmptyCells) {
            return false;
        }

        //
        // RT-36507: viewportLength - offset gives the maximum number of
        // additional cells that should ever be able to fit in the viewport if
        // every cell had a height of 1. If index ever exceeds this count,
        // then offset is not incrementing fast enough, or at all, which means
        // there is something wrong with the cell size calculation.
        //
        final double maxCellCount = viewportLength - offset;
        while (offset < viewportLength && index < cellCount) {
            if (index >= cellCount) {
                if (offset < viewportLength) filledWithNonEmpty = false;
                if (! fillEmptyCells) return filledWithNonEmpty;
                // RT-36507 - return if we've exceeded the maximum
                if (index > maxCellCount) {
                    final PlatformLogger logger = Logging.getControlsLogger();
                    if (logger.isLoggable(PlatformLogger.Level.INFO)) {
                        if (startCell != null) {
                            logger.info("index exceeds maxCellCount. Check size calculations for " + startCell.getClass());
                        } else {
                            logger.info("index exceeds maxCellCount");
                        }
                    }
                    return filledWithNonEmpty;
                }
            }
            T cell = getAvailableCell(index);
            setCellIndex(cell, index);
            resizeCellSize(cell); // resize happens after config!
            cells.addLast(cell);

            // Position the cell and update the max pref
            positionCell(cell, offset);
            setMaxPrefBreadth(Math.max(getMaxPrefBreadth(), getCellBreadth(cell)));

            offset += getCellLength(cell);
            cell.setVisible(true);
            ++index;
        }

        // Discover whether the first cell coincides with index #0. If after
        // adding all the trailing cells we find that a) the first cell was
        // not index #0 and b) there are trailing cells, then we have a
        // problem. We need to shift all the cells down and add leading cells,
        // one at a time, until either the very last non-empty cells is aligned
        // with the bottom OR we have laid out cell index #0 at the first
        // position.
        T firstCell = cells.getFirst();
        index = getCellIndex(firstCell);
        T lastNonEmptyCell = getLastVisibleCell();
        double start = getCellPosition(firstCell);
        double end = getCellPosition(lastNonEmptyCell) + getCellLength(lastNonEmptyCell);
        if ((index != 0 || (index == 0 && start < 0)) && fillEmptyCells &&
                lastNonEmptyCell != null && getCellIndex(lastNonEmptyCell) == cellCount - 1 && end < viewportLength) {

            double prospectiveEnd = end;
            double distance = viewportLength - end;
            while (prospectiveEnd < viewportLength && index != 0 && (-start) < distance) {
                index--;
                T cell = getAvailableCell(index);
                setCellIndex(cell, index);
                resizeCellSize(cell); // resize must be after config
                cells.addFirst(cell);
                double cellLength = getCellLength(cell);
                start -= cellLength;
                prospectiveEnd += cellLength;
                positionCell(cell, start);
                setMaxPrefBreadth(Math.max(getMaxPrefBreadth(), getCellBreadth(cell)));
                cell.setVisible(true);
            }

            // The amount by which to translate the cells down
            firstCell = cells.getFirst();
            start = getCellPosition(firstCell);
            double delta = viewportLength - end;
            if (getCellIndex(firstCell) == 0 && delta > (-start)) {
                delta = (-start);
            }
            // Move things
            for (int i = 0; i < cells.size(); i++) {
                T cell = cells.get(i);
                positionCell(cell, getCellPosition(cell) + delta);
            }

            // Check whether the first cell, subsequent to our adjustments, is
            // now index #0 and aligned with the top. If so, change the position
            // to be at 0 instead of 1.
            start = getCellPosition(firstCell);
            if (getCellIndex(firstCell) == 0 && start == 0) {
                setPosition(0);
            } else if (getPosition() != 1) {
                setPosition(1);
            }
        }

        return filledWithNonEmpty;
    }

    /**
     * @return true if bar visibility changed
     */
    private boolean computeBarVisiblity() {
        if (cells.isEmpty()) {
            // In case no cells are set yet, we assume no bars are needed
            needLengthBar = false;
            needBreadthBar = false;
            return true;
        }

        final boolean isVertical = isVertical();
        boolean barVisibilityChanged = false;

        VirtualScrollBar breadthBar = isVertical ? hbar : vbar;
        VirtualScrollBar lengthBar = isVertical ? vbar : hbar;
        double breadthBarLength = snapSize(isVertical ? hbar.prefHeight(-1) : vbar.prefWidth(-1));
        double lengthBarBreadth = snapSize(isVertical ? vbar.prefWidth(-1) : hbar.prefHeight(-1));

        final double viewportBreadth = getViewportBreadth();

        final int cellsSize = cells.size();
        for (int i = 0; i < 2; i++) {
            final boolean lengthBarVisible = getPosition() > 0 || cellCount > cellsSize ||
                    (cellCount == cellsSize && (getCellPosition(cells.getLast()) + getCellLength(cells.getLast())) > getViewportLength());
            if (lengthBarVisible ^ needLengthBar) {
                needLengthBar = lengthBarVisible;
                barVisibilityChanged = true;
            }

            // second conditional removed for RT-36669.
            final boolean breadthBarVisible = (maxPrefBreadth > viewportBreadth);// || (needLengthBar && maxPrefBreadth > (viewportBreadth - lengthBarBreadth));
            if (breadthBarVisible ^ needBreadthBar) {
                needBreadthBar = breadthBarVisible;
                barVisibilityChanged = true;
            }
        }

        // Start by optimistically deciding whether the length bar and
        // breadth bar are needed and adjust the viewport dimensions
        // accordingly. If during layout we find that one or the other of the
        // bars actually is needed, then we will perform a cleanup pass

        if (!IS_TOUCH_SUPPORTED) {
            setViewportLength((isVertical ? getHeight() : getWidth()) - (needBreadthBar ? breadthBarLength : 0));
            setViewportBreadth((isVertical ? getWidth() : getHeight()) - (needLengthBar ? lengthBarBreadth : 0));

            breadthBar.setVisible(needBreadthBar);
            lengthBar.setVisible(needLengthBar);
        } else {
            breadthBar.setVisible(needBreadthBar && tempVisibility);
            lengthBar.setVisible(needLengthBar && tempVisibility);
        }

        return barVisibilityChanged;
    }

    private void initViewport() {
        // Initialize the viewportLength and viewportBreadth to match the
        // width/height of the flow
        final boolean isVertical = isVertical();
        double width = getWidth();
        double height = getHeight();
        setViewportLength(snapSize(isVertical ? height : width));
        setViewportBreadth(snapSize(isVertical ? width : height));

        VirtualScrollBar breadthBar = isVertical ? hbar : vbar;
        VirtualScrollBar lengthBar = isVertical ? vbar : hbar;

        // If there has been a switch between the virtualized bar, then we
        // will want to do some stuff TODO.
        breadthBar.setVirtual(false);
        lengthBar.setVirtual(true);
    }

    @Override protected void setWidth(double value) {
        if (value != lastWidth) {
            super.setWidth(value);
            sizeChanged = true;
            setNeedsLayout(true);
            requestLayout();
        }
    }

    @Override protected void setHeight(double value) {
        if (value != lastHeight) {
            super.setHeight(value);
            sizeChanged = true;
            setNeedsLayout(true);
            requestLayout();
        }
    }

    private void updateScrollBarsAndCells(boolean recreate) {
        // Assign the hbar and vbar to the breadthBar and lengthBar so as
        // to make some subsequent calculations easier.
        final boolean isVertical = isVertical();
        VirtualScrollBar breadthBar = isVertical ? hbar : vbar;
        VirtualScrollBar lengthBar = isVertical ? vbar : hbar;
        
        // Update cell positions.
        // When rebuilding the cells, we add the cells and along the way compute
        // the maxPrefBreadth. Based on the computed value, we may add
        // the breadth scrollbar which changes viewport length, so we need
        // to re-position the cells.
        if (!cells.isEmpty()) {
            final double currOffset = -computeViewportOffset(getPosition());
            final int currIndex = computeCurrentIndex() - cells.getFirst().getIndex();
            final int size = cells.size();

            // position leading cells
            double offset = currOffset;
            boolean first = true;
            for (int i = currIndex; i >= 0 && i < size; i--) {
                final T cell = cells.get(i);

                offset -= first ? 0.0 : getCellLength(cell);
                first = false;

                positionCell(cell, offset);
            }

            // position trailing cells
            offset = currOffset;
            for (int i = currIndex; i >= 0 && i < size; i++) {
                final T cell = cells.get(i);
                positionCell(cell, offset);

                offset += getCellLength(cell);
            }
        }

        // Toggle visibility on the corner
        corner.setVisible(breadthBar.isVisible() && lengthBar.isVisible());

        double sumCellLength = 0;
        double flowLength = (isVertical ? getHeight() : getWidth()) -
            (breadthBar.isVisible() ? breadthBar.prefHeight(-1) : 0);

        final double viewportBreadth = getViewportBreadth();
        final double viewportLength = getViewportLength();
        
        // Now position and update the scroll bars
        if (breadthBar.isVisible()) {
            /*
            ** Positioning the ScrollBar
            */
            if (!IS_TOUCH_SUPPORTED) {
                if (isVertical) {
                    hbar.resizeRelocate(0, viewportLength,
                        viewportBreadth, hbar.prefHeight(viewportBreadth));
                } else {
                    vbar.resizeRelocate(viewportLength, 0,
                        vbar.prefWidth(viewportBreadth), viewportBreadth);
                }
            }
            else {
                if (isVertical) {
                    hbar.resizeRelocate(0, (viewportLength-hbar.getHeight()),
                        viewportBreadth, hbar.prefHeight(viewportBreadth));
                } else {
                    vbar.resizeRelocate((viewportLength-vbar.getWidth()), 0,
                        vbar.prefWidth(viewportBreadth), viewportBreadth);
                }
            }

            if (getMaxPrefBreadth() != -1) {
                double newMax = Math.max(1, getMaxPrefBreadth() - viewportBreadth);
                if (newMax != breadthBar.getMax()) {
                    breadthBar.setMax(newMax);

                    double breadthBarValue = breadthBar.getValue();
                    boolean maxed = breadthBarValue != 0 && newMax == breadthBarValue;
                    if (maxed || breadthBarValue > newMax) {
                        breadthBar.setValue(newMax);
                    }

                    breadthBar.setVisibleAmount((viewportBreadth / getMaxPrefBreadth()) * newMax);
                }
            }
        }
        
        // determine how many cells there are on screen so that the scrollbar
        // thumb can be appropriately sized
        if (recreate && (lengthBar.isVisible() || IS_TOUCH_SUPPORTED)) {
            int numCellsVisibleOnScreen = 0;
            for (int i = 0, max = cells.size(); i < max; i++) {
                T cell = cells.get(i);
                if (cell != null && !cell.isEmpty()) {
                    sumCellLength += (isVertical ? cell.getHeight() : cell.getWidth());
                    if (sumCellLength > flowLength) {
                        break;
                    }

                    numCellsVisibleOnScreen++;
                }
            }

            lengthBar.setMax(1);
            if (numCellsVisibleOnScreen == 0 && cellCount == 1) {
                    // special case to help resolve RT-17701 and the case where we have
                // only a single row and it is bigger than the viewport
                lengthBar.setVisibleAmount(flowLength / sumCellLength);
            } else {
                lengthBar.setVisibleAmount(numCellsVisibleOnScreen / (float) cellCount);
            }
        }

        if (lengthBar.isVisible()) {
            // Fix for RT-11873. If this isn't here, we can have a situation where
            // the scrollbar scrolls endlessly. This is possible when the cell
            // count grows as the user hits the maximal position on the scrollbar
            // (i.e. the list size dynamically grows as the user needs more).
            //
            // This code was commented out to resolve RT-14477 after testing
            // whether RT-11873 can be recreated. It could not, and therefore
            // for now this code will remained uncommented until it is deleted
            // following further testing.
//            if (lengthBar.getValue() == 1.0 && lastCellCount != cellCount) {
//                lengthBar.setValue(0.99);
//            }

            /*
            ** Positioning the ScrollBar
            */
            if (!IS_TOUCH_SUPPORTED) {
                if (isVertical) {
                    vbar.resizeRelocate(viewportBreadth, 0, vbar.prefWidth(viewportLength), viewportLength);
                } else {
                    hbar.resizeRelocate(0, viewportBreadth, viewportLength, hbar.prefHeight(-1));
                }
            }
            else {
                if (isVertical) {
                    vbar.resizeRelocate((viewportBreadth-vbar.getWidth()), 0, vbar.prefWidth(viewportLength), viewportLength);
                } else {
                    hbar.resizeRelocate(0, (viewportBreadth-hbar.getHeight()), viewportLength, hbar.prefHeight(-1));
                }
            }
        }

        if (corner.isVisible()) {
            if (!IS_TOUCH_SUPPORTED) {
                corner.resize(vbar.getWidth(), hbar.getHeight());
                corner.relocate(hbar.getLayoutX() + hbar.getWidth(), vbar.getLayoutY() + vbar.getHeight());
            }
            else {
                corner.resize(vbar.getWidth(), hbar.getHeight());
                corner.relocate(hbar.getLayoutX() + (hbar.getWidth()-vbar.getWidth()), vbar.getLayoutY() + (vbar.getHeight()-hbar.getHeight()));
                hbar.resize(hbar.getWidth()-vbar.getWidth(), hbar.getHeight());
                vbar.resize(vbar.getWidth(), vbar.getHeight()-hbar.getHeight());
            }
        }

        clipView.resize(snapSize(isVertical ? viewportBreadth : viewportLength),
                        snapSize(isVertical ? viewportLength : viewportBreadth));

        // We may have adjusted the viewport length and breadth after the
        // layout due to scroll bars becoming visible. So we need to perform
        // a follow up pass and resize and shift all the cells to fit the
        // viewport. Note that the prospective viewport size is always >= the
        // final viewport size, so we don't have to worry about adding
        // cells during this cleanup phase.
        fitCells();

        // If the viewportLength becomes large enough that all cells fit
        // within the viewport, then we want to update the value to match.
        if (getPosition() != lengthBar.getValue()) {
            lengthBar.setValue(getPosition());
        }
    }

    /**
     * Adjusts the cells location and size if necessary. The breadths of all
     * cells will be adjusted to fit the viewportWidth or maxPrefBreadth, and
     * the layout position will be updated if necessary based on index and
     * offset.
     */
    private void fitCells() {
        double size = Math.max(getMaxPrefBreadth(), getViewportBreadth());
        boolean isVertical = isVertical();

        // Note: Do not optimise this loop by pre-calculating the cells size and
        // storing that into a int value - this can lead to RT-32828
        for (int i = 0; i < cells.size(); i++) {
            Cell<?> cell = cells.get(i);
            if (isVertical) {
                cell.resize(size, cell.getHeight());
            } else {
                cell.resize(cell.getWidth(), size);
            }
        }
    }

    private void cull() {
        final double viewportLength = getViewportLength();
        for (int i = cells.size() - 1; i >= 0; i--) {
            T cell = cells.get(i);
            double cellSize = getCellLength(cell);
            double cellStart = getCellPosition(cell);
            double cellEnd = cellStart + cellSize;
            if (cellStart > viewportLength || cellEnd < 0) {
                addToPile(cells.remove(i));
            }
        }
    }

    /***************************************************************************
     *                                                                         *
     *                Helper functions for working with cells                  *
     *                                                                         *
     **************************************************************************/

    /**
     * Return the index for a given cell. This allows subclasses to customise
     * how cell indices are retrieved.
     */
    protected int getCellIndex(T cell){
        return cell.getIndex();
    }


    /**
     * Return a cell for the given index. This may be called for any cell,
     * including beyond the range defined by cellCount, in which case an
     * empty cell will be returned. The returned value should not be stored for
     * any reason.
     */
    public T getCell(int index) {
        // If there are cells, then we will attempt to get an existing cell
        if (! cells.isEmpty()) {
            // First check the cells that have already been created and are
            // in use. If this call returns a value, then we can use it
            T cell = getVisibleCell(index);
            if (cell != null) return cell;
        }

        // check the pile
        for (int i = 0; i < pile.size(); i++) {
            T cell = pile.get(i);
            if (getCellIndex(cell) == index) {
                // Note that we don't remove from the pile: if we do it leads
                // to a severe performance decrease. This seems to be OK, as
                // getCell() is only used for cell measurement purposes.
                // pile.remove(i);
                return cell;
            }
        }

        if (pile.size() > 0) {
            return pile.get(0);
        }

        // We need to use the accumCell and return that
        if (accumCell == null) {
            Callback<VirtualFlow,T> createCell = getCreateCell();
            if (createCell != null) {
                accumCell = createCell.call(this);
                accumCellParent.getChildren().setAll(accumCell);
            }
        }
        setCellIndex(accumCell, index);
        resizeCellSize(accumCell);
        return accumCell;
    }

    /**
     * After using the accum cell, it needs to be released!
     */
    private void releaseCell(T cell) {
        if (accumCell != null && cell == accumCell) {
            accumCell.updateIndex(-1);
        }
    }

    /**
     * This method is an experts-only method - if the requested index is not
     * already an existing visible cell, it will create a cell for the
     * given index and insert it into the sheet. From that point on it will be
     * unmanaged, and is up to the caller of this method to manage it.
     */
    T getPrivateCell(int index)  {
        T cell = null;

        // If there are cells, then we will attempt to get an existing cell
        if (! cells.isEmpty()) {
            // First check the cells that have already been created and are
            // in use. If this call returns a value, then we can use it
            cell = getVisibleCell(index);
            if (cell != null) return cell;
        }

        // check the existing sheet children
        if (cell == null) {
            for (int i = 0; i < sheetChildren.size(); i++) {
                T _cell = (T) sheetChildren.get(i);
                if (getCellIndex(_cell) == index) {
                    return _cell;
                }
            }
        }

        if (cell == null) {
            Callback<VirtualFlow, T> createCell = getCreateCell();
            if (createCell != null) {
                cell = createCell.call(this);
            }
        }

        if (cell != null) {
            setCellIndex(cell, index);
            resizeCellSize(cell);
            cell.setVisible(false);
            sheetChildren.add(cell);
            privateCells.add(cell);
        }

        return cell;
    }

    private final List<T> privateCells = new ArrayList<>();

    private void releaseAllPrivateCells() {
        sheetChildren.removeAll(privateCells);
    }

    /**
     * Compute and return the length of the cell for the given index. This is
     * called both internally when adjusting by pixels, and also at times
     * by PositionMapper (see the getItemSize callback). When called by
     * PositionMapper, it is possible that it will be called for some index
     * which is not associated with any cell, so we have to do a bit of work
     * to use a cell as a helper for computing cell size in some cases.
     */
    protected double getCellLength(int index) {
        if (fixedCellSizeEnabled) return fixedCellSize;
        
        T cell = getCell(index);
        double length = getCellLength(cell);
        releaseCell(cell);
        return length;
    }

    /**
     */
    protected double getCellBreadth(int index) {
        T cell = getCell(index);
        double b = getCellBreadth(cell);
        releaseCell(cell);
        return b;
    }

    /**
     * Gets the length of a specific cell
     */
    protected double getCellLength(T cell) {
        if (cell == null) return 0;
        if (fixedCellSizeEnabled) return fixedCellSize;

        return isVertical() ?
            cell.getLayoutBounds().getHeight()
            : cell.getLayoutBounds().getWidth();
    }

//    private double getCellPrefLength(T cell) {
//        return isVertical() ?
//            cell.prefHeight(-1)
//            : cell.prefWidth(-1);
//    }

    /**
     * Gets the breadth of a specific cell
     */
    protected double getCellBreadth(Cell cell) {
        return isVertical() ?
            cell.prefWidth(-1)
            : cell.prefHeight(-1);
    }

    /**
     * Gets the layout position of the cell along the length axis
     */
    protected double getCellPosition(T cell) {
        if (cell == null) return 0;

        return isVertical() ?
            cell.getLayoutY()
            : cell.getLayoutX();
    }

    protected void positionCell(T cell, double position) {
        if (isVertical()) {
            cell.setLayoutX(0);
            cell.setLayoutY(snapSize(position));
        } else {
            cell.setLayoutX(snapSize(position));
            cell.setLayoutY(0);
        }
    }

    protected void resizeCellSize(T cell) {
        if (cell == null) return;
        
        if (isVertical()) {
            double width = Math.max(getMaxPrefBreadth(), getViewportBreadth());
            cell.resize(width, fixedCellSizeEnabled ? fixedCellSize : Utils.boundedSize(cell.prefHeight(width), cell.minHeight(width), cell.maxHeight(width)));
        } else {
            double height = Math.max(getMaxPrefBreadth(), getViewportBreadth());
            cell.resize(fixedCellSizeEnabled ? fixedCellSize : Utils.boundedSize(cell.prefWidth(height), cell.minWidth(height), cell.maxWidth(height)), height);
        }
    }

    protected void setCellIndex(T cell, int index) {
        assert cell != null;

        cell.updateIndex(index);

        // make sure the cell is sized correctly. This is important for both
        // general layout of cells in a VirtualFlow, but also in cases such as
        // RT-34333, where the sizes were being reported incorrectly to the
        // ComboBox popup.
        if (cell.isNeedsLayout() && cell.getScene() != null) {
            cell.applyCss();
        }
    }

    /***************************************************************************
     *                                                                         *
     *                 Helper functions for cell management                    *
     *                                                                         *
     **************************************************************************/

    /**
     * Get a cell which can be used in the layout. This function will reuse
     * cells from the pile where possible, and will create new cells when
     * necessary.
     */
    protected T getAvailableCell(int prefIndex) {
        T cell = null;
        
        // Fix for RT-12822. We try to retrieve the cell from the pile rather
        // than just grab a random cell from the pile (or create another cell).
        for (int i = 0, max = pile.size(); i < max; i++) {
            T _cell = pile.get(i);
            assert _cell != null;
            
            if (getCellIndex(_cell) == prefIndex) {
                cell = _cell;
                pile.remove(i);
                break;
            }
            cell = null;
        }

        if (cell == null) {
            if (pile.size() > 0) {
                // we try to get a cell with an index that is the same even/odd
                // as the prefIndex. This saves us from having to run so much
                // css on the cell as it will not change from even to odd, or
                // vice versa
                final boolean prefIndexIsEven = (prefIndex & 1) == 0;
                for (int i = 0, max = pile.size(); i < max; i++) {
                    final T c = pile.get(i);
                    final int cellIndex = getCellIndex(c);

                    if ((cellIndex & 1) == 0 && prefIndexIsEven) {
                        cell = c;
                        pile.remove(i);
                        break;
                    } else if ((cellIndex & 1) == 1 && ! prefIndexIsEven) {
                        cell = c;
                        pile.remove(i);
                        break;
                    }
                }

                if (cell == null) {
                    cell = pile.removeFirst();
                }
            } else {
                cell = getCreateCell().call(this);
            }
        }

        if (cell.getParent() == null) {
            sheetChildren.add(cell);
        }
        
        return cell;
    }

    // protected to allow subclasses to clean up
    protected void addAllToPile() {
        for (int i = 0, max = cells.size(); i < max; i++) {
            addToPile(cells.removeFirst());
        }
    }

    /**
     * Puts the given cell onto the pile. This is called whenever a cell has
     * fallen off the flow's start.
     */
    private void addToPile(T cell) {
        assert cell != null;
        pile.addLast(cell);
    }

    private void cleanPile() {
        boolean wasFocusOwner = false;

        for (int i = 0, max = pile.size(); i < max; i++) {
            T cell = pile.get(i);
            wasFocusOwner = wasFocusOwner || doesCellContainFocus(cell);
            cell.setVisible(false);
        }

        // Fix for RT-35876: Rather than have the cells do weird things with
        // focus (in particular, have focus jump between cells), we return focus
        // to the VirtualFlow itself.
        if (wasFocusOwner) {
            requestFocus();
        }
    }

    private boolean doesCellContainFocus(Cell<?> c) {
        Scene scene = c.getScene();
        final Node focusOwner = scene == null ? null : scene.getFocusOwner();

        if (focusOwner != null) {
            if (c.equals(focusOwner)) {
                return true;
            }

            Parent p = focusOwner.getParent();
            while (p != null && ! (p instanceof VirtualFlow)) {
                if (c.equals(p)) {
                    return true;
                }
                p = p.getParent();
            }
        }

        return false;
    }

    /**
     * Gets a cell for the given index if the cell has been created and laid out.
     * "Visible" is a bit of a misnomer, the cell might not be visible in the
     * viewport (it may be clipped), but does distinguish between cells that
     * have been created and are in use vs. those that are in the pile or
     * not created.
     */
    public T getVisibleCell(int index) {
        if (cells.isEmpty()) return null;

        // check the last index
        T lastCell = cells.getLast();
        int lastIndex = getCellIndex(lastCell);
        if (index == lastIndex) return lastCell;

        // check the first index
        T firstCell = cells.getFirst();
        int firstIndex = getCellIndex(firstCell);
        if (index == firstIndex) return firstCell;

        // if index is > firstIndex and < lastIndex then we can get the index
        if (index > firstIndex && index < lastIndex) {
            T cell = cells.get(index - firstIndex);
            if (getCellIndex(cell) == index) return cell;
        }

        // there is no visible cell for the specified index
        return null;
    }

    /**
     * Locates and returns the last non-empty IndexedCell that is currently
     * partially or completely visible. This function may return null if there
     * are no cells, or if the viewport length is 0.
     */
    public T getLastVisibleCell() {
        if (cells.isEmpty() || getViewportLength() <= 0) return null;

        T cell;
        for (int i = cells.size() - 1; i >= 0; i--) {
            cell = cells.get(i);
            if (! cell.isEmpty()) {
                return cell;
            }
        }

        return null;
    }

    /**
     * Locates and returns the first non-empty IndexedCell that is partially or
     * completely visible. This really only ever returns null if there are no
     * cells or the viewport length is 0.
     */
    public T getFirstVisibleCell() {
        if (cells.isEmpty() || getViewportLength() <= 0) return null;
        T cell = cells.getFirst();
        return cell.isEmpty() ? null : cell;
    }

    // Returns last visible cell whose bounds are entirely within the viewport
    public T getLastVisibleCellWithinViewPort() {
        if (cells.isEmpty() || getViewportLength() <= 0) return null;

        T cell;
        final double max = getViewportLength();
        for (int i = cells.size() - 1; i >= 0; i--) {
            cell = cells.get(i);
            if (cell.isEmpty()) continue;

            final double cellStart = getCellPosition(cell);
            final double cellEnd = cellStart + getCellLength(cell);

            // we use the magic +2 to allow for a little bit of fuzziness,
            // this is to help in situations such as RT-34407
            if (cellEnd <= (max + 2)) {
                return cell;
            }
        }

        return null;
    }

    // Returns first visible cell whose bounds are entirely within the viewport
    public T getFirstVisibleCellWithinViewPort() {
        if (cells.isEmpty() || getViewportLength() <= 0) return null;

        T cell;
        for (int i = 0; i < cells.size(); i++) {
            cell = cells.get(i);
            if (cell.isEmpty()) continue;

            final double cellStart = getCellPosition(cell);
            if (cellStart >= 0) {
                return cell;
            }
        }

        return null;
    }

    /**
     * Adjust the position of cells so that the specified cell
     * will be positioned at the start of the viewport. The given cell must
     * already be "live". This is bad public API!
     */
    public void showAsFirst(T firstCell) {
        if (firstCell != null) {
            adjustPixels(getCellPosition(firstCell));
        }
    }

    /**
     * Adjust the position of cells so that the specified cell
     * will be positioned at the end of the viewport. The given cell must
     * already be "live". This is bad public API!
     */
    public void showAsLast(T lastCell) {
        if (lastCell != null) {
            adjustPixels(getCellPosition(lastCell) + getCellLength(lastCell) - getViewportLength());
        }
    }

    /**
     * Adjusts the cells such that the selected cell will be fully visible in
     * the viewport (but only just).
     */
    public void show(T cell) {
        if (cell != null) {
            final double start = getCellPosition(cell);
            final double length = getCellLength(cell);
            final double end = start + length;
            final double viewportLength = getViewportLength();

            if (start < 0) {
                adjustPixels(start);
            } else if (end > viewportLength) {
                adjustPixels(end - viewportLength);
            }
        }
    }

    public void show(int index) {
        T cell = getVisibleCell(index);
        if (cell != null) {
            show(cell);
        } else {
            // See if the previous index is a visible cell
            T prev = getVisibleCell(index - 1);
            if (prev != null) {
                // Need to add a new cell and then we can show it
//                layingOut = true;
                cell = getAvailableCell(index);
                setCellIndex(cell, index);
                resizeCellSize(cell); // resize must be after config
                cells.addLast(cell);
                positionCell(cell, getCellPosition(prev) + getCellLength(prev));
                setMaxPrefBreadth(Math.max(getMaxPrefBreadth(), getCellBreadth(cell)));
                cell.setVisible(true);
                show(cell);
//                layingOut = false;
                return;
            }
            // See if the next index is a visible cell
            T next = getVisibleCell(index + 1);
            if (next != null) {
//                layingOut = true;
                cell = getAvailableCell(index);
                setCellIndex(cell, index);
                resizeCellSize(cell); // resize must be after config
                cells.addFirst(cell);
                positionCell(cell, getCellPosition(next) - getCellLength(cell));
                setMaxPrefBreadth(Math.max(getMaxPrefBreadth(), getCellBreadth(cell)));
                cell.setVisible(true);
                show(cell);
//                layingOut = false;
                return;
            }

            // In this case, we're asked to show a random cell
//            layingOut = true;
            adjustPositionToIndex(index);
            addAllToPile();
            requestLayout();
//            layingOut = false;            
        }
    }

    public void scrollTo(int index) {
        boolean posSet = false;
        
        if (index >= cellCount - 1) {
            setPosition(1);
            posSet = true;
        } else if (index < 0) {
            setPosition(0);
            posSet = true;
        }
        
        if (! posSet) {
            adjustPositionToIndex(index);
            double offset = - computeOffsetForCell(index);
            adjustByPixelAmount(offset);
        }
        
        requestLayout();        
    }
    
    //TODO We assume all the cell have the same length.  We will need to support
    // cells of different lengths.
    public void scrollToOffset(int offset) {
        adjustPixels(offset * getCellLength(0));
    }    
    
    /**
     * Given a delta value representing a number of pixels, this method attempts
     * to move the VirtualFlow in the given direction (positive is down/right,
     * negative is up/left) the given number of pixels. It returns the number of
     * pixels actually moved.
     */
    public double adjustPixels(final double delta) {
        // Short cut this method for cases where nothing should be done
        if (delta == 0) return 0;

        final boolean isVertical = isVertical();
        if (((isVertical && (tempVisibility ? !needLengthBar : !vbar.isVisible())) ||
                (! isVertical && (tempVisibility ? !needLengthBar : !hbar.isVisible())))) return 0;
        
        double pos = getPosition();
        if (pos == 0.0f && delta < 0) return 0;
        if (pos == 1.0f && delta > 0) return 0;

        adjustByPixelAmount(delta);
        if (pos == getPosition()) {
            // The pos hasn't changed, there's nothing to do. This is likely
            // to occur when we hit either extremity
            return 0;
        }

        // Now move stuff around. Translating by pixels fundamentally means
        // moving the cells by the delta. However, after having
        // done that, we need to go through the cells and see which cells,
        // after adding in the translation factor, now fall off the viewport.
        // Also, we need to add cells as appropriate to the end (or beginning,
        // depending on the direction of travel).
        //
        // One simplifying assumption (that had better be true!) is that we
        // will only make it this far in the function if the virtual scroll
        // bar is visible. Otherwise, we never will pixel scroll. So as we go,
        // if we find that the maxPrefBreadth exceeds the viewportBreadth,
        // then we will be sure to show the breadthBar and update it
        // accordingly.
        if (cells.size() > 0) {
            for (int i = 0; i < cells.size(); i++) {
                T cell = cells.get(i);
                assert cell != null;
                positionCell(cell, getCellPosition(cell) - delta);
            }

            // Fix for RT-32908
            T firstCell = cells.getFirst();
            double layoutY = firstCell == null ? 0 : getCellPosition(firstCell);
            for (int i = 0; i < cells.size(); i++) {
                T cell = cells.get(i);
                assert cell != null;
                double actualLayoutY = getCellPosition(cell);
                if (actualLayoutY != layoutY) {
                    // we need to shift the cell to layoutY
                    positionCell(cell, layoutY);
                }

                layoutY += getCellLength(cell);
            }
            // end of fix for RT-32908

            // Add any necessary leading cells
            if (firstCell != null) {
                int firstIndex = getCellIndex(firstCell);
                double prevIndexSize = getCellLength(firstIndex - 1);
                addLeadingCells(firstIndex - 1, getCellPosition(firstCell) - prevIndexSize);
            } else {
                int currentIndex = computeCurrentIndex();

                // The distance from the top of the viewport to the top of the
                // cell for the current index.
                double offset = -computeViewportOffset(getPosition());

                // Add all the leading and trailing cells (the call to add leading
                // cells will add the current cell as well -- that is, the one that
                // represents the current position on the mapper).
                addLeadingCells(currentIndex, offset);
            }

            // Starting at the tail of the list, loop adding cells until
            // all the space on the table is filled up. We want to make
            // sure that we DO NOT add empty trailing cells (since we are
            // in the full virtual case and so there are no trailing empty
            // cells).
            if (! addTrailingCells(true)) {
                // Reached the end, but not enough cells to fill up to
                // the end. So, remove the trailing empty space, and translate
                // the cells down
                final T lastCell = getLastVisibleCell();
                final double lastCellSize = getCellLength(lastCell);
                final double cellEnd = getCellPosition(lastCell) + lastCellSize;
                final double viewportLength = getViewportLength();

                if (cellEnd < viewportLength) {
                    // Reposition the nodes
                    double emptySize = viewportLength - cellEnd;
                    for (int i = 0; i < cells.size(); i++) {
                        T cell = cells.get(i);
                        positionCell(cell, getCellPosition(cell) + emptySize);
                    }
                    setPosition(1.0f);
                }
            }
        }

        // Now throw away any cells that don't fit
        cull();

        // Finally, update the scroll bars
        updateScrollBarsAndCells(false);
        lastPosition = getPosition();

        // notify
        return delta; // TODO fake
    }

    private boolean needsReconfigureCells = false; // when cell contents are the same
    private boolean needsRecreateCells = false; // when cell factory changed
    private boolean needsRebuildCells = false; // when cell contents have changed
    private boolean needsCellsLayout = false;
    private boolean sizeChanged = false;
    private final BitSet dirtyCells = new BitSet();
    
    public void reconfigureCells() {
        needsReconfigureCells = true;
        requestLayout();
    }

    public void recreateCells() {
        needsRecreateCells = true;
        requestLayout();
    }
    
    public void rebuildCells() {
        needsRebuildCells = true;
        requestLayout();
    }

    public void requestCellLayout() {
        needsCellsLayout = true;
        requestLayout();
    }

    public void setCellDirty(int index) {
        dirtyCells.set(index);
        requestLayout();
    }

    private static final double GOLDEN_RATIO_MULTIPLIER = 0.618033987;

    private double getPrefBreadth(double oppDimension) {
        double max = getMaxCellWidth(10);

        // This primarily exists for the case where we do not want the breadth
        // to grow to ensure a golden ratio between width and height (for example,
        // when a ListView is used in a ComboBox - the width should not grow
        // just because items are being added to the ListView)
        if (oppDimension > -1) {
            double prefLength = getPrefLength();
            max = Math.max(max, prefLength * GOLDEN_RATIO_MULTIPLIER);
        }
        
        return max;
    }

    private double getPrefLength() {
        double sum = 0.0;
        int rows = Math.min(10, cellCount);
        for (int i = 0; i < rows; i++) {
            sum += getCellLength(i);
        }
        return sum;
    }

    @Override protected double computePrefWidth(double height) {
        double w = isVertical() ? getPrefBreadth(height) : getPrefLength();
        return w + vbar.prefWidth(-1);
    }

    @Override protected double computePrefHeight(double width) {
        double h = isVertical() ? getPrefLength() : getPrefBreadth(width);
        return h + hbar.prefHeight(-1);
    }
    
    double getMaxCellWidth(int rowsToCount) {
        double max = 0.0;
        
        // we always measure at least one row
        int rows = Math.max(1, rowsToCount == -1 ? cellCount : rowsToCount); 
        for (int i = 0; i < rows; i++) {
            max = Math.max(max, getCellBreadth(i));
        }
        return max;
    }
    
    
    
    // Old PositionMapper
    /**
     * Given a position value between 0 and 1, compute and return the viewport
     * offset from the "current" cell associated with that position value.
     * That is, if the return value of this function where used as a translation
     * factor for a sheet that contained all the items, then the current
     * item would end up positioned correctly.
     */
    private double computeViewportOffset(double position) {
        double p = com.sun.javafx.Utils.clamp(0, position, 1);
        double fractionalPosition = p * getCellCount();
        int cellIndex = (int) fractionalPosition;
        double fraction = fractionalPosition - cellIndex;
        double cellSize = getCellLength(cellIndex);
        double pixelOffset = cellSize * fraction;
        double viewportOffset = getViewportLength() * p;
        return pixelOffset - viewportOffset;
    }

    private void adjustPositionToIndex(int index) {
        int cellCount = getCellCount();
        if (cellCount <= 0) {
            setPosition(0.0f);
        } else {            
            setPosition(((double)index) / cellCount);
        }
    }

    /**
     * Adjust the position based on a delta of pixels. If negative, then the
     * position will be adjusted negatively. If positive, then the position will
     * be adjusted positively. If the pixel amount is too great for the range of
     * the position, then it will be clamped such that position is always
     * strictly between 0 and 1
     */
    private void adjustByPixelAmount(double numPixels) {
        if (numPixels == 0) return;
        // Starting from the current cell, we move in the direction indicated
        // by numPixels one cell at a team. For each cell, we discover how many
        // pixels the "position" line would move within that cell, and adjust
        // our count of numPixels accordingly. When we come to the "final" cell,
        // then we can take the remaining number of pixels and multiply it by
        // the "travel rate" of "p" within that cell to get the delta. Add
        // the delta to "p" to get position.

        // get some basic info about the list and the current cell
        boolean forward = numPixels > 0;
        int cellCount = getCellCount();
        double fractionalPosition = getPosition() * cellCount;
        int cellIndex = (int) fractionalPosition;
        if (forward && cellIndex == cellCount) return;
        double cellSize = getCellLength(cellIndex);
        double fraction = fractionalPosition - cellIndex;
        double pixelOffset = cellSize * fraction;

        // compute the percentage of "position" that represents each cell
        double cellPercent = 1.0 / cellCount;

        // To help simplify the algorithm, we pretend as though the current
        // position is at the beginning of the current cell. This reduces some
        // of the corner cases and provides a simpler algorithm without adding
        // any overhead to performance.
        double start = computeOffsetForCell(cellIndex);
        double end = cellSize + computeOffsetForCell(cellIndex + 1);

        // We need to discover the distance that the fictional "position line"
        // would travel within this cell, from its current position to the end.
        double remaining = end - start;

        // Keep track of the number of pixels left to travel
        double n = forward ?
              numPixels + pixelOffset - (getViewportLength() * getPosition()) - start
            : -numPixels + end - (pixelOffset - (getViewportLength() * getPosition()));

        // "p" represents the most recent value for position. This is always
        // based on the edge between two cells, except at the very end of the
        // algorithm where it is added to the computed "p" offset for the final
        // value of Position.
        double p = cellPercent * cellIndex;

        // Loop over the cells one at a time until either we reach the end of
        // the cells, or we find that the "n" will fall within the cell we're on
        while (n > remaining && ((forward && cellIndex < cellCount - 1) || (! forward && cellIndex > 0))) {
            if (forward) cellIndex++; else cellIndex--;
            n -= remaining;
            cellSize = getCellLength(cellIndex);
            start = computeOffsetForCell(cellIndex);
            end = cellSize + computeOffsetForCell(cellIndex + 1);
            remaining = end - start;
            p = cellPercent * cellIndex;
        }

        // if remaining is < n, then we must have hit an end, so as a
        // fast path, we can just set position to 1.0 or 0.0 and return
        // because we know we hit the end
        if (n > remaining) {
            setPosition(forward ? 1.0f : 0.0f);
        } else if (forward) {
            double rate = cellPercent / Math.abs(end - start);
            setPosition(p + (rate * n));
        } else {
            double rate = cellPercent / Math.abs(end - start);
            setPosition((p + cellPercent) - (rate * n));
        }
    }

    private int computeCurrentIndex() {
        return (int) (getPosition() * getCellCount());
    }

    /**
     * Given an item index, this function will compute and return the viewport
     * offset from the beginning of the specified item. Notice that because each
     * item has the same percentage of the position dedicated to it, and since
     * we are measuring from the start of each item, this is a very simple
     * calculation.
     */
    private double computeOffsetForCell(int itemIndex) {
        double cellCount = getCellCount();
        double p = com.sun.javafx.Utils.clamp(0, itemIndex, cellCount) / cellCount;
        return -(getViewportLength() * p);
    }
    
//    /**
//     * Adjust the position based on a chunk of pixels. The position is based
//     * on the start of the scrollbar position.
//     */
//    private void adjustByPixelChunk(double numPixels) {
//        setPosition(0);
//        adjustByPixelAmount(numPixels);
//    }
    // end of old PositionMapper code
    
    
    /**
     * A simple extension to Region that ensures that anything wanting to flow
     * outside of the bounds of the Region is clipped.
     */
    static class ClippedContainer extends Region {

        /**
         * The Node which is embedded within this {@code ClipView}.
         */
        private Node node;
        public Node getNode() { return this.node; }
        public void setNode(Node n) {
            this.node = n;

            getChildren().clear();
            getChildren().add(node);
        }

        public void setClipX(double clipX) {
            setLayoutX(-clipX);
            clipRect.setLayoutX(clipX);
        }

        public void setClipY(double clipY) {
            setLayoutY(-clipY);
            clipRect.setLayoutY(clipY);
        }

        private final Rectangle clipRect;

        public ClippedContainer(final VirtualFlow<?> flow) {
            if (flow == null) {
                throw new IllegalArgumentException("VirtualFlow can not be null");
            }

            getStyleClass().add("clipped-container");

            // clipping
            clipRect = new Rectangle();
            clipRect.setSmooth(false);
            setClip(clipRect);
            // --- clipping
            
            super.widthProperty().addListener(valueModel -> {
                clipRect.setWidth(getWidth());
            });
            super.heightProperty().addListener(valueModel -> {
                clipRect.setHeight(getHeight());
            });
        }
    }

    /**
     * A List-like implementation that is exceedingly efficient for the purposes
     * of the VirtualFlow. Typically there is not much variance in the number of
     * cells -- it is always some reasonably consistent number. Yet for efficiency
     * in code, we like to use a linked list implementation so as to append to
     * start or append to end. However, at times when we need to iterate, LinkedList
     * is expensive computationally as well as requiring the construction of
     * temporary iterators.
     * <p>
     * This linked list like implementation is done using an array. It begins by
     * putting the first item in the center of the allocated array, and then grows
     * outward (either towards the first or last of the array depending on whether
     * we are inserting at the head or tail). It maintains an index to the start
     * and end of the array, so that it can efficiently expose iteration.
     * <p>
     * This class is package private solely for the sake of testing.
     */
    public static class ArrayLinkedList<T> extends AbstractList<T> {
        /**
         * The array list backing this class. We default the size of the array
         * list to be fairly large so as not to require resizing during normal
         * use, and since that many ArrayLinkedLists won't be created it isn't
         * very painful to do so.
         */
        private final ArrayList<T> array;

        private int firstIndex = -1;
        private int lastIndex = -1;

        public ArrayLinkedList() {
            array = new ArrayList<T>(50);

            for (int i = 0; i < 50; i++) {
                array.add(null);
            }
        }

        public T getFirst() {
            return firstIndex == -1 ? null : array.get(firstIndex);
        }

        public T getLast() {
            return lastIndex == -1 ? null : array.get(lastIndex);
        }

        public void addFirst(T cell) {
            // if firstIndex == -1 then that means this is the first item in the
            // list and we need to initialize firstIndex and lastIndex
            if (firstIndex == -1) {
                firstIndex = lastIndex = array.size() / 2;
                array.set(firstIndex, cell);
            } else if (firstIndex == 0) {
                // we're already at the head of the array, so insert at position
                // 0 and then increment the lastIndex to compensate
                array.add(0, cell);
                lastIndex++;
            } else {
                // we're not yet at the head of the array, so insert at the
                // firstIndex - 1 position and decrement first position
                array.set(--firstIndex, cell);
            }
        }

        public void addLast(T cell) {
            // if lastIndex == -1 then that means this is the first item in the
            // list and we need to initialize the firstIndex and lastIndex
            if (firstIndex == -1) {
                firstIndex = lastIndex = array.size() / 2;
                array.set(lastIndex, cell);
            } else if (lastIndex == array.size() - 1) {
                // we're at the end of the array so need to "add" so as to force
                // the array to be expanded in size
                array.add(++lastIndex, cell);
            } else {
                array.set(++lastIndex, cell);
            }
        }

        public int size() {
            return firstIndex == -1 ? 0 : lastIndex - firstIndex + 1;
        }

        public boolean isEmpty() {
            return firstIndex == -1;
        }

        public T get(int index) {
            if (index > (lastIndex - firstIndex) || index < 0) {
                // Commented out exception due to RT-29111
                // throw new java.lang.ArrayIndexOutOfBoundsException();
                return null;
            }

            return array.get(firstIndex + index);
        }

        public void clear() {
            for (int i = 0; i < array.size(); i++) {
                array.set(i, null);
            }

            firstIndex = lastIndex = -1;
        }

        public T removeFirst() {
            if (isEmpty()) return null;
            return remove(0);
        }

        public T removeLast() {
            if (isEmpty()) return null;
            return remove(lastIndex - firstIndex);
        }

        public T remove(int index) {
            if (index > lastIndex - firstIndex || index < 0) {
                throw new java.lang.ArrayIndexOutOfBoundsException();
            }

            // if the index == 0, then we're removing the first
            // item and can simply set it to null in the array and increment
            // the firstIndex unless there is only one item, in which case
            // we have to also set first & last index to -1.
            if (index == 0) {
                T cell = array.get(firstIndex);
                array.set(firstIndex, null);
                if (firstIndex == lastIndex) {
                    firstIndex = lastIndex = -1;
                } else {
                    firstIndex++;
                }
                return cell;
            } else if (index == lastIndex - firstIndex) {
                // if the index == lastIndex - firstIndex, then we're removing the
                // last item and can simply set it to null in the array and
                // decrement the lastIndex
                T cell = array.get(lastIndex);
                array.set(lastIndex--, null);
                return cell;
            } else {
                // if the index is somewhere in between, then we have to remove the
                // item and decrement the lastIndex
                T cell = array.get(firstIndex + index);
                array.set(firstIndex + index, null);
                for (int i = (firstIndex + index + 1); i <= lastIndex; i++) {
                    array.set(i - 1, array.get(i));
                }
                array.set(lastIndex--, null);
                return cell;
            }
        }
    }

    Timeline sbTouchTimeline;
    KeyFrame sbTouchKF1;
    KeyFrame sbTouchKF2;

    private boolean needBreadthBar;
    private boolean needLengthBar;
    private boolean tempVisibility = false;

    protected void startSBReleasedAnimation() {
        if (sbTouchTimeline == null) {
            /*
            ** timeline to leave the scrollbars visible for a short
            ** while after a scroll/drag
            */
            sbTouchTimeline = new Timeline();
            sbTouchKF1 = new KeyFrame(Duration.millis(0), event -> {
                tempVisibility = true;
                requestLayout();
            });

            sbTouchKF2 = new KeyFrame(Duration.millis(1000), event -> {
                if (touchDetected == false && mouseDown == false) {
                    tempVisibility = false;
                    requestLayout();
                }
            });
            sbTouchTimeline.getKeyFrames().addAll(sbTouchKF1, sbTouchKF2);
        }
        sbTouchTimeline.playFromStart();
    }

    protected void scrollBarOn() {
        tempVisibility = true;
        requestLayout();
    }
}
