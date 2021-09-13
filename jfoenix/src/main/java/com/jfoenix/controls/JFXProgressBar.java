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
import com.jfoenix.skins.JFXProgressBarSkin;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.ProgressBar;
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
        return JFoenixResources.load("css/controls/jfx-progress-bar.css").toExternalForm();
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
