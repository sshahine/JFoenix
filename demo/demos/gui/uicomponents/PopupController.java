package demos.gui.uicomponents;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

import com.cctintl.c3dfx.controls.C3DHamburger;
import com.cctintl.c3dfx.controls.C3DPopup;
import com.cctintl.c3dfx.controls.C3DPopup.C3DPopupHPosition;
import com.cctintl.c3dfx.controls.C3DPopup.C3DPopupVPosition;
import com.cctintl.c3dfx.controls.C3DRippler;

@FXMLController(value = "/resources/fxml/ui/Popup.fxml" , title = "Material Design Example")
public class PopupController {
	
	@FXMLViewFlowContext
	private ViewFlowContext context;
	@FXML private StackPane root;
	
	@FXML private C3DRippler rippler1;
	@FXML private C3DRippler rippler2;
	@FXML private C3DRippler rippler3;
	@FXML private C3DRippler rippler4;
	
	@FXML private C3DHamburger burger1;
	@FXML private C3DHamburger burger2;
	@FXML private C3DHamburger burger3;
	@FXML private C3DHamburger burger4;
	
	@FXML private C3DPopup popup;
	
	@PostConstruct
	public void init() throws FlowException, VetoException {
		popup.setPopupContainer(root);
		
		burger1.setOnMouseClicked((e)->{
			popup.setSource(rippler1);
			popup.show(C3DPopupVPosition.TOP, C3DPopupHPosition.LEFT);
		});
		
		burger2.setOnMouseClicked((e)->{
			popup.setSource(rippler2);
			popup.show(C3DPopupVPosition.TOP, C3DPopupHPosition.RIGHT);
		});
		
		burger3.setOnMouseClicked((e)->{
			popup.setSource(rippler3);
			popup.show(C3DPopupVPosition.BOTTOM, C3DPopupHPosition.LEFT);
		});
		
		burger4.setOnMouseClicked((e)->{
			popup.setSource(rippler4);
			popup.show(C3DPopupVPosition.BOTTOM, C3DPopupHPosition.RIGHT);
		});
	}
}
