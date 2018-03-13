package demos.gui.uicomponents;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

@ViewController(value = "/fxml/ui/Alert.fxml", title = "Material Design Example")
public class AlertController {

    public static final String CONTENT_PANE = "ContentPane";
    @FXMLViewFlowContext
    private ViewFlowContext context;
    @FXML
    private JFXButton centerButton;
    @FXML
    private JFXButton topButton;
    @FXML
    private JFXButton rightButton;
    @FXML
    private JFXButton bottomButton;
    @FXML
    private JFXButton leftButton;
    @FXML
    private JFXButton acceptButton;
    @FXML
    private StackPane root;
    @FXML
    private JFXAlert<String> alert;

    /**
     * init fxml when loaded.
     */
    @PostConstruct
    public void init() {

        alert = new JFXAlert<>();
        alert.setContent(new Label("Hey this is an alert."));

        centerButton.setOnMouseClicked((e) -> {
            alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
            alert.show();
        });

//        topButton.setOnMouseClicked((e) -> {
//            alert.setTransitionType(DialogTransition.TOP);
//            alert.show((StackPane) context.getRegisteredObject(CONTENT_PANE));
//        });
//
//        rightButton.setOnMouseClicked((e) -> {
//            alert.setTransitionType(DialogTransition.RIGHT);
//            alert.show((StackPane) context.getRegisteredObject(CONTENT_PANE));
//        });
//
//        bottomButton.setOnMouseClicked((e) -> {
//            alert.setTransitionType(DialogTransition.BOTTOM);
//            alert.show((StackPane) context.getRegisteredObject(CONTENT_PANE));
//        });
//
//        leftButton.setOnMouseClicked((e) -> {
//            alert.setTransitionType(DialogTransition.LEFT);
//            alert.show((StackPane) context.getRegisteredObject(CONTENT_PANE));
//        });
//
//        acceptButton.setOnMouseClicked((e) -> alert.close());
    }

}
