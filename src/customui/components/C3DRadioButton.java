package customui.components;

import javafx.scene.control.RadioButton;
import javafx.scene.control.Skin;
import customui.skins.C3DRadioButtonSkin;

public class C3DRadioButton extends RadioButton {

	private static final String DEFAULT_STYLE_CLASS = "c3d-radio-button";

	public C3DRadioButton(String text) {
		super(text);
		getStyleClass().setAll(DEFAULT_STYLE_CLASS);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new C3DRadioButtonSkin(this);
	}

	@Override
	protected String getUserAgentStylesheet() {
		return C3DRadioButton.class.getResource("/resources/css/c3dobjects.css").toExternalForm();
	}

}
