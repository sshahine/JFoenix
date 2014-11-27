package com.cctintl.c3dfx.demos.gui.uicomponents;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.util.Duration;

import javax.annotation.PostConstruct;

import com.cctintl.c3dfx.controls.C3DProgressBar;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.util.VetoException;

@FXMLController(value = "/resources/fxml/ui/ProgressBar.fxml" , title = "Material Design Example")
public class ProgressBarController {

	@FXML private C3DProgressBar progress1;
	@FXML private C3DProgressBar progress2;
	
	@PostConstruct
	public void init() throws FlowException, VetoException {
		
		Timeline task = new Timeline(
				new KeyFrame(
						Duration.ZERO,       
						new KeyValue(progress1.progressProperty(), 0),
						new KeyValue(progress2.progressProperty(), 0)
						),
						new KeyFrame(
								Duration.seconds(2), 
								new KeyValue(progress1.progressProperty(), 1),
								new KeyValue(progress2.progressProperty(), 1)
								)
				);
		task.setCycleCount(5);
		task.playFromStart();
	}
	
	
	
}
