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

package com.jfoenix.controls.behavior;

import com.jfoenix.skins.JFXDatePickerSkin;
import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXDatePickerBehavior extends ComboBoxBaseBehavior<LocalDate> {

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public JFXDatePickerBehavior(final DatePicker datePicker) {
        super(datePicker, JFX_DATE_PICKER_BINDINGS);
    }

    /***************************************************************************
     *                                                                         *
     * Key event handling                                                      *
     *                                                                         *
     **************************************************************************/

    protected static final List<KeyBinding> JFX_DATE_PICKER_BINDINGS = new ArrayList<>();

    static {
        JFX_DATE_PICKER_BINDINGS.addAll(COMBO_BOX_BASE_BINDINGS);
    }

    /**************************************************************************
     *                                                                        *
     * Mouse Events handling (when losing focus)                              *
     *                                                                        *
     *************************************************************************/

    @Override
    public void onAutoHide() {
        DatePicker datePicker = (DatePicker) getControl();
        JFXDatePickerSkin cpSkin = (JFXDatePickerSkin) datePicker.getSkin();
        cpSkin.syncWithAutoUpdate();
        if (!datePicker.isShowing()) {
            super.onAutoHide();
        }
    }

}
