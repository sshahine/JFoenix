package demos.gui.uicomponents;

import javax.annotation.PostConstruct;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXScrollPane;
import com.jfoenix.svg.SVGGlyph;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.util.VetoException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

@FXMLController(value = "/resources/fxml/ui/ScrollPane.fxml" , title = "Material Design Example")
public class ScrollPaneController {
	@FXML private JFXListView<String> contentList;
	@FXML private JFXButton backButton;
	@FXML private JFXScrollPane scroll;
	
	@PostConstruct
	public void init() throws FlowException, VetoException {
		
		for(int i = 0 ; i < 100 ; i++) 
			contentList.getItems().add("Item " + i);
		contentList.setMaxHeight(3400);
		
		smoothScrolling((ScrollPane) scroll.getChildren().get(0));
		
		SVGGlyph arrow = new SVGGlyph(0, "FULLSCREEN", "M402.746 877.254l-320-320c-24.994-24.992-24.994-65.516 0-90.51l320-320c24.994-24.992 65.516-24.992 90.51 0 24.994 24.994 24.994 65.516 0 90.51l-210.746 210.746h613.49c35.346 0 64 28.654 64 64s-28.654 64-64 64h-613.49l210.746 210.746c12.496 12.496 18.744 28.876 18.744 45.254s-6.248 32.758-18.744 45.254c-24.994 24.994-65.516 24.994-90.51 0z", Color.WHITE);		
		arrow.setSize(20, 16);
		backButton.setGraphic(arrow);
		backButton.setRipplerFill(Color.WHITE);
	}
	
	
	// smoothing the scroll (WILL BE REMOVED LATER)
	private double currentScroll = -1;
	
	private void smoothScrolling(ScrollPane scrollPane) {
		Timeline t = new Timeline();
		scrollPane.addEventFilter(ScrollEvent.ANY, event->{
			if(event.getEventType().equals(ScrollEvent.SCROLL)){
				if(currentScroll == -1) currentScroll = scrollPane.getVvalue();
				currentScroll = currentScroll + 0.015 * (event.getDeltaY() > 0 ? -1 : 1);
				currentScroll = currentScroll < 0 ? 0 : currentScroll > 1 ? 1 : currentScroll;
				if(t.getStatus().equals(Animation.Status.STOPPED))
					t.play();
				event.consume();
			}
		});

		double dy = 0.001;
		t.getKeyFrames().add(new KeyFrame(Duration.millis(3), (event)->{
			if(Math.abs(scrollPane.getVvalue() - currentScroll) > 0.0001){
				scrollPane.setVvalue(scrollPane.getVvalue() + dy * (scrollPane.getVvalue() > currentScroll ? -1 : 1));
			}else{
				currentScroll = -1;
				t.stop();
			}
		}));
		t.setCycleCount(Animation.INDEFINITE);
	}
	
}


