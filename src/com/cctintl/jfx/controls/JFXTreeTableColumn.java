package com.cctintl.jfx.controls;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

import com.cctintl.jfx.controls.cells.editors.base.C3DTreeTableCell;
import com.cctintl.jfx.controls.datamodels.treetable.RecursiveTreeObject;


/**
 * @author sshahine
 *
 * @param <S>
 * @param <T>
 */

public class JFXTreeTableColumn<S, T> extends TreeTableColumn<S, T> {

	public JFXTreeTableColumn() {
		super();
		init();
	}
	
	public JFXTreeTableColumn(String text){
		super(text);
		init();
	}
	
	private void init(){
		this.setCellFactory(new Callback<TreeTableColumn<S,T>, TreeTableCell<S,T>>() {
			@Override
			public TreeTableCell<S, T> call(TreeTableColumn<S, T> param) {
				return new C3DTreeTableCell<S, T>(){
					 @Override protected void updateItem(T item, boolean empty) {
		                    if (item == getItem()) return;
		                    super.updateItem(item, empty);
		                    if (item == null) {
		                        super.setText(null);
		                        super.setGraphic(null);
		                    } else if (item instanceof Node) {
		                        super.setText(null);
		                        super.setGraphic((Node)item);
		                    } else {
		                        super.setText(item.toString());
		                        super.setGraphic(null);
		                    }
		                }
				};
			}
		});
	}
	
	public final boolean validateValue(CellDataFeatures<S, T> param){
		Object rowObject = param.getValue().getValue();
		if((rowObject instanceof RecursiveTreeObject && rowObject.getClass() == RecursiveTreeObject.class)
		|| (param.getTreeTableView() instanceof JFXTreeTableView && ((JFXTreeTableView<?>)param.getTreeTableView()).getGroupOrder().contains(this)))
			return false;
		return true;
	}
	
	public final ObservableValue<T> getComputedValue(CellDataFeatures<S, T> param){
		Object rowObject = param.getValue().getValue();
		if(rowObject instanceof RecursiveTreeObject){
			RecursiveTreeObject<?> item = (RecursiveTreeObject<?>) rowObject;
			if(item.getGroupedColumn() == this)
				return new ReadOnlyObjectWrapper(item.getGroupedValue());
		}
		return null;
	}
	
}
