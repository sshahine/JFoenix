package customui.components;

import javafx.scene.control.MenuButton;
import javafx.scene.control.Skin;
import customui.skins.C3DMenuButtonSkin;

public class C3DMenuButton extends MenuButton {
	@Override
	protected Skin<?> createDefaultSkin()	{
		return new C3DMenuButtonSkin(this);
	}
}
