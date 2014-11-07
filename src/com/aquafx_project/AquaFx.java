package com.aquafx_project;

import com.aquafx_project.controls.skin.styles.ControlSizeVariant;
import com.aquafx_project.controls.skin.styles.StyleFactory;
import com.aquafx_project.controls.skin.styles.styler.ButtonStyler;
import com.aquafx_project.controls.skin.styles.styler.CheckBoxStyler;
import com.aquafx_project.controls.skin.styles.styler.ChoiceBoxStyler;
import com.aquafx_project.controls.skin.styles.styler.ColorPickerStyler;
import com.aquafx_project.controls.skin.styles.styler.ComboBoxStyler;
import com.aquafx_project.controls.skin.styles.styler.LabelStyler;
import com.aquafx_project.controls.skin.styles.styler.PasswordFieldStyler;
import com.aquafx_project.controls.skin.styles.styler.ProgressBarStyler;
import com.aquafx_project.controls.skin.styles.styler.ProgressIndicatorStyler;
import com.aquafx_project.controls.skin.styles.styler.RadioButtonStyler;
import com.aquafx_project.controls.skin.styles.styler.ScrollBarStyler;
import com.aquafx_project.controls.skin.styles.styler.SliderStyler;
import com.aquafx_project.controls.skin.styles.styler.TabPaneStyler;
import com.aquafx_project.controls.skin.styles.styler.TextAreaStyler;
import com.aquafx_project.controls.skin.styles.styler.TextFieldStyler;
import com.aquafx_project.controls.skin.styles.styler.ToggleButtonStyler;
import com.aquafx_project.controls.skin.styles.styler.ToolBarStyler;

import javafx.application.Application;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * <p>
 * AquaFX is the facade, which wraps all functionality of AquaFX in one class. Its functionality
 * contains:
 * <ul>
 * <li>setting the stylesheet for AquaFX</li>
 * <li>changing the style of the {@link Stage} for different {@link StageStyle} types</li>
 * <li>changing the size of controls</li>
 * <li>styling panes to have a Mac OS look</li>
 * <li>providing {@link com.aquafx_project.controls.skin.styles.styler.Styler Styler} instances for skinned
 * Controls</li>
 * </ul>
 * 
 * <p>
 * EXAMPLES:
 * </p>
 * <p>
 * For using AquaFX to set the basic style use:
 * 
 * <pre>
 * <code>AquaFX.style();</code>
 * </pre>
 * 
 * <p>
 * To style the stage use:
 * 
 * <pre>
 * <code>AquaFX.styleStage(stage, StageStyle.DECORATED);</code>
 * </pre>
 * 
 * <p>
 * To resize a Control use:
 * 
 * <pre>
 * <code>AquaFX.resizeControl(control, ControlSizeVariant.SMALL);</code>
 * </pre>
 * 
 * <p>
 * To set a GrouoBox use:
 * 
 * <pre>
 * <code>AquaFX.setGroupBox(pane);</code>
 * </pre>
 * 
 * <p>
 * To retrieve a Styler use:
 * 
 * <pre>
 * <code>AquaFX.createButtonStyler();</code>
 * </pre>
 * 
 * @author claudinezillmann
 * 
 */
public class AquaFx {
    /**
     * The AQUA_CSS_NAME is a constant, which holds the path to the CSS file for AquaFX.
     */
    private final static String AQUA_CSS_NAME = AquaFx.class.getResource("mac_os.css").toExternalForm();

    /**
     * A Style Factory adds a Style Class to a Control for the possibility of CSS styling.
     */
    private static StyleFactory styleFactory = new StyleFactory();

    /**
     * Applies AquaFx skinning to the application.
     */
    public static void style() {
        Application.setUserAgentStylesheet(AQUA_CSS_NAME);
    }

    /**
     * Styles the Stage to the given StageStyle. Possible StageStyles are:
     * <ul>
     * <li>
     * StageStyle.DECORATED - Default type of a stage. It has a title, a full screen mode and
     * typical Mac OS buttons.</li>
     * <li>
     * StageStyle.UNDECORATED - The undecorated style hat no title bar but a shadow.</li>
     * <li>
     * StageStyle.TRANSPARENT - The transparent style just has the color which is defined as
     * background color.</li>
     * <li>StageStyle.UNIFIED - Unified style has a title bar as decorated, but with no border. This
     * makes it possible to create dialogs with typical toolbars, which have an intersection form
     * title to toolbar. This is a conditional feature, to check if it is supported see
     * <code>javafx.application.Platform#isSupported(javafx.application.ConditionalFeature);</code>
     * If the feature is not supported by the platform, this style downgrades to
     * StageStyle.DECORATED</li>
     * <li>StageStyle.UTILITY - The utility style has a smaller title bar. Ideal for utility
     * dialogs.</li>
     * </ul>
     * 
     * For further information see {@link StageStyle}.
     * 
     * 
     * @param stage
     *            The stage to be styled.
     * @param style
     *            The StageStyle to be applied.
     */
    public static void styleStage(Stage stage, StageStyle style) {
        stage.initStyle(style);
    }

    /**
     * Resizes the given Control to the given {@link com.aquafx_project.controls.skin.styles.ControlSizeVariant ControlSizeVariant}. Possible ControlSizeVariants are:
     * <ul>
     * <li>ControlSizeVariant.REGULAR - Regular size of a control. This is the default value with a
     * font size of 13.</li>
     * <li>ControlSizeVariant.SMALL - Small size of a control with a font size of 11.</li>
     * <li>ControlSizeVariant.MINI - Small size of a control with a font size of 9.</li>
     * </ul>
     * 
     * @param <T>
     *            Type of Control
     * @param control
     *            The Control to be resized
     * @param controlSizeVariant
     *            The ControlSizeVariant
     */
    public static <T extends Control> void resizeControl(T control, ControlSizeVariant controlSizeVariant) {
        styleFactory.addStyles(control, controlSizeVariant);
    }

    /**
     * Styles a Pane to a GroupBox, which means the panes' background color is a darker grey and it
     * has rounded borders. This is applicable for all types, derived from pane.
     * 
     * @param pane
     *            The Pane to be styled
     */
    public static void setGroupBox(Pane pane) {
        pane.getStyleClass().add("aqua-group-box");
    }

    /***************************************************************
     * * StylerClasses for skinned Controls * *
     **************************************************************/

    /**
     * @return The ButtonStyler associated with the Button class
     */
    public static ButtonStyler createButtonStyler() {
        return ButtonStyler.create();
    }

    /**
     * @return The ToggleButtonStyler associated with the ToggleButton class
     */
    public static ToggleButtonStyler createToggleButtonStyler() {
        return ToggleButtonStyler.create();
    }

    /**
     * @return The LabelStyler associated with the Label class
     */
    public static LabelStyler createLabelStyler() {
        return LabelStyler.create();
    }

    /**
     * @return The TextFieldStyler associated with the TextField class
     */
    public static TextFieldStyler createTextFieldStyler() {
        return TextFieldStyler.create();
    }

    /**
     * @return The TextAreaStyler associated with the TextArea class
     */
    public static TextAreaStyler createTextAreaStyler() {
        return TextAreaStyler.create();
    }

    /**
     * @return The PasswordFieldStyler associated with the PasswordField class
     */
    public static PasswordFieldStyler createPasswordFieldStyler() {
        return PasswordFieldStyler.create();
    }

    /**
     * @return The CheckBoxStyler associated with the CheckBox class
     */
    public static CheckBoxStyler createCheckBoxStyler() {
        return CheckBoxStyler.create();
    }

    /**
     * @return The RadioButtonStyler associated with the RadioButton class
     */
    public static RadioButtonStyler createRadioButtonStyler() {
        return RadioButtonStyler.create();
    }

    /**
     * @return The ChoiceBoxStyler associated with the ChoiceBox class
     */
    public static ChoiceBoxStyler createChoiceBoxStyler() {
        return ChoiceBoxStyler.create();
    }

    /**
     * @return The ComboBoxStyler associated with the ComboBox class
     */
    public static ComboBoxStyler createComboBoxStyler() {
        return ComboBoxStyler.create();
    }

    /**
     * @return The ColorPickerStyler associated with the ColorPicker class
     */
    public static ColorPickerStyler createColorPickerStyler() {
        return ColorPickerStyler.create();
    }

    /**
     * @return The ProgressBarStyler associated with the ProgressBar class
     */
    public static ProgressBarStyler createProgressBarStyler() {
        return ProgressBarStyler.create();
    }

    /**
     * @return The ProgressIndicatorStyler associated with the ProgressIndicator class
     */
    public static ProgressIndicatorStyler createProgressIndicatorStyler() {
        return ProgressIndicatorStyler.create();
    }

    /**
     * @return The SliderStyler associated with the Slider class
     */
    public static SliderStyler createSliderStyler() {
        return SliderStyler.create();
    }

    /**
     * @return The ScrollBarStyler associated with the ScrollBar class
     */
    public static ScrollBarStyler createScrollBarStyler() {
        return ScrollBarStyler.create();
    }

    /**
     * @return The TabPaneStyler associated with the TabPane class
     */
    public static TabPaneStyler createTabPaneStyler() {
        return TabPaneStyler.create();
    }

    /**
     * @return The ToolBarStyler associated with the ToolBar class
     */
    public static ToolBarStyler createToolBarStyler() {
        return ToolBarStyler.create();
    }
}
