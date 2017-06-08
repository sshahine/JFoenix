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

package com.jfoenix.controls;

import javafx.scene.AccessibleAttribute;

/**
 * JFXTextField used in pickers {@link JFXDatePicker}, {@link JFXTimePicker}
 *
 * Created by sshahine on 6/8/2017.
 */
final class FakeFocusJFXTextField extends JFXTextField {
    @Override public void requestFocus() {
        if (getParent() != null) {
            getParent().requestFocus();
        }
    }
    public void setFakeFocus(boolean b) {
        setFocused(b);
    }
    @Override
    public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
        switch (attribute) {
            case FOCUS_ITEM:
                // keep focus on parent control
                return getParent();
            default: return super.queryAccessibleAttribute(attribute, parameters);
        }
    }
}
