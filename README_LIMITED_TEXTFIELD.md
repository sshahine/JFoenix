# JFXLimitedTextField

A custom JavaFX TextField control that extends JFoenix's `JFXTextField` with character limiting and text length validation features. **Floating labels are enabled by default** to match modern Material Design practices.

## Features

- **Floating Labels Enabled by Default** - All fields automatically display floating labels (no configuration needed)
- **Character Limiting** - Set a maximum number of characters allowed in the text field
- **Material Design Animations** - Inherits all JFoenix animations, focus effects, and transitions
- **Validation Support** - Full support for validators with error display and icons
- **Input Filtering** - Prevents invalid input at the keyboard level
- **Paste Handling** - Intelligently handles paste operations (Ctrl+V / Cmd+V)
- **Character Counter** - Easy integration with UI to show remaining characters
- **Builder Pattern** - Fluent API for easy configuration

## Classes

### JFXLimitedTextField
The main control class that extends `JFXTextField` with character limiting.

**Key Difference:** Floating labels are **enabled by default** - no need to call `setLabelFloat(true)`

**Properties:**
- `maxCharacters` (IntegerProperty): Maximum number of characters allowed (default: Integer.MAX_VALUE)
- `promptText` (inherited): Becomes the floating label automatically

**Methods:**
- `getMaxCharacters()`: Get the maximum characters limit
- `setMaxCharacters(int)`: Set the maximum characters limit
- `maxCharactersProperty()`: Get the maxCharacters property for binding
- `getCharacterCount()`: Get current character count
- `getRemainingCharacters()`: Get remaining characters allowed

### JFXLimitedTextFieldSkin
Custom skin that extends `JFXTextFieldSkin` and enforces character limits at the input level. Preserves all JFoenix Material Design features.

### LimitedTextFieldBuilder
Builder class for fluent configuration of `JFXLimitedTextField` instances.

**Methods:**
- `text(String)`: Set initial text
- `maxCharacters(int)`: Set maximum characters
- `promptText(String)`: Set floating label text
- `prefWidth(double)`: Set preferred width
- `prefHeight(double)`: Set preferred height
- `build()`: Create and return the configured TextField

## Usage Examples

### Basic Usage - No Configuration Needed for Floating Labels
```java
// Floating labels are already enabled!
JFXLimitedTextField textField = new JFXLimitedTextField();
textField.setMaxCharacters(100);
textField.setPromptText("Enter text");
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
    counter.setText(bioField.getCharacterCount() + " / 500");
});
```

### With Validation
```java
JFXLimitedTextField usernameField = new LimitedTextFieldBuilder()
    .maxCharacters(20)
    .promptText("Enter username")
    .prefWidth(300)
    .build();

RequiredFieldValidator validator = new RequiredFieldValidator();
validator.setMessage("Username is required");
usernameField.getValidators().add(validator);

usernameField.focusedProperty().addListener((o, oldVal, newVal) -> {
    if (!newVal) {
        usernameField.validate();
    }
});
```

## Common Use Cases

**Username Field (20 characters)**
```java
JFXLimitedTextField usernameField = new LimitedTextFieldBuilder()
    .maxCharacters(20)
    .promptText("Enter username")
    .prefWidth(300)
    .build();
```

**Email Field (100 characters)**
```java
JFXLimitedTextField emailField = new LimitedTextFieldBuilder()
    .maxCharacters(100)
    .promptText("Enter email address")
    .prefWidth(300)
    .build();
```

**Phone Number Field (15 characters)**
```java
JFXLimitedTextField phoneField = new LimitedTextFieldBuilder()
    .maxCharacters(15)
    .promptText("Enter phone number")
    .prefWidth(300)
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

### Floating Labels
All `JFXLimitedTextField` instances have floating labels enabled by default. The label animates from the baseline to above the field when you start typing.

### Input Validation
- **Key Typed Events**: Prevents characters from being typed if the limit would be exceeded
- **Key Released Events**: Handles paste operations and post-edit validation
- **Text Property Listener**: Automatically truncates text if exceeded programmatically

### Character Filtering
The skin provides a foundation for adding character-specific validation. You can extend the skin to add custom filtering:

```java
// Example in JFXLimitedTextFieldSkin.handleKeyTyped():
// Allow only digits
if (!character.matches("[0-9]")) {
    event.consume();
}
```

### Paste Handling
The skin detects paste operations (Ctrl+V / Cmd+V) and validates the resulting text, truncating if necessary while preserving the caret position.

## Styling

Inherits all JFoenix TextField styling. You can style it using CSS:

```css
.jfx-limited-text-field {
    -fx-font-size: 14;
    -fx-padding: 10;
}

.jfx-limited-text-field:focused {
    -fx-focus-color: #3f51b5;
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

## Differences from JFXTextField

| Feature | JFXTextField | JFXLimitedTextField |
|---------|--------------|-------------------|
| Floating Labels | Manual (call `setLabelFloat(true)`) | **Enabled by Default** |
| Character Limit | None | Configurable per instance |
| Input Filtering | None | Prevents exceeding limit |
| Use Case | General purpose | Form fields with limits |

## Future Enhancements

- Pattern-based input validation (regex support)
- Numeric-only mode
- Alphanumeric-only mode
- Phone number formatting
- Credit card formatting
- Real-time validation feedback with visual indicators
- Custom character filtering rules
