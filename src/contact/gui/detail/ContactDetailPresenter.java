package contact.gui.detail;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.action.ActionMethod;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.flow.container.ContainerAnimations;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javax.annotation.PostConstruct;

import contact.AnimatedFlowContainer;
import contact.service.Contact;

@FXMLController("/resources/fxml/ContactDetail.fxml")
public class ContactDetailPresenter
{
    @FXML private Node root;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;

	@FXMLViewFlowContext
	protected ViewFlowContext context;

	@FXML
	@ActionTrigger("save")
	private Button saveButton;
	
	@FXML
	@ActionTrigger("cancel")
	private Button cancelButton;
	
	
    @PostConstruct
	public void init() throws FlowException{
    	firstNameField.textProperty().setValue(context.getRegisteredObject(Contact.class).getFirstNameProperty().getValue());
    	lastNameField.textProperty().setValue(context.getRegisteredObject(Contact.class).getLastNameProperty().getValue());
    }
    
    @ActionMethod("saveContact")
    public void saveContact() throws VetoException, FlowException{
    	context.getRegisteredObject(Contact.class).getFirstNameProperty().setValue(firstNameField.textProperty().getValue());
    	context.getRegisteredObject(Contact.class).getLastNameProperty().setValue(lastNameField.textProperty().getValue());
    	backAction();
    }
    
    @ActionMethod("backAction")    
    public void backAction(){
    	((AnimatedFlowContainer)context.getRegisteredObject(FlowHandler.class).getContainerProperty().getValue()).changeAnimation(ContainerAnimations.SWIPE_RIGHT);
    }
    
    
}

