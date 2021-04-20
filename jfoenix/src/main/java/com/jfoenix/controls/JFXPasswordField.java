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

import com.jfoenix.assets.JFoenixResources;
import com.jfoenix.controls.base.IFXLabelFloatControl;
import com.jfoenix.skins.JFXPasswordFieldSkin;
import com.jfoenix.validation.base.ValidatorBase;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Skin;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * JFXPasswordField is the material design implementation of a password Field.
 *
 * @author Shadi Shaheen
 * @version 2.0
 * @since 2016-03-09
 */
public class JFXPasswordField extends JFXTextField implements IFXLabelFloatControl {

    /**
     * {@inheritDoc}
     */
    public JFXPasswordField() {
        getStyleClass().add("password-field");
        setAccessibleRole(AccessibleRole.PASSWORD_FIELD);
        initialize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXPasswordFieldSkin(this);
    }

    private void initialize() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
        if ("dalvik".equals(System.getProperty("java.vm.name").toLowerCase())) {
            this.setStyle("-fx-skin: \"com.jfoenix.android.skins.JFXTextFieldSkinAndroid\";");
        }
    }

    /***************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/

    @Override
    public void cut() {
        if (isMaskedText()) {
            // No-op
            return;
        }
        copy();
        IndexRange selection = getSelection();
        deleteText(selection.getStart(), selection.getEnd());
    }

    /**
     * Transfers the currently selected range in the text to the clipboard,
     * leaving the current selection.
     */
    @Override
    public void copy() {
        if (isMaskedText()) {
            // No-op
            return;
        }
        final String selectedText = getSelectedText();
        if (selectedText.length() > 0) {
            final ClipboardContent content = new ClipboardContent();
            content.putString(selectedText);
            Clipboard.getSystemClipboard().setContent(content);
        }
    }

    /***************************************************************************
     *                                                                         *
     * Accessibility handling                                                  *
     *                                                                         *
     **************************************************************************/

    @Override
    public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
        switch (attribute) {
            case TEXT:
                return null;
            default:
                return super.queryAccessibleAttribute(attribute, parameters);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return USER_AGENT_STYLESHEET;
    }

    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * wrapper for validation properties / methods
     */
    private ValidationControl validationControl = new ValidationControl(this);

    @Override
    public ValidatorBase getActiveValidator() {
        return validationControl.getActiveValidator();
    }

    @Override
    public ReadOnlyObjectProperty<ValidatorBase> activeValidatorProperty() {
        return validationControl.activeValidatorProperty();
    }

    @Override
    public ObservableList<ValidatorBase> getValidators() {
        return validationControl.getValidators();
    }

    @Override
    public void setValidators(ValidatorBase... validators) {
        validationControl.setValidators(validators);
    }

    @Override
    public boolean validate() {
        return validationControl.validate();
    }

    @Override
    public void resetValidation() {
        validationControl.resetValidation();
    }

    private BooleanProperty maskedText = new SimpleBooleanProperty(true);

    public boolean isMaskedText() {
        return maskedText.get();
    }

    public BooleanProperty maskedTextProperty() {
        return maskedText;
    }

    public void setMaskedText(boolean maskedText) {
        this.maskedText.set(maskedText);
    }

    /***************************************************************************
     *                                                                         *
     * Styleable Properties                                                    *
     *                                                                         *
     **************************************************************************/

    /**
     * Initialize the style class to 'jfx-password-field'.
     * <p>
     * This is the selector class from which CSS can be used to style
     * this control.
     */
    private static final String DEFAULT_STYLE_CLASS = "jfx-password-field";
    private static final String USER_AGENT_STYLESHEET = JFoenixResources.load("css/controls/jfx-password-field.css").toExternalForm();
}
