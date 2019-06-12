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

import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
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
     * This {@link PseudoClass} will be activated when a validation error occurs.
     * <p>
     * Some components have default styling for this pseudo class. See {@code jfx-text-field.css}
     * and {@code jfx-combo-box.css} for examples.
     */
    public static final PseudoClass PSEUDO_CLASS_ERROR = PseudoClass.getPseudoClass("error");

    /**
     * When using {@code Tooltip.install(node, tooltip)}, the given tooltip is stored in the Node's properties
     * under this key.
     *
     * @see Tooltip#install(Node, Tooltip)
     */
    private static final String TOOLTIP_PROP_KEY = "javafx.scene.control.Tooltip";

    /**
     * Used to determine if a given tooltip is our {@link #errorTooltip} or not.
     */
    public static final String ERROR_TOOLTIP_STYLE_CLASS = "error-tooltip";

    /**
     * If the {@link #srcControl} has a tooltip, it's saved here so that we can replace it with a tooltip
     * containing the validator's error message ({@link #errorTooltip}).
     */
    private Tooltip savedTooltip = null;

    /**
     * If the validator {@link #hasErrors}, then we'll show the validator's {@link #message} in this tooltip on the
     * {@link #srcControl}.
     */
    private Tooltip errorTooltip = null;

    /**
     * @see #ValidatorBase()
     * @param message will be set as the validator's {@link #message}.
     */
    public ValidatorBase(String message) {
        this();
        this.setMessage(message);
    }

    /**
     * When creating a new validator you need to define the validation condition by implementing {@link #eval()}.
     * <p>
     * For examples of how you might implement it, see {@link RequiredFieldValidator} and
     * {@link RegexValidator}.
     */
    public ValidatorBase() {
        parentProperty().addListener((o, oldVal, newVal) -> parentChanged());
        errorTooltip = new Tooltip();
        errorTooltip.getStyleClass().add(ERROR_TOOLTIP_STYLE_CLASS);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////

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
     * Will validate the source control.
     * <p>
     * Calls {@link #eval()} and then {@link #onEval()}.
     */
    public void validate() {
        eval();
        onEval();
    }

    /**
     * Should evaluate the validation condition and set {@link #hasErrors} to true or false. It should
     * be true when the value is invalid (it has errors) and false when the value is valid (no errors).
     * <p>
     * This method is fired once {@link #validate()} is called.
     */
    protected abstract void eval();

    /**
     * This method will update the source control after evaluating the validation condition (see {@link #eval()}).
     * <p>
     * If the validator isn't "passing" the {@link #PSEUDO_CLASS_ERROR :error} pseudoclass is applied to the
     * {@link #srcControl}.
     */
    protected void onEval() {
        Node control = getSrcControl();
        if (hasErrors.get()) {
            control.pseudoClassStateChanged(PSEUDO_CLASS_ERROR, true);

            if (control instanceof Control) {
                Tooltip controlTooltip = ((Control) control).getTooltip();
                if (controlTooltip != null && !controlTooltip.getStyleClass().contains(ERROR_TOOLTIP_STYLE_CLASS)) {
                    savedTooltip = ((Control) control).getTooltip();
                }
                errorTooltip.setText(getMessage());
                ((Control) control).setTooltip(errorTooltip);
            }
        } else {
            if (control instanceof Control) {
                Tooltip controlTooltip = ((Control) control).getTooltip();
                if ((controlTooltip != null && controlTooltip.getStyleClass().contains(ERROR_TOOLTIP_STYLE_CLASS))
                    || (controlTooltip == null && savedTooltip != null)) {
                    ((Control) control).setTooltip(savedTooltip);
                }
                savedTooltip = null;
            }
            control.pseudoClassStateChanged(PSEUDO_CLASS_ERROR, false);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties
    ///////////////////////////////////////////////////////////////////////////

    /**
     * The {@link Control}/{@link Node} that the validator is checking the value of.
     * <p>
     * Supports {@link Node}s because not all things that need validating are {@link Control}s.
     */
    protected SimpleObjectProperty<Node> srcControl = new SimpleObjectProperty<>();

    /**
     * @see #srcControl
     */
    public void setSrcControl(Node srcControl) {
        this.srcControl.set(srcControl);
    }

    /**
     * @see #srcControl
     */
    public Node getSrcControl() {
        return this.srcControl.get();
    }

    /**
     * @see #srcControl
     */
    public ObjectProperty<Node> srcControlProperty() {
        return this.srcControl;
    }


    /***** src *****/
    // TODO: 6/11/2019 Describe what this is for and when someone should set/override it.
    protected SimpleStringProperty src = new SimpleStringProperty() {
        @Override
        protected void invalidated() {
            updateSrcControl();
        }
    };

    /**
     * @see #src
     */
    public void setSrc(String src) {
        this.src.set(src);
    }

    /**
     * @see #src
     */
    public String getSrc() {
        return this.src.get();
    }

    /**
     * @see #src
     */
    public StringProperty srcProperty() {
        return this.src;
    }


    /**
     * Tells whether the validator is "passing" or not.
     * <p>
     * In a validator's implementation of {@link #eval()}, if the value the validator is checking is invalid, it should
     * set this to <em>true</em>. If the value is <em>valid</em>, it should set this to <em>false</em>.
     * <p>
     * When <em>hasErrors</em> is true, the validator will automatically apply the {@link #PSEUDO_CLASS_ERROR :error}
     * pseudoclass to the {@link #srcControl}; the {@link #srcControl} will also have a {@link Tooltip} containing the
     * {@link #message} applied to it (see {@link #errorTooltip}).
     */
    protected ReadOnlyBooleanWrapper hasErrors = new ReadOnlyBooleanWrapper(false);

    /**
     * @see #hasErrors
     */
    public boolean getHasErrors() {
        return hasErrors.get();
    }

    /**
     * @see #hasErrors
     */
    public ReadOnlyBooleanProperty hasErrorsProperty() {
        return hasErrors.getReadOnlyProperty();
    }


    /**
     * The error message to display when the validator is <em>not</em> "passing."
     * <p>
     * When {@link #hasErrors} is true, this message is displayed near the {@link #srcControl} (usually below);
     * it's also displayed in a {@link Tooltip} applied to the {@link #srcControl} (see {@link #errorTooltip}).
     */
    protected SimpleStringProperty message = new SimpleStringProperty() {
        @Override
        protected void invalidated() {
            updateSrcControl();
        }
    };

    /**
     * @see #message
     */
    public void setMessage(String msg) {
        this.message.set(msg);
    }

    /**
     * @see #message
     */
    public String getMessage() {
        return this.message.get();
    }

    /**
     * @see #message
     */
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
