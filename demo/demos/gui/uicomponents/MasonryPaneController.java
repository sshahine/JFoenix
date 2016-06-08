package demos.gui.uicomponents;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import com.jfoenix.controls.JFXMasonryPane;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.util.VetoException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

@FXMLController(value = "/resources/fxml/ui/Masonry.fxml" , title = "Material Design Example")
public class MasonryPaneController {

	@FXML ScrollPane scrollPane; 
	@FXML JFXMasonryPane masonryPane;
	
	@PostConstruct
	public void init() throws FlowException, VetoException {
		ArrayList<Label> labels = new ArrayList<>();
		for(int i = 0 ; i < 40 ; i++){
			Label label = new Label("Label " + i);
			double width = Math.random()*200;
			label.setMinWidth(width);
			label.setMaxWidth(width);
			label.setPrefWidth(width);
			double height = Math.random()*200;
			label.setMinHeight(height);
			label.setMaxHeight(height);
			label.setPrefHeight(height);
			label.setStyle("-fx-background-color: " + getDefaultColor(i%12));
			label.setAlignment(Pos.CENTER);
			label.setText("Label " + i);
			label.setWrapText(true);
			labels.add(label);
		}
		masonryPane.getChildren().addAll(labels);
		Platform.runLater(()->scrollPane.requestLayout());
	}
	
	private String getDefaultColor(int i) {
		String color = "#FFFFFF";
		switch (i) {
		case 0:
			color = "#8F3F7E";
			break;
		case 1:
			color = "#B5305F";
			break;
		case 2:
			color = "#CE584A";
			break;
		case 3:
			color = "#DB8D5C";
			break;
		case 4:
			color = "#DA854E";
			break;
		case 5:
			color = "#E9AB44";
			break;
		case 6:
			color = "#FEE435";
			break;
		case 7:
			color = "#99C286";
			break;
		case 8:
			color = "#01A05E";
			break;
		case 9:
			color = "#4A8895";
			break;
		case 10:
			color = "#16669B";
			break;
		case 11:
			color = "#2F65A5";
			break;
		case 12:
			color = "#4E6A9C";
			break;
		default:
			break;
		}
		return color;
	}

	
}
