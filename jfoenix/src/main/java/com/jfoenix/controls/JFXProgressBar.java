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

import com.jfoenix.skins.JFXProgressBarSkin;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Skin;

/**
 * JFXProgressBar is the material design implementation of a progress bar.
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXProgressBar extends ProgressBar {
    /**
     * Initialize the style class to 'jfx-progress-bar'.
     * <p>
     * This is the selector class from which CSS can be used to style
     * this control.
     */
    private static final String DEFAULT_STYLE_CLASS = "jfx-progress-bar";

    /**
     * {@inheritDoc}
     */
    public JFXProgressBar() {
        initialize();
    }

    /**
     * {@inheritDoc}
     */
    public JFXProgressBar(double progress) {
        super(progress);
        initialize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return getClass().getResource("/css/controls/jfx-progress-bar.css").toExternalForm();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXProgressBarSkin(this);
    }

    private void initialize() {
        setPrefWidth(200);
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }


    private DoubleProperty secondaryProgress = new SimpleDoubleProperty(INDETERMINATE_PROGRESS);

    public double getSecondaryProgress() {
        return secondaryProgress == null ? INDETERMINATE_PROGRESS : secondaryProgress.get();
    }

    public DoubleProperty secondaryProgressProperty() {
        return secondaryProgress;
    }

    public void setSecondaryProgress(double secondaryProgress) {
        secondaryProgressProperty().set(secondaryProgress);
    }
}
