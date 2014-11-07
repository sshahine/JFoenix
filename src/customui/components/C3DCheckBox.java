package customui.components;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Skin;
import customui.skins.C3DCheckBoxSkin;

public class C3DCheckBox extends CheckBox {
	
	public C3DCheckBox(String label){
		super(label);
	}
	
	public C3DCheckBox(){
		super();
	}
	
	@Override
	protected Skin<?> createDefaultSkin()	{
		return new C3DCheckBoxSkin(this);
	}
}