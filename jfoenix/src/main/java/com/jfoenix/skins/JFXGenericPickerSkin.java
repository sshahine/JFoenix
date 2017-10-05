package com.jfoenix.skins;

import com.jfoenix.controls.behavior.JFXGenericPickerBehavior;
import com.sun.javafx.binding.ExpressionHelper;
import com.sun.javafx.event.EventHandlerManager;
import com.sun.javafx.stage.WindowEventDispatcher;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ComboBoxBaseSkin;
import javafx.scene.control.skin.ComboBoxPopupControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class JFXGenericPickerSkin<T> extends ComboBoxPopupControl<T>{

    private final EventHandler<MouseEvent> mouseEnteredEventHandler;
    private final EventHandler<MouseEvent> mousePressedEventHandler;
    private final EventHandler<MouseEvent> mouseReleasedEventHandler;
    private final EventHandler<MouseEvent> mouseExitedEventHandler;

    protected JFXGenericPickerBehavior<T> behavior;

    // reference of the arrow button node in getChildren (not the actual field)
    protected Pane arrowButton;
    protected PopupControl popup;

    public JFXGenericPickerSkin(ComboBoxBase<T> comboBoxBase) {
        super(comboBoxBase);
        behavior = new JFXGenericPickerBehavior<T>(comboBoxBase);

        removeParentFakeFocusListener(comboBoxBase);

        this.mouseEnteredEventHandler = event -> behavior.mouseEntered(event);
        this.mousePressedEventHandler = event -> {
            behavior.mousePressed(event);
            event.consume();
        };
        this.mouseReleasedEventHandler = event -> {
            behavior.mouseReleased(event);
            event.consume();
        };
        this.mouseExitedEventHandler = event -> behavior.mouseExited(event);

        arrowButton = (Pane) getChildren().get(0);

        parentArrowEventHandlerTerminator.accept("mouseEnteredEventHandler", MouseEvent.MOUSE_ENTERED);
        parentArrowEventHandlerTerminator.accept("mousePressedEventHandler", MouseEvent.MOUSE_PRESSED);
        parentArrowEventHandlerTerminator.accept("mouseReleasedEventHandler", MouseEvent.MOUSE_RELEASED);
        parentArrowEventHandlerTerminator.accept("mouseExitedEventHandler", MouseEvent.MOUSE_EXITED);
        this.unregisterChangeListeners(comboBoxBase.editableProperty());

        updateArrowButtonListeners();
        registerChangeListener(comboBoxBase.editableProperty(), obs->{
            updateArrowButtonListeners();
            reflectUpdateDisplayArea();
        });

        removeParentPopupHandlers();

        popup = (PopupControl) fieldConsumer.apply(()->ComboBoxPopupControl.class.getDeclaredField("popup"), this);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.behavior != null) {
            this.behavior.dispose();
        }
    }


    /***************************************************************************
     *                                                                         *
     * Reflections internal API                                                *
     *                                                                         *
     **************************************************************************/

    @FunctionalInterface
    public interface CheckedSupplier<T> {
        T get() throws Exception;
    }

    private BiFunction<CheckedSupplier<Method>, Object, Object> methodConsumer = (methodSupplier, object) -> {
        try {
            Method method = methodSupplier.get();
            method.setAccessible(true);
            return method.invoke(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    };

    private BiFunction<CheckedSupplier<Field>, Object, Object> fieldConsumer = (fieldSupplier, object) -> {
        try {
            Field field = fieldSupplier.get();
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    };

    private BiConsumer<String, EventType<?>> parentArrowEventHandlerTerminator = (handlerName, eventType) ->{
        try {
            Field field = ComboBoxBaseSkin.class.getDeclaredField(handlerName);
            field.setAccessible(true);
            arrowButton.removeEventHandler(eventType, (EventHandler) field.get(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    private void removeParentFakeFocusListener(ComboBoxBase<T> comboBoxBase) {
        // handle FakeFocusField cast exception
        try {
            final ReadOnlyBooleanProperty focusedProperty = comboBoxBase.focusedProperty();
            Field helper = focusedProperty.getClass().getSuperclass().getDeclaredField("helper");
            helper.setAccessible(true);
            ExpressionHelper value = (ExpressionHelper) helper.get(focusedProperty);
            Field changeListenersField = value.getClass().getDeclaredField("changeListeners");
            changeListenersField.setAccessible(true);
            ChangeListener[] changeListeners = (ChangeListener[]) changeListenersField.get(value);
            // remove parent focus listener to prevent editor class cast exception
            for(int i = changeListeners.length - 1; i > 0; i--) {
                if (changeListeners[i] != null && changeListeners[i].getClass().getName().contains("ComboBoxPopupControl")) {
                    focusedProperty.removeListener(changeListeners[i]);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeParentPopupHandlers() {
        try {
            Method getPopupMethod = methodSupplier.apply("getPopup");
            getPopupMethod.setAccessible(true);
            PopupControl popup = (PopupControl) getPopupMethod.invoke(this);
            popup.setOnAutoHide(event -> behavior.onAutoHide(popup));

            WindowEventDispatcher dispatcher = (WindowEventDispatcher)
                methodConsumer.apply(()->Window.class.getDeclaredMethod("getInternalEventDispatcher"), popup);
            Map compositeEventHandlersMap = (Map)
                fieldConsumer.apply(()->EventHandlerManager.class.getDeclaredField("eventHandlerMap"), dispatcher.getEventHandlerManager());
            compositeEventHandlersMap.remove(MouseEvent.MOUSE_CLICKED);

//            CompositeEventHandler compositeEventHandler = (CompositeEventHandler) compositeEventHandlersMap.get(MouseEvent.MOUSE_CLICKED);
//            Object obj = fieldConsumer.apply(()->CompositeEventHandler.class.getDeclaredField("firstRecord"),compositeEventHandler);
//            EventHandler handler = (EventHandler) fieldConsumer.apply(() -> obj.getClass().getDeclaredField("eventHandler"), obj);
//            popup.removeEventHandler(MouseEvent.MOUSE_CLICKED, handler);
            popup.addEventHandler(MouseEvent.MOUSE_CLICKED, click-> behavior.onAutoHide(popup));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateArrowButtonListeners() {
        if (getSkinnable().isEditable()) {
            arrowButton.addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEnteredEventHandler);
            arrowButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressedEventHandler);
            arrowButton.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedEventHandler);
            arrowButton.addEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedEventHandler);
        } else {
            arrowButton.removeEventHandler(MouseEvent.MOUSE_ENTERED, mouseEnteredEventHandler);
            arrowButton.removeEventHandler(MouseEvent.MOUSE_PRESSED, mousePressedEventHandler);
            arrowButton.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedEventHandler);
            arrowButton.removeEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedEventHandler);
        }
    }


    /***************************************************************************
     *                                                                         *
     * Reflections internal API for ComboBoxPopupControl                       *
     *                                                                         *
     **************************************************************************/

    private HashMap<String, Method> parentCachedMethods = new HashMap<>();

    Function<String, Method> methodSupplier = name ->{
        if(!parentCachedMethods.containsKey(name)){
            try {
                Method method = ComboBoxPopupControl.class.getDeclaredMethod(name);
                method.setAccessible(true);
                parentCachedMethods.put(name, method);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return parentCachedMethods.get(name);
    };

    Consumer<Method> methodInvoker = method -> {
        try {
            method.invoke(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    Function<Method, Object> methodReturnInvoker = method -> {
        try {
            return method.invoke(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    };

    protected void reflectUpdateDisplayArea() {
        methodInvoker.accept(methodSupplier.apply("updateDisplayArea"));
    }

    protected void reflectSetTextFromTextFieldIntoComboBoxValue() {
        methodInvoker.accept(methodSupplier.apply("setTextFromTextFieldIntoComboBoxValue"));
    }

    protected TextField reflectGetEditableInputNode(){
        return (TextField) methodReturnInvoker.apply(methodSupplier.apply("getEditableInputNode"));
    }

    protected void reflectUpdateDisplayNode() {
        methodInvoker.accept(methodSupplier.apply("updateDisplayNode"));
    }
}
