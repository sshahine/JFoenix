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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package demos.gui.uicomponents;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import javax.annotation.PostConstruct;

import com.jfoenix.controls.JFXProgressBar;

@FXMLController(value = "/resources/fxml/ui/ProgressBar.fxml", title = "Material Design Example")
public class ProgressBarController {

	@FXML
	private JFXProgressBar progress1;
	@FXML
	private JFXProgressBar progress2;

	@FXMLViewFlowContext
	private ViewFlowContext context;

	@PostConstruct
	public void init() throws FlowException, VetoException {
		if(((Pane) context.getRegisteredObject("ContentPane")).getChildren().size() > 0)
			Platform.runLater(()-> ((Pane)((Pane) context.getRegisteredObject("ContentPane")).getChildren().get(0)).getChildren().remove(1));

		Timeline task = new Timeline(
							new KeyFrame(
									Duration.ZERO,
									new KeyValue(progress1.progressProperty(), 0),
									new KeyValue(progress2.progressProperty(), 0)),
							new KeyFrame(
									Duration.seconds(2),
									new KeyValue(progress1.progressProperty(), 1),
									new KeyValue(progress2.progressProperty(), 1)));
		task.setCycleCount(Timeline.INDEFINITE);
		task.play();
	}

}
