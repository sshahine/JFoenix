package com.jfoenix.controls;

/**
 * Builder class for configuring JFXLimitedTextField instances with fluent API.
 * All created fields have floating labels enabled by default.
 *
 * Example usage:
 * <pre>
 * JFXLimitedTextField emailField = new LimitedTextFieldBuilder()
 *     .maxCharacters(100)
 *     .promptText("Enter email")
 *     .prefWidth(300)
 *     .build();
 * </pre>
 *
 * @author JGood
 * @version 1.0
 */
public class LimitedTextFieldBuilder {

    private String initialText;
    private int maxCharacters;
    private String promptText;
    private double prefWidth;
    private double prefHeight;

    /**
     * Constructor: Creates a new builder with default values.
     * Note: Floating labels are enabled by default on all built instances.
     */
    public LimitedTextFieldBuilder() {
        this.initialText = "";
        this.maxCharacters = Integer.MAX_VALUE;
        this.promptText = "";
        this.prefWidth = -1;
        this.prefHeight = -1;
    }

    /**
     * Set the initial text for the text field.
     *
     * @param text the initial text
     * @return this builder for chaining
     */
    public LimitedTextFieldBuilder text(String text) {
        this.initialText = text != null ? text : "";
        return this;
    }

    /**
     * Set the maximum number of characters allowed.
     *
     * @param max the maximum characters
     * @return this builder for chaining
     */
    public LimitedTextFieldBuilder maxCharacters(int max) {
        this.maxCharacters = max;
        return this;
    }

    /**
     * Set the prompt text (floating label) for the text field.
     *
     * @param prompt the prompt text that becomes the floating label
     * @return this builder for chaining
     */
    public LimitedTextFieldBuilder promptText(String prompt) {
        this.promptText = prompt != null ? prompt : "";
        return this;
    }

    /**
     * Set the preferred width of the text field.
     *
     * @param width the preferred width
     * @return this builder for chaining
     */
    public LimitedTextFieldBuilder prefWidth(double width) {
        this.prefWidth = width;
        return this;
    }

    /**
     * Set the preferred height of the text field.
     *
     * @param height the preferred height
     * @return this builder for chaining
     */
    public LimitedTextFieldBuilder prefHeight(double height) {
        this.prefHeight = height;
        return this;
    }

    /**
     * Build and return the configured JFXLimitedTextField.
     * The returned field will have:
     * - Floating labels enabled (default)
     * - Character limit set (if configured)
     * - Prompt text set (if configured)
     * - Preferred dimensions set (if configured)
     *
     * @return a new JFXLimitedTextField with the configured properties
     */
    public JFXLimitedTextField build() {
        JFXLimitedTextField textField = new JFXLimitedTextField(initialText);
        textField.setMaxCharacters(maxCharacters);
        textField.setPromptText(promptText);
        // Floating labels are already enabled in JFXLimitedTextField constructor
        
        if (prefWidth > 0) {
            textField.setPrefWidth(prefWidth);
        }
        if (prefHeight > 0) {
            textField.setPrefHeight(prefHeight);
        }
        
        return textField;
    }
}
