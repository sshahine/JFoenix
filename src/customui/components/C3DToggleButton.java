package customui.components;

import javafx.scene.control.Skin;
import javafx.scene.control.ToggleButton;
import customui.skins.C3DToggleButtonSkin;

public class C3DToggleButton extends ToggleButton {

	@Override
	protected Skin<?> createDefaultSkin()	{
		return new C3DToggleButtonSkin(this);
	}
}
