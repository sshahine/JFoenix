package demos.components;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class TextFieldDemo extends Application {

    private static final String FX_LABEL_FLOAT_TRUE = "-fx-label-float:true;";
    private static final String ERROR = "error";

    @Override
    public void start(Stage stage) throws Exception {

        final VBox pane = new VBox();
        pane.setSpacing(30);
        pane.setStyle("-fx-background-color:WHITE;-fx-padding:40;");

        pane.getChildren().add(new TextField());

        JFXTextField field = new JFXTextField();
        field.setLabelFloat(true);
        field.setPromptText("Type Something");
        pane.getChildren().add(field);


        JFXTextField disabledField = new JFXTextField();
        disabledField.setStyle(FX_LABEL_FLOAT_TRUE);
        disabledField.setPromptText("I'm disabled..");
        disabledField.setDisable(true);
        pane.getChildren().add(disabledField);

        JFXTextField validationField = new JFXTextField();

        validationField.setPromptText("With Validation..");
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Input Required");
        FontIcon warnIcon = new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE);
        warnIcon.getStyleClass().add(ERROR);
        validator.setIcon(warnIcon);
        validationField.getValidators().add(validator);
        validationField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                validationField.validate();
            }
        });
        pane.getChildren().add(validationField);


        JFXPasswordField passwordField = new JFXPasswordField();
        passwordField.setStyle(FX_LABEL_FLOAT_TRUE);
        passwordField.setPromptText("Password");
        validator = new RequiredFieldValidator();
        validator.setMessage("Password Can't be empty");
        warnIcon = new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE);
        warnIcon.getStyleClass().add(ERROR);
        validator.setIcon(warnIcon);
        passwordField.getValidators().add(validator);
        passwordField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                passwordField.validate();
            }
        });
        pane.getChildren().add(passwordField);

        final Scene scene = new Scene(pane, 600, 400, Color.WHITE);
        scene.getStylesheets()
            .add(TextFieldDemo.class.getResource("/css/jfoenix-components.css").toExternalForm());
        stage.setTitle("JFX TextField Demo ");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();


    }

    public static void main(String[] args) {
        launch(args);
    }


}
