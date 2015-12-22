/*
 * JFoenix
 * Copyright (c) 2015, JFoenix and/or its affiliates., All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package com.jfoenix.controls.behavior;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.jfoenix.skins.JFXDatePickerSkin;
import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.behavior.KeyBinding;

import javafx.scene.control.DatePicker;

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

    protected static final List<KeyBinding> JFX_DATE_PICKER_BINDINGS = new ArrayList<KeyBinding>();
    static {
        JFX_DATE_PICKER_BINDINGS.addAll(COMBO_BOX_BASE_BINDINGS);
    }
    
	/**************************************************************************
     *                                                                        *
     * Mouse Events handling (when losing focus)                              *
     *                                                                        *
     *************************************************************************/

	@Override public void onAutoHide() {
        DatePicker datePicker = (DatePicker)getControl();
        JFXDatePickerSkin cpSkin = (JFXDatePickerSkin)datePicker.getSkin();
        cpSkin.syncWithAutoUpdate();
        if (!datePicker.isShowing()) super.onAutoHide();
    }

}
