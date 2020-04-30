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

package com.jfoenix.controls.pannable;

import com.jfoenix.controls.pannable.base.IPannablePane;
import com.jfoenix.controls.pannable.gestures.PanningGestures;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

/**
 * Simple pannable pane implementation
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2020-04-30
 */
public class PannablePane extends Pane implements IPannablePane {

    private Scale scale = new Scale(1, 1, 0, 0);

    public PannablePane() {
        getTransforms().add(scale);
        scale.yProperty().bind(scale.xProperty());
    }

    @Override
    public double getScale() {
        return scale.getX();
    }

    @Override
    public void setScale(double scale) {
        this.scale.setX(scale);
    }

    @Override
    public DoubleProperty scaleProperty() {
        return scale.xProperty();
    }

    public static PannablePane wrap(Node... children) {
        PannablePane canvas = new PannablePane();
        PanningGestures.attachViewPortGestures(canvas);
        canvas.getChildren().setAll(children);
        return canvas;
    }
}

