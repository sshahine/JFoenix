package customui.components;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.Skin;
import customui.skins.C3DProgressBarSkin;

public class C3DProgressBar extends ProgressBar {
	@Override
	protected Skin<?> createDefaultSkin()	{
		return new C3DProgressBarSkin(this);
	}
}
