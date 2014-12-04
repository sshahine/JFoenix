package demos.gui.uicomponents;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.util.VetoException;
import javafx.fxml.FXML;

import javax.annotation.PostConstruct;

import com.cctintl.c3dfx.controls.C3DButton;
import com.cctintl.c3dfx.controls.C3DListView;

@FXMLController(value = "/resources/fxml/ui/ListView.fxml" , title = "Material Design Example")
public class ListViewController {

	@FXML private C3DListView<?> list1;
	@FXML private C3DListView<?> list2;
	
	@FXML private C3DButton button3D;
	@FXML private C3DButton collapse;
	@FXML private C3DButton expand;
	
	
	private int counter = 0;
	
	@PostConstruct
	public void init() throws FlowException, VetoException {
		button3D.setOnMouseClicked((e)->{
			int val = ++counter%2;
			list1.depthProperty().set(val);
			list2.depthProperty().set(val);
		});
		expand.setOnMouseClicked((e)->list2.expandedProperty().set(true));
		collapse.setOnMouseClicked((e)->list2.expandedProperty().set(false));
	}
	
	
	
}
