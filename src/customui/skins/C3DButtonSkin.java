package customui.skins;

import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import com.sun.javafx.scene.control.skin.ButtonSkin;
import com.sun.javafx.scene.control.skin.LabeledText;

import customui.components.C3DButton;
import customui.components.C3DButton.ButtonType;
import customui.components.DepthManager;
import customui.components.Rippler;

public class C3DButtonSkin extends ButtonSkin {
	
	private final AnchorPane main = new AnchorPane();
	
	private boolean invalid = true;
	private StackPane buttonComponents;
	private Rippler rippler;
	
	private final Color disabledColor = Color.valueOf("#EAEAEA");
	
	public C3DButtonSkin(Button button) {
		super(button);

		// create button
		Rectangle buttonRect = new Rectangle();
		buttonRect.setArcHeight(7);
		buttonRect.setArcWidth(7);
		buttonComponents = new StackPane();
		buttonComponents.getChildren().add(buttonRect);
		rippler = new Rippler(buttonComponents){
			public Shape getMask(){
				Rectangle mask = new Rectangle(buttonRect.getWidth() - 0.1,buttonRect.getHeight() - 0.1); // -0.1 to prevent resizing the anchor pane
				mask.setArcHeight(buttonRect.getArcHeight());
				mask.setArcWidth(buttonRect.getArcWidth());					
				return mask;
			}
		};
		main.getChildren().add(rippler);
		
		
		if(button.isDisabled()) buttonRect.setFill(disabledColor);		
		else buttonRect.setFill(button.getBackground().getFills().get(0).getFill());
		button.setStyle("-fx-background-color: TRANSPARENT");
		
		
		// add listeners to the button
		button.widthProperty().addListener((o,oldVal,newVal)->{
			buttonRect.setWidth(newVal.doubleValue());
		});
		button.heightProperty().addListener((o,oldVal,newVal)->{
			buttonRect.setHeight(newVal.doubleValue());
		});
		
		if(((C3DButton)getSkinnable()).getType() == ButtonType.RAISED)
			DepthManager.setDepth(rippler, 2);
		
		updateChildren();
	}
	
	@Override protected void updateChildren() {
		super.updateChildren();
		if (main != null) {
			getChildren().add(main);
		}
	}
	
	@Override 
	protected void layoutChildren(final double x, final double y, final double w, final double h) {
		if(invalid){
			rippler.setColor(((LabeledText)getChildren().get(0)).getFill());
			((LabeledText)getChildren().get(0)).fillProperty().addListener((o,oldVal,newVal)-> rippler.setColor(newVal));
			buttonComponents.getChildren().add(getChildren().get(0));
			invalid = false;
		}
		layoutLabelInArea(x, y, w, h);
	}
}
