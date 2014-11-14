package customui.components;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Skin;
import customui.skins.C3DButtonSkin;

public class C3DButton extends Button {
	
	public static enum ButtonType{FLAT, RAISED};	
	
	private ButtonType type;
	
	public C3DButton() {
		super();
	}	
		
	public C3DButton(String text){
		super(text);
	}

	public C3DButton(String text, Node graphic){
		super(text, graphic);
	}
	
	public ButtonType getType() {
		return type;
	}

	public void setType(ButtonType type) {
		this.type = type;
	}

	@Override
	protected Skin<?> createDefaultSkin()	{
		return new C3DButtonSkin(this);
	}
	
	
	
}
