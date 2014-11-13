package customui.skins;

import javafx.scene.control.MenuButton;

import com.sun.javafx.scene.control.skin.MenuButtonSkin;

public class C3DMenuButtonSkin extends MenuButtonSkin {

	public C3DMenuButtonSkin(MenuButton menuButton) {
		super(menuButton);
	}


	@Override protected void layoutChildren(final double x, final double y,
			final double w, final double h) {
		final double arrowButtonWidth = snapSize(arrowButton.prefWidth(-1));
		label.resizeRelocate(x, y, w - arrowButtonWidth, h);
		arrowButton.resizeRelocate(x+(w-arrowButtonWidth), y, arrowButtonWidth, h);
	}

}
