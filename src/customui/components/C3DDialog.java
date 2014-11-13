package customui.components;

import javax.swing.plaf.synth.Region;

import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;


public class C3DDialog extends StackPane {

	public static enum C3DDialogLayout{PLAIN, HEADING, ACTIONS, BACKDROP};
	public static enum C3DDialogTransition{CENTER, LEFT, RIGHT, TOP, BOTTOM};

	private C3DDialogLayout layout;
	private C3DDialogTransition transitionType;
	private Transition transition;
	
	
	public C3DDialog(Pane parent) {		
		final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);
        
        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().add(new Text("This is a Dialog"));

        StackPane overlayPane = new StackPane();
        overlayPane.getChildren().add(dialogVbox);
        overlayPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 100, 200, 0.3), null, null)));        
        parent.getChildren().add(overlayPane);
        
        
        overlayPane.setOnMouseClicked((e)->parent.getChildren().remove(overlayPane));
        
	}
	
	public void show(){
		
	}
	
}

