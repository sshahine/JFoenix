package demos.components;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXNotifications;
import com.jfoenix.notification.template.JFXSimpleNotificationTemplate;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public final class NotificationDemo extends Application {

    @Override
    public void start(Stage stage) {
        JFXButton buttonTopLeft = new JFXButton("Open TOP LEFT");
        JFXButton buttonTopCenter = new JFXButton("Open TOP CENTER");
        JFXButton buttonTopRight = new JFXButton("Open TOP RIGHT");
        JFXButton buttonCenterLeft = new JFXButton("Open CENTER LEFT");
        JFXButton buttonCenter = new JFXButton("Open CENTER");
        JFXButton buttonCenterRight = new JFXButton("Open CENTER RIGHT");
        JFXButton buttonBottomLeft = new JFXButton("Open BOTTOM LEFT");
        JFXButton buttonBottomCenter = new JFXButton("Open BOTTOM CENTER");
        JFXButton buttonBottomRight = new JFXButton("Open BOTTOM RIGHT");

        buttonTopLeft.setOnAction( __ -> showDialog(Pos.TOP_LEFT));
        buttonTopCenter.setOnAction( __ -> showDialog(Pos.TOP_CENTER));
        buttonTopRight.setOnAction( __ -> showDialog(Pos.TOP_RIGHT));
        buttonCenterLeft.setOnAction( __ -> showDialog(Pos.CENTER_LEFT));
        buttonCenter.setOnAction( __ -> showDialog(Pos.CENTER));
        buttonCenterRight.setOnAction( __ -> showDialog(Pos.CENTER_RIGHT));
        buttonBottomLeft.setOnAction( __ -> showDialog(Pos.BOTTOM_LEFT));
        buttonBottomCenter.setOnAction( __ -> showDialog(Pos.BOTTOM_CENTER));
        buttonBottomRight.setOnAction( __ -> showDialog(Pos.BOTTOM_RIGHT));
        FlowPane pane = new FlowPane();
        pane.setHgap(5);
        pane.setVgap(5);
        pane.getChildren().addAll(buttonTopLeft,buttonTopCenter,buttonTopRight,buttonCenterLeft,
            buttonCenter,buttonCenterRight,buttonBottomLeft,buttonBottomCenter,buttonBottomRight);
        pane.getChildren().forEach(button -> {
            button.setStyle("-fx-background-color: WHITE");
            ((JFXButton)button).setButtonType(JFXButton.ButtonType.RAISED);
        });
        final Scene scene = new Scene(pane, 800, 200);
        scene.getStylesheets().addAll(NotificationDemo.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
            NotificationDemo.class.getResource("/css/jfoenix-design.css").toExternalForm(),
            NotificationDemo.class.getResource("/css/jfoenix-components.css").toExternalForm());
        stage.setTitle("JFXNotification Demo");
        stage.setScene(scene);
        stage.show();
    }

    private void showDialog(Pos pos){
        JFXSimpleNotificationTemplate template = new JFXSimpleNotificationTemplate();
        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.GLASS);
        template.setHeader(icon, "Fast Food");
        template.setBody("Lily (MacDonald)","Su pedido esta listo, solo debe recogerlo en el lugar mas solicitado");
        JFXButton aceptar = new JFXButton("Responder");
        template.setActions(aceptar);
        JFXNotifications.create().position(pos)
            .hideAfter(Duration.seconds(6))
            .template(template)
            .showMessage();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
