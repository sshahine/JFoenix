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

import com.jfoenix.controls.base.IFXValidatableControl;
import com.jfoenix.validation.base.ValidatorBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;

/**
 * this class used as validation model wrapper for all {@link IFXValidatableControl}
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2018-07-19
 */
class ValidationControl implements IFXValidatableControl {

    private ReadOnlyObjectWrapper<ValidatorBase> activeValidator = new ReadOnlyObjectWrapper<>();

    private Control control;

    public ValidationControl(Control control) {
        this.control = control;
    }

    @Override
    public ValidatorBase getActiveValidator() {
        return activeValidator == null ? null : activeValidator.get();
    }

    @Override
    public ReadOnlyObjectProperty<ValidatorBase> activeValidatorProperty() {
        return this.activeValidator.getReadOnlyProperty();
    }

    ReadOnlyObjectWrapper<ValidatorBase> activeValidatorWritableProperty() {
        return this.activeValidator;
    }

    /**
     * list of validators that will validate the text value upon calling
     * {{@link #validate()}
     */
    private ObservableList<ValidatorBase> validators = FXCollections.observableArrayList();

    @Override
    public ObservableList<ValidatorBase> getValidators() {
        return validators;
    }

    @Override
    public void setValidators(ValidatorBase... validators) {
        this.validators.addAll(validators);
    }

    /**
     * validates the text value using the list of validators provided by the user
     * {{@link #setValidators(ValidatorBase...)}
     *
     * @return true if the value is valid else false
     */
    @Override
    public boolean validate() {
        for (ValidatorBase validator : validators) {
            // source control must be set to allow validators re-usability
            validator.setSrcControl(control);
            validator.validate();
            if (validator.getHasErrors()) {
                activeValidator.set(validator);
                return false;
            }
        }
        activeValidator.set(null);
        return true;
    }

    public void resetValidation() {
        control.pseudoClassStateChanged(ValidatorBase.PSEUDO_CLASS_ERROR, false);
        activeValidator.set(null);
    }
}
