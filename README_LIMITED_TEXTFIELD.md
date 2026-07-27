# JFXLimitedTextField

A custom JavaFX TextField control that provides character limiting and text length validation features. This control extends the standard JavaFX `TextField` with additional properties to restrict user input.

## Features

- **Character Limiting**: Set a maximum number of characters allowed in the text field
- **Text Length Limiting**: Configure maximum text length constraints
- **Input Validation**: Automatically validates and truncates input that exceeds limits
- **Paste Handling**: Intelligently handles paste operations (Ctrl+V / Cmd+V)
- **Event Filtering**: Prevents invalid characters from being entered at the keyboard level
- **Builder Pattern**: Fluent API for easy configuration
- **Character Counter**: Easy integration with UI to show remaining characters

## Classes

### JFXLimitedTextField
The main control class that extends `TextField`. Provides properties and methods for character limiting.

**Properties:**
- `maxCharacters` (IntegerProperty): Maximum number of characters allowed (default: Integer.MAX_VALUE)
- `maxLength` (IntegerProperty): Maximum text length (default: Integer.MAX_VALUE)

**Methods:**
- `getMaxCharacters()`: Get the maximum characters limit
- `setMaxCharacters(int)`: Set the maximum characters limit
- `maxCharactersProperty()`: Get the maxCharacters property for binding
- `getMaxLength()`: Get the maximum length limit
- `setMaxLength(int)`: Set the maximum length limit
- `maxLengthProperty()`: Get the maxLength property for binding

### JFXLimitedTextFieldSkin
Custom skin that extends `TextFieldSkin` and enforces the character limits at the input level.

**Methods:**
- `getCharacterCount()`: Returns the current number of characters
- `getRemainingCharacters()`: Returns how many more characters can be entered

### LimitedTextFieldBuilder
Builder class for fluent configuration of `JFXLimitedTextField` instances.

**Methods:**
- `text(String)`: Set initial text
- `maxCharacters(int)`: Set maximum characters
- `maxLength(int)`: Set maximum length
- `promptText(String)`: Set placeholder text
- `prefWidth(double)`: Set preferred width
- `prefHeight(double)`: Set preferred height
- `build()`: Create and return the configured TextField

## Usage Examples

### Basic Usage
```java
// Create a simple limited text field
JFXLimitedTextField textField = new JFXLimitedTextField();
textField.setMaxCharacters(100);
textField.setPromptText("Enter text (max 100 characters)");
```

### Using the Builder
```java
JFXLimitedTextField emailField = new LimitedTextFieldBuilder()
    .maxCharacters(100)
    .promptText("Enter email address")
    .prefWidth(300)
    .build();
```

### With Character Counter
```java
JFXLimitedTextField bioField = new LimitedTextFieldBuilder()
    .maxCharacters(500)
    .promptText("Tell us about yourself")
    .prefWidth(300)
    .build();

Label counter = new Label("0 / 500");
bioField.textProperty().addListener((obs, oldVal, newVal) -> {
    int length = newVal != null ? newVal.length() : 0;
    counter.setText(length + " / 500");
});
```

### Common Use Cases

**Username Field (20 characters)**
```java
JFXLimitedTextField usernameField = new LimitedTextFieldBuilder()
    .maxCharacters(20)
    .promptText("Enter username")
    .build();
```

**Phone Number Field (15 characters)**
```java
JFXLimitedTextField phoneField = new LimitedTextFieldBuilder()
    .maxCharacters(15)
    .promptText("Enter phone number")
    .build();
```

**Tweet-like Field (280 characters)**
```java
JFXLimitedTextField tweetField = new LimitedTextFieldBuilder()
    .maxCharacters(280)
    .promptText("What's happening?!")
    .prefWidth(400)
    .build();
```

## Implementation Details

### Input Validation
- **Key Typed Events**: Prevents characters from being typed if the limit would be exceeded
- **Key Released Events**: Handles paste operations and post-edit validation
- **Text Property Listener**: Automatically truncates text if exceeded programmatically

### Character Filtering
The skin provides a foundation for adding character-specific validation. You can extend the `handleKeyTyped` method to add custom filtering:

```java
// Example: Allow only digits
if (!character.matches("[0-9]")) {
    event.consume();
}
```

### Paste Handling
The skin detects paste operations (Ctrl+V / Cmd+V) and validates the resulting text, truncating if necessary while preserving the caret position.

## Styling

The control inherits all standard JavaFX TextField styling. You can style it using CSS:

```css
.jfx-limited-text-field {
    -fx-font-size: 14;
    -fx-padding: 10;
    -fx-border-radius: 4;
}
```

## Testing

Run `JFXLimitedTextFieldTest` to see a demonstration with various use cases:
- Email field (100 characters)
- Username field (20 characters)
- Phone number field (15 characters)
- Bio field (500 characters)
- Single character field (1 character)

Each field includes a live character counter showing current/maximum characters.

## Future Enhancements

- Pattern-based input validation (regex support)
- Custom character filtering rules
- Numeric-only mode
- Alphanumeric-only mode
- Input formatting (phone numbers, credit cards, etc.)
- Real-time validation feedback with visual indicators
