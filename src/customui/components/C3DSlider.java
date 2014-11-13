package customui.components;

import javafx.scene.control.Skin;
import javafx.scene.control.Slider;
import customui.skins.C3DSliderSkin;

public class C3DSlider extends Slider {

	public C3DSlider() {
		super(0, 100, 50);
	}

	public C3DSlider(double min, double max, double value) {
		super(min, max, value);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new C3DSliderSkin(this);
	}

}
