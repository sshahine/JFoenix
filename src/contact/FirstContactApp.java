package contact;

import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import contact.gui.main.MainPresenter;

public class FirstContactApp extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    public void start(Stage stage) throws Exception
    {
    	Font.loadFont("/resources/roboto/Roboto-Regular.ttf", 10);
    	
    	 Flow flow = new Flow(MainPresenter.class);
         DefaultFlowContainer container = new DefaultFlowContainer();
         flow.createHandler().start(container);
         Scene scene = new Scene(container.getView(),600,600);
         scene.getStylesheets().add("/resources/styles.css");
         stage.setScene(scene);
//         stage.initStyle(StageStyle.UNDECORATED);
         stage.setFullScreen(true);
         stage.show();
    	
    }
}

