package demos.gui.uicomponents;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import javax.annotation.PostConstruct;

import com.cctintl.c3dfx.controls.C3DButton;
import com.cctintl.c3dfx.controls.C3DListView;

@FXMLController(value = "/resources/fxml/ui/ListView.fxml" , title = "Material Design Example")
public class ListViewController {

	
	@FXMLViewFlowContext
	private ViewFlowContext context;

	@FXML private C3DListView<?> list1;
	@FXML private C3DListView<?> list2;
	@FXML private C3DListView<?> subList;
	
	@FXML private C3DButton button3D;
	@FXML private C3DButton collapse;
	@FXML private C3DButton expand;
	
	private int counter = 0;
	
	@PostConstruct
	public void init() throws FlowException, VetoException {
		
		if(((Pane) context.getRegisteredObject("ContentPane")).getChildren().size() > 0)
			Platform.runLater(()-> ((Pane)((Pane) context.getRegisteredObject("ContentPane")).getChildren().get(0)).getChildren().remove(1));
		
		button3D.setOnMouseClicked((e)->{
			int val = ++counter%2;
			list1.depthProperty().set(val);
			list2.depthProperty().set(val);
		});
		
		expand.setOnMouseClicked((e)->list2.expandedProperty().set(true));
		collapse.setOnMouseClicked((e)->list2.expandedProperty().set(false));
		list1.depthProperty().set(1);
	}
	
	
	
}
