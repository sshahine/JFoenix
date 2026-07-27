package com.jfoenix.controls;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Test application demonstrating JFXLimitedTextField functionality.
 * Shows various use cases for character limiting and text validation.
 */
public class JFXLimitedTextFieldTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        // Test 1: Email field with 100 character limit
        Label emailLabel = new Label("Email (max 100 characters):");
        JFXLimitedTextField emailField = new LimitedTextFieldBuilder()
                .maxCharacters(100)
                .promptText("Enter email address")
                .prefWidth(300)
                .build();

        // Character counter for email
        Label emailCounter = new Label("0 / 100");
        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            int length = newVal != null ? newVal.length() : 0;
            emailCounter.setText(length + " / 100");
        });

        // Test 2: Username field (20 character limit, alphanumeric only)
        Label usernameLabel = new Label("Username (max 20 characters, alphanumeric):");
        JFXLimitedTextField usernameField = new LimitedTextFieldBuilder()
                .maxCharacters(20)
                .promptText("Enter username")
                .prefWidth(300)
                .build();

        Label usernameCounter = new Label("0 / 20");
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            int length = newVal != null ? newVal.length() : 0;
            usernameCounter.setText(length + " / 20");
        });

        // Test 3: Phone number field (15 character limit)
        Label phoneLabel = new Label("Phone Number (max 15 characters):");
        JFXLimitedTextField phoneField = new LimitedTextFieldBuilder()
                .maxCharacters(15)
                .promptText("Enter phone number")
                .prefWidth(300)
                .build();

        Label phoneCounter = new Label("0 / 15");
        phoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            int length = newVal != null ? newVal.length() : 0;
            phoneCounter.setText(length + " / 15");
        });

        // Test 4: Bio field with 500 character limit
        Label bioLabel = new Label("Bio (max 500 characters):");
        JFXLimitedTextField bioField = new LimitedTextFieldBuilder()
                .maxCharacters(500)
                .promptText("Tell us about yourself")
                .prefWidth(300)
                .build();

        Label bioCounter = new Label("0 / 500");
        bioField.textProperty().addListener((obs, oldVal, newVal) -> {
            int length = newVal != null ? newVal.length() : 0;
            bioCounter.setText(length + " / 500");
        });

        // Test 5: Single character field
        Label singleCharLabel = new Label("Single Character (max 1):");
        JFXLimitedTextField singleCharField = new LimitedTextFieldBuilder()
                .maxCharacters(1)
                .promptText("One character")
                .prefWidth(300)
                .build();

        Label singleCharCounter = new Label("0 / 1");
        singleCharField.textProperty().addListener((obs, oldVal, newVal) -> {
            int length = newVal != null ? newVal.length() : 0;
            singleCharCounter.setText(length + " / 1");
        });

        // Add all components to root
        root.getChildren().addAll(
                emailLabel, emailField, emailCounter,
                new Label(""),
                usernameLabel, usernameField, usernameCounter,
                new Label(""),
                phoneLabel, phoneField, phoneCounter,
                new Label(""),
                bioLabel, bioField, bioCounter,
                new Label(""),
                singleCharLabel, singleCharField, singleCharCounter
        );

        Scene scene = new Scene(root, 400, 700);
        primaryStage.setTitle("JFXLimitedTextField Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
