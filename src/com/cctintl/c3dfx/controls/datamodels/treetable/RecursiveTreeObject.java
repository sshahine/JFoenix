package com.cctintl.c3dfx.controls.datamodels.treetable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author sshahine
 *
 * @param <T> is the concrete object of the Tree table
 */

public class RecursiveTreeObject<T> {

	ObservableList<T> children = FXCollections.observableArrayList();
	
	public ObservableList<T> getChildren(){
		return children;
	}
	
}
