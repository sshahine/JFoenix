package customui.components;

import javafx.scene.control.RadioButton;
import javafx.scene.control.Skin;
import customui.skins.C3DRadioButtonSkin;

public class C3DRadioButton extends RadioButton {

	public C3DRadioButton(String text) {
		super(text);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new C3DRadioButtonSkin(this);
	}

}
