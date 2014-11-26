package contact.gui.search;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.action.ActionMethod;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.flow.container.ContainerAnimations;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;

import java.awt.Dimension;
import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.embed.swing.SwingNode;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.media.opengl.awt.GLJPanel;

import com.fxexperience.javafx.animation.FadeInRightTransition;
import com.jogamp.opengl.util.FPSAnimator;

import contact.AnimatedFlowContainer;
import contact.JOGL2Setup_GLCanvas;
import contact.service.Contact;
import contact.service.ContactService;
import contact.service.SimpleContactService;
import customui.components.C3DHamburger;
import customui.transitions.hamburger.HamburgerSlideCloseTransition;

@FXMLController("/resources/fxml2/ContactSearch.fxml")
public class ContactSearchPresenter 
{
	@FXMLViewFlowContext
	protected ViewFlowContext context;

	@FXML 
	private VBox root1;

	@FXML
	private TextField searchField;

	@FXML
	@ActionTrigger("search")
	private Button searchButton;

	@FXML 
	private ListView<Contact> resultsList;

	@Inject
	private ContactService contactService;

	@FXML
	private SwingNode swingnode;
	

	private ListProperty<Contact> contacts;

	@PostConstruct
	public void init() throws FlowException {		
		new FadeInRightTransition(searchButton).play();

		 GLJPanel canvas = new JOGL2Setup_GLCanvas();
         canvas.setPreferredSize(new Dimension(400, 400));

         // Create a animator that drives canvas' display() at the specified FPS.
         final FPSAnimator animator = new FPSAnimator(canvas, 60, true);

         animator.start();
	 
        swingnode.setContent(canvas);
        
		
        
		
		
		
		if(context.getRegisteredObject(SimpleContactService.class) == null){
			contactService = new SimpleContactService();
			context.register(contactService);
		}else{
			contactService = context.getRegisteredObject(SimpleContactService.class);			
		}

		contacts = ((SimpleContactService)contactService).getContacts();

		resultsList.itemsProperty().bindBidirectional(contacts);
		((SimpleContactService)contactService).selectedContactIndexProperty().bind(resultsList.getSelectionModel().selectedIndexProperty());

		resultsList.setCellFactory(new Callback<ListView<Contact>, ListCell<Contact>>(){
			public ListCell<Contact> call(ListView<Contact> contactListView){
				final ListCell<Contact> cell = new ListCell<Contact>(){
					protected void updateItem(Contact contact, boolean empty)
					{
						super.updateItem(contact, empty);
						String text = "";
						if(contact!=null)
							text = String.format("%s %s", contact.getFirstName(), contact.getLastName());
						setText(empty ? null : text);
						setGraphic(null);
					}
				};
				cell.setOnMouseClicked(new EventHandler<Event>(){
					public void handle(Event event)
					{
						Contact contact = cell.getItem();
						if (contact != null)
						{
							context.register(contact);
							next();
						}
					}
				});
				return cell;
			}
		});
	}



	@ActionMethod("search")
	public void onSearch()  
	{

		String searchPhrase = searchField.getText();
		final String[] keywords = searchPhrase != null ? searchPhrase.split("\\s+") : null;
		final Task<List<Contact>> searchTask = new Task<List<Contact>>()
				{
			protected List<Contact> call() throws Exception
			{
				return contactService.searchContacts(keywords);
			}
				};

				searchTask.stateProperty().addListener(new ChangeListener<Worker.State>(){
					public void changed(ObservableValue<? extends Worker.State> source, Worker.State oldState, Worker.State newState){
						if (newState.equals(Worker.State.SUCCEEDED)){
							contacts.addAll(searchTask.getValue());
						}
					}
				});

				contacts.clear();
				new Thread(searchTask).start();
	}

	public void next(){
		try {
			((AnimatedFlowContainer)context.getRegisteredObject(FlowHandler.class).getContainerProperty().getValue()).changeAnimation(ContainerAnimations.SWIPE_LEFT);
			context.getRegisteredObject(FlowHandler.class).handle("next");
		} catch (VetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FlowException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

