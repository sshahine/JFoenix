package customui.components;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.util.Callback;
import customui.skins.C3DListViewSkin;

public class C3DListView<T> extends ListView<T> {
	
    public C3DListView() {
    	super();
    	this.setCellFactory(new Callback<ListView<T>, ListCell<T>>() {
	        @Override
	        public ListCell<T> call(ListView<T> listView) {
	            return new C3DListCell<T>();
	        }
	    });
	}	 
	
	@Override
	protected Skin<?> createDefaultSkin()	{
		return new C3DListViewSkin<T>(this);
	}
	
	
}
