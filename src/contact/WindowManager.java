package contact;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class WindowManager {

	public static void getPopup(){
		
		final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);
        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().add(new Text("This is a Dialog"));
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
        dialog.requestFocus();
        
//		Flow flow = new Flow(MainPresenter.class);
//        DefaultFlowContainer container = new DefaultFlowContainer();
//        flow.createHandler().start(container);
//        Scene scene = new Scene(container.getView(),600,600);
	}
}
