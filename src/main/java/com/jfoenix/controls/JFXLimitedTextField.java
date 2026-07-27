package com.jfoenix.controls;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Skin;

/**
 * Extended JFXTextField with character limiting and text length validation.
 * Extends JFXTextField to add character limiting capabilities while preserving
 * all Material Design features (floating labels, animations, validation, etc.)
 *
 * @author JGood
 * @version 1.0
 */
public class JFXLimitedTextField extends JFXTextField {

    /**
     * Property to limit the maximum number of characters allowed.
     * Default: Integer.MAX_VALUE (unlimited)
     */
    private final IntegerProperty maxCharacters = new SimpleIntegerProperty(this, "maxCharacters", Integer.MAX_VALUE);

    /**
     * Constructor: Creates a new JFXLimitedTextField with no initial text.
     */
    public JFXLimitedTextField() {
        this("");
    }

    /**
     * Constructor: Creates a new JFXLimitedTextField with initial text.
     *
     * @param text the initial text to display
     */
    public JFXLimitedTextField(String text) {
        super(text);
        initialize();
    }

    /**
     * Initialize the limited text field with validation listeners.
     */
    private void initialize() {
        // Listen to text changes and enforce character limit
        this.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() > getMaxCharacters()) {
                int caretPos = Math.min(this.getCaretPosition(), getMaxCharacters());
                this.setText(newVal.substring(0, getMaxCharacters()));
                this.positionCaret(caretPos);
            }
        });
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXLimitedTextFieldSkin<>(this);
    }

    /**
     * Get the maximum number of characters allowed.
     *
     * @return the maximum characters value
     */
    public int getMaxCharacters() {
        return maxCharacters.get();
    }

    /**
     * Set the maximum number of characters allowed.
     *
     * @param max the maximum number of characters
     */
    public void setMaxCharacters(int max) {
        maxCharacters.set(max);
    }

    /**
     * Get the maxCharacters property for binding.
     *
     * @return the maxCharacters IntegerProperty
     */
    public IntegerProperty maxCharactersProperty() {
        return maxCharacters;
    }

    /**
     * Get the current character count.
     *
     * @return the number of characters currently in the text field
     */
    public int getCharacterCount() {
        String text = this.getText();
        return text != null ? text.length() : 0;
    }

    /**
     * Get remaining characters that can be entered.
     *
     * @return the number of remaining characters
     */
    public int getRemainingCharacters() {
        return getMaxCharacters() - getCharacterCount();
    }
}
