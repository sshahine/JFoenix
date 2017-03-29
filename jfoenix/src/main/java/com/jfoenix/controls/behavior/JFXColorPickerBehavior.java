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

package com.jfoenix.controls.behavior;

import com.jfoenix.skins.JFXColorPickerSkin;
import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;

/**
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
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
    protected static final List<KeyBinding> JFX_COLOR_PICKER_BINDINGS = new ArrayList<>();

    static {
        JFX_COLOR_PICKER_BINDINGS.add(new KeyBinding(ESCAPE, KEY_PRESSED, JFX_CLOSE_ACTION));
        JFX_COLOR_PICKER_BINDINGS.add(new KeyBinding(SPACE, KEY_PRESSED, JFX_OPEN_ACTION));
        JFX_COLOR_PICKER_BINDINGS.add(new KeyBinding(ENTER, KEY_PRESSED, JFX_OPEN_ACTION));
    }

    @Override
    protected void callAction(String name) {
        if (JFX_OPEN_ACTION.equals(name)) {
            show();
        } else if (JFX_CLOSE_ACTION.equals(name)) {
            hide();
        } else {
            super.callAction(name);
        }
    }

    /**************************************************************************
     *                                                                        *
     * Mouse Events handling (when losing focus)                              *
     *                                                                        *
     *************************************************************************/

    @Override
    public void onAutoHide() {
        ColorPicker colorPicker = (ColorPicker) getControl();
        JFXColorPickerSkin cpSkin = (JFXColorPickerSkin) colorPicker.getSkin();
        cpSkin.syncWithAutoUpdate();
        if (!colorPicker.isShowing()) {
            super.onAutoHide();
        }
    }

}
