package customui.skins;


import javafx.scene.control.ListView;

import com.sun.javafx.scene.control.skin.ListViewSkin;

public class C3DListViewSkin<T> extends ListViewSkin<T> {

	public C3DListViewSkin(ListView<T> listView) {
		super(listView);
		listView.getStyleClass().add("c3d-list-view");
	}

}
