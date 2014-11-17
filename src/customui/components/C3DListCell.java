package customui.components;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.Skin;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import customui.skins.C3DListCellSkin;

public class C3DListCell<T> extends ListCell<T> {
	
	@Override
	protected Skin<?> createDefaultSkin()	{
		return new C3DListCellSkin<T>(this);
	}
	
	@Override
	public void updateItem(T item, boolean empty){
	    super.updateItem(item,empty);
	    if(item != null && (item instanceof Region || item instanceof Control)) {
	    	StackPane cellContainer = new StackPane();
	    	cellContainer.getChildren().add((Node) item);
	    	cellContainer.getStyleClass().add("c3d-list-cell-holder");	    	
	    	cellContainer.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));	    	
	    	DepthManager.setDepth(cellContainer, 1);
	    	Rippler rippler = new Rippler(cellContainer);
	    	rippler.setRipplerFill(Color.GREEN);
	    	StackPane mainContainer = new StackPane();
	    	mainContainer.getChildren().add(rippler);
	    	StackPane.setMargin(rippler, new Insets(0,5,7,5));
	        setGraphic(mainContainer);
	    }
	}
}
