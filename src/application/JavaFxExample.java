package application;

import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import application.gui.MainController;

public class JavaFxExample extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		 Flow flow = new Flow(MainController.class);
         
         DefaultFlowContainer container = new DefaultFlowContainer();
         flow.createHandler().start(container);
   
         Scene scene = new Scene(container.getView(),600,600);
         primaryStage.setScene(scene);
         primaryStage.show();
	}
}
