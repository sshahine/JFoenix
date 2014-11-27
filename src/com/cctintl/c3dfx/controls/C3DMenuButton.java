package com.cctintl.c3dfx.controls;

import com.cctintl.c3dfx.skins.C3DMenuButtonSkin;

import javafx.scene.control.MenuButton;
import javafx.scene.control.Skin;

public class C3DMenuButton extends MenuButton {
	@Override
	protected Skin<?> createDefaultSkin()	{
		return new C3DMenuButtonSkin(this);
	}
}
