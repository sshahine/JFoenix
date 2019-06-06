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

package com.jfoenix.validation.base;

import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;

import java.util.function.Supplier;

/**
 * An abstract class that defines the basic validation functionality for a certain control.
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public abstract class ValidatorBase extends Parent {

    /**
     * this style class will be activated when a validation error occurs
     */
    public static final PseudoClass PSEUDO_CLASS_ERROR = PseudoClass.getPseudoClass("error");
    
    /**
     * When using {@code Tooltip.install(node, tooltip)}, the given tooltip is stored in the Node's properties
     * under this key.
     * @see Tooltip#install(Node, Tooltip) 
     */
    private static final String TOOLTIP_PROP_KEY = "javafx.scene.control.Tooltip";

    /**
     * If the {@link #srcControl} has a tooltip, it's saved here so that we can replace it with a tooltip
     * containing the validator's error message.
     */
    private Tooltip savedTooltip = null;
    private Tooltip errorTooltip = null;

    public ValidatorBase(String message) {
        this();
        this.setMessage(message);
    }

    public ValidatorBase() {
        parentProperty().addListener((o, oldVal, newVal) -> parentChanged());
        errorTooltip = new Tooltip();
        errorTooltip.getStyleClass().add("error-tooltip");
    }

    /***************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/

    private void parentChanged() {
        updateSrcControl();
    }

    private void updateSrcControl() {
        Parent parent = getParent();
        if (parent != null) {
            Node control = parent.lookup(getSrc());
            srcControl.set(control);
        }
    }

    /**
     * will validate the source control
     */
    public void validate() {
        eval();
        onEval();
    }

    /**
     * will evaluate the validation condition once calling validate method
     */
    protected abstract void eval();

    /**
     * this method will update the source control after evaluating the validation condition
     */
    protected void onEval() {
        Node control = getSrcControl();
        if (hasErrors.get()) {
            /*
             * TODO
             * NOTE: NEED TO FIX adding error style class to text area
             * is causing the caret to disappear
             */
            if (!control.getStyleClass().contains(PSEUDO_CLASS_ERROR.getPseudoClassName())) {
                control.getStyleClass().add(PSEUDO_CLASS_ERROR.getPseudoClassName());
            }

            control.pseudoClassStateChanged(PSEUDO_CLASS_ERROR, true);

            Tooltip errorTooltip = new Tooltip();
            errorTooltip.getStyleClass().add("error-tooltip");
            errorTooltip.setText(getMessage());
            
            if (control instanceof Control) {
                Tooltip controlTooltip = ((Control) control).getTooltip();
                if (controlTooltip != null && !controlTooltip.getStyleClass().contains("error-tooltip")) {
                    savedTooltip = controlTooltip;
                }

                errorTooltip.setText(getMessage());

                ((Control) control).setTooltip(errorTooltip);
            } else {
                Tooltip installedTooltip = (Tooltip) control.getProperties().get(TOOLTIP_PROP_KEY);
                if (installedTooltip != null && !installedTooltip.getStyleClass().contains("error-tooltip")) {
                    savedTooltip = installedTooltip;
                }
                
                Tooltip.install(control, errorTooltip);
            }
        } else {
            if (control instanceof Control) {
                Tooltip controlTooltip = ((Control) control).getTooltip();
                if ((controlTooltip != null && controlTooltip.getStyleClass().contains("error-tooltip"))
                        || (controlTooltip == null && savedTooltip != null)) {
                    ((Control) control).setTooltip(savedTooltip);
                }
            } else {
                Tooltip installedTooltip = (Tooltip) control.getProperties().get(TOOLTIP_PROP_KEY);
                if ((installedTooltip != null && installedTooltip.getStyleClass().contains("error-tooltip"))
                        || (installedTooltip == null && savedTooltip != null)) {
                    Tooltip.install(control, savedTooltip);
                }
            }
            savedTooltip = null;
            control.pseudoClassStateChanged(PSEUDO_CLASS_ERROR, false);
        }
    }

    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    /***** srcControl *****/
    protected SimpleObjectProperty<Node> srcControl = new SimpleObjectProperty<>();

    public void setSrcControl(Node srcControl) {
        this.srcControl.set(srcControl);
    }

    public Node getSrcControl() {
        return this.srcControl.get();
    }

    public ObjectProperty<Node> srcControlProperty() {
        return this.srcControl;
    }


    /***** src *****/
    protected SimpleStringProperty src = new SimpleStringProperty() {
        @Override
        protected void invalidated() {
            updateSrcControl();
        }
    };

    public void setSrc(String src) {
        this.src.set(src);
    }

    public String getSrc() {
        return this.src.get();
    }

    public StringProperty srcProperty() {
        return this.src;
    }


    /***** hasErrors *****/
    protected ReadOnlyBooleanWrapper hasErrors = new ReadOnlyBooleanWrapper(false);

    public boolean getHasErrors() {
        return hasErrors.get();
    }

    public ReadOnlyBooleanProperty hasErrorsProperty() {
        return hasErrors.getReadOnlyProperty();
    }

    /***** Message *****/
    protected SimpleStringProperty message = new SimpleStringProperty() {
        @Override
        protected void invalidated() {
            updateSrcControl();
        }
    };

    public void setMessage(String msg) {
        this.message.set(msg);
    }

    public String getMessage() {
        return this.message.get();
    }

    public StringProperty messageProperty() {
        return this.message;
    }

    /***** Icon *****/
    protected SimpleObjectProperty<Supplier<Node>> iconSupplier = new SimpleObjectProperty<Supplier<Node>>() {
        @Override
        protected void invalidated() {
            updateSrcControl();
        }
    };

    public void setIconSupplier(Supplier<Node> icon) {
        this.iconSupplier.set(icon);
    }

    public SimpleObjectProperty<Supplier<Node>> iconSupplierProperty() {
        return this.iconSupplier;
    }

    public Supplier<Node> getIconSupplier() {
        return iconSupplier.get();
    }

    public void setIcon(Node icon) {
        iconSupplier.set(() -> icon);
    }

    public Node getIcon() {
        if (iconSupplier.get() == null) {
            return null;
        }
        Node icon = iconSupplier.get().get();
        if (icon != null) {
            icon.getStyleClass().add("error-icon");
        }
        return icon;
    }
}
