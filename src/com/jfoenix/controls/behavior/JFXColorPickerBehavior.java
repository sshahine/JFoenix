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
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.ESCAPE;
import static javafx.scene.input.KeyCode.SPACE;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;

import java.util.ArrayList;
import java.util.List;

import com.jfoenix.skins.JFXColorPickerSkin;
import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.behavior.KeyBinding;

import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

public class JFXColorPickerBehavior extends ComboBoxBaseBehavior<Color> {

	
    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/
    
    public JFXColorPickerBehavior(final ColorPicker colorPicker) {
        super(colorPicker, JFX_COLOR_PICKER_BINDINGS);
    }

    /***************************************************************************
     *                                                                         *
     * Key event handling                                                      *
     *                                                                         *
     **************************************************************************/
    protected static final String JFX_OPEN_ACTION = "Open";
    protected static final String JFX_CLOSE_ACTION = "Close";
    protected static final List<KeyBinding> JFX_COLOR_PICKER_BINDINGS = new ArrayList<KeyBinding>();
    static {
        JFX_COLOR_PICKER_BINDINGS.add(new KeyBinding(ESCAPE, KEY_PRESSED, JFX_CLOSE_ACTION));
        JFX_COLOR_PICKER_BINDINGS.add(new KeyBinding(SPACE, KEY_PRESSED, JFX_OPEN_ACTION));
        JFX_COLOR_PICKER_BINDINGS.add(new KeyBinding(ENTER, KEY_PRESSED, JFX_OPEN_ACTION));
    }

    @Override protected void callAction(String name) {
        if (JFX_OPEN_ACTION.equals(name)) show();
        else if(JFX_CLOSE_ACTION.equals(name)) hide();
        else super.callAction(name);
    }

	/**************************************************************************
     *                                                                        *
     * Mouse Events handling (when losing focus)                              *
     *                                                                        *
     *************************************************************************/

    @Override public void onAutoHide() {
        ColorPicker colorPicker = (ColorPicker)getControl();
        JFXColorPickerSkin cpSkin = (JFXColorPickerSkin)colorPicker.getSkin();
        cpSkin.syncWithAutoUpdate();
        if (!colorPicker.isShowing()) super.onAutoHide();
    }    

}
