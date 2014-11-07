package customui.components;

import customui.skins.C3DTextFieldSkin;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;

public class C3DTextField extends TextField {

	@Override
	protected Skin<?> createDefaultSkin()	{
		return new C3DTextFieldSkin(this);
	}
}
