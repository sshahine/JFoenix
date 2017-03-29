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

import com.jfoenix.controls.JFXTimePicker;
import com.jfoenix.skins.JFXTimePickerSkin;
import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.behavior.KeyBinding;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2017-03-01
 */
public class JFXTimePickerBehavior extends ComboBoxBaseBehavior<LocalTime> {

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public JFXTimePickerBehavior(final JFXTimePicker timePicker) {
        super(timePicker, JFX_TIME_PICKER_BINDINGS);
    }

    /***************************************************************************
     *                                                                         *
     * Key event handling                                                      *
     *                                                                         *
     **************************************************************************/

    protected static final List<KeyBinding> JFX_TIME_PICKER_BINDINGS = new ArrayList<>();

    static {
        JFX_TIME_PICKER_BINDINGS.addAll(COMBO_BOX_BASE_BINDINGS);
    }

    /**************************************************************************
     *                                                                        *
     * Mouse Events handling (when losing focus)                              *
     *                                                                        *
     *************************************************************************/

    @Override
    public void onAutoHide() {
        JFXTimePicker datePicker = (JFXTimePicker) getControl();
        JFXTimePickerSkin cpSkin = (JFXTimePickerSkin) datePicker.getSkin();
        cpSkin.syncWithAutoUpdate();
        if (!datePicker.isShowing()) {
            super.onAutoHide();
        }
    }

}
