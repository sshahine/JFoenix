package contact.gui.main;


import java.awt.Window;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.action.FlowActionChain;
import io.datafx.controller.flow.action.FlowLink;
import io.datafx.controller.flow.action.FlowMethodAction;
import io.datafx.controller.flow.container.ContainerAnimations;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;







import javax.annotation.PostConstruct;







import com.cctintl.c3dfx.controls.BlurPane;
import com.cctintl.c3dfx.transitions.hamburger.HamburgerBackArrowBasicTransition;
import com.cctintl.c3dfx.transitions.hamburger.HamburgerBasicCloseTransition;
import com.cctintl.c3dfx.transitions.hamburger.HamburgerNextArrowBasicTransition;
import com.cctintl.c3dfx.transitions.hamburger.HamburgerSlideCloseTransition;

import contact.AnimatedFlowContainer;
import contact.WindowManager;
import contact.gui.detail.ContactDetailPresenter;
import contact.gui.search.ContactSearchPresenter;

@FXMLController(value = "/resources/fxml/Main.fxml" , title = "MVP Exmaple & DataFX")
public class MainPresenter
{
	@FXMLViewFlowContext
	private ViewFlowContext context;

	@FXML
	private Parent root;

	@FXML
	private BorderPane contentArea;

	@FXML
	private ProgressBar c3dProgressBar;

	@FXML
	private SVGPath linesSVG;

	@FXML
	private Button linesButton;

	@FXML 
	private Button backButton;

	@FXML 
	private VBox burgerIcon;
	@FXML
	private VBox burgerIcon1;
	@FXML
	private VBox burgerIcon2;
	@FXML
	private VBox burgerIcon3;

	private FlowHandler flowHandler;


	@PostConstruct
	public void init() throws FlowException, VetoException {

		Timeline task = new Timeline(
				new KeyFrame(
						Duration.ZERO,       
						new KeyValue(c3dProgressBar.progressProperty(), 0)
						),
						new KeyFrame(
								Duration.seconds(2), 
								new KeyValue(c3dProgressBar.progressProperty(), 1)
								)
				);
		task.playFromStart();

		//
		//		Timeline task1 = new Timeline(
		//				new KeyFrame(
		//						Duration.ZERO,       
		//						new KeyValue(linesSVG.contentProperty(), "M4,10h24c1.104,0,2-0.896,2-2s-0.896-2-2-2H4C2.896,6,2,6.896,2,8S2.896,10,4,10z M28,14H4c-1.104,0-2,0.896-2,2  s0.896,2,2,2h24c1.104,0,2-0.896,2-2S29.104,14,28,14z M28,22H4c-1.104,0-2,0.896-2,2s0.896,2,2,2h24c1.104,0,2-0.896,2-2  S29.104,22,28,22z", Interpolator.EASE_BOTH),
		//						new KeyValue(linesSVG.strokeProperty(), Color.TRANSPARENT  ),
		//						new KeyValue(linesSVG.strokeDashOffsetProperty(), 23 )
		//						),
		//						new KeyFrame(
		//								Duration.millis(320),
		//								new KeyValue(linesSVG.contentProperty(), "M4,10h24c1.104,0,2-0.896,2-2s-0.896-2-2-2H4C2.896,6,2,6.896,2,8S2.896,10,4,10z M28,14H4c-1.104,0-2,0.896-2,2  s0.896,2,2,2h24c1.104,0,2-0.896,2-2S29.104,14,28,14z M28,22H4c-1.104,0-2,0.896-2,2s0.896,2,2,2h24c1.104,0,2-0.896,2-2  S29.104,22,28,22z", Interpolator.EASE_BOTH),
		//								new KeyValue(linesSVG.strokeProperty(), Color.WHITE  ),
		//								new KeyValue(linesSVG.strokeDashOffsetProperty(), 200 )
		//								)
		//				);
		//
		//		linesButton.addEventHandler(MouseEvent.MOUSE_PRESSED,new EventHandler<MouseEvent>() {
		//			@Override public void handle(MouseEvent e) {
		//				FadeOutLeftTransition fadeout = new FadeOutLeftTransition(linesButton);
		//				FadeInRightTransition fadein = new FadeInRightTransition(backButton);
		//
		//				fadeout.setOnFinished(new EventHandler<ActionEvent>() {
		//					@Override
		//					public void handle(ActionEvent arg0) {
		//						linesButton.setVisible(false);
		//					}
		//				});        		
		//				fadein.setOnFinished(new EventHandler<ActionEvent>() {
		//					@Override
		//					public void handle(ActionEvent arg0) {
		//						backButton.setVisible(true);
		//					}
		//				});
		//				fadeout.play();
		//				fadein.play();
		//			}
		//		});
		//
		//		linesButton.addEventHandler(MouseEvent.MOUSE_EXITED,new EventHandler<MouseEvent>() {
		//			@Override public void handle(MouseEvent e) {
		//				task1.setRate(-1);
		//				task1.playFrom(task1.getCurrentTime());
		//			}
		//		});
		//		linesButton.addEventHandler(MouseEvent.MOUSE_ENTERED,new EventHandler<MouseEvent>() {
		//			@Override public void handle(MouseEvent e) {
		//				task1.setRate(1);
		//				task1.play();
		//			}
		//		});


		//		burgerIcon.getChildren().get(0).getTransforms().add(new Rotate(45, 0, 0));
		//		burgerIcon.getChildren().get(2).getTransforms().add(new Rotate(-45, 0, 10));

		HamburgerSlideCloseTransition burgerTask = new HamburgerSlideCloseTransition(burgerIcon);
		burgerIcon.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
			WindowManager.getPopup();
			burgerTask.setRate(burgerTask.getRate()*-1);
			burgerTask.play();
		});

		
		HamburgerBasicCloseTransition burgerTask1 = new HamburgerBasicCloseTransition(burgerIcon1);
		burgerIcon1.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
			burgerTask1.setRate(burgerTask1.getRate()*-1);
			burgerTask1.play();
			contentArea.getChildren().add(new BlurPane());
		});
		
		HamburgerBackArrowBasicTransition burgerTask2 = new HamburgerBackArrowBasicTransition(burgerIcon2);
		burgerIcon2.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
			burgerTask2.setRate(burgerTask2.getRate()*-1);
			burgerTask2.play();
		});
		
		HamburgerNextArrowBasicTransition burgerTask3 = new HamburgerNextArrowBasicTransition(burgerIcon3);
		burgerIcon3.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
			burgerTask3.setRate(burgerTask3.getRate()*-1);
			burgerTask3.play();
		});

		context = new ViewFlowContext();
		Flow innerFlow = new Flow(ContactSearchPresenter.class);
		innerFlow.withLink(ContactSearchPresenter.class, "next", ContactDetailPresenter.class);
		innerFlow.withAction(ContactDetailPresenter.class, "cancel",  new FlowActionChain(new FlowMethodAction("backAction"), new FlowLink<ContactSearchPresenter>(ContactSearchPresenter.class)));
		innerFlow.withAction(ContactDetailPresenter.class, "save", new FlowActionChain(new FlowMethodAction("saveContact"), new FlowLink<ContactSearchPresenter>(ContactSearchPresenter.class)));

		flowHandler = innerFlow.createHandler(context);
		context.register(flowHandler);
		contentArea.setCenter(flowHandler.start(new AnimatedFlowContainer(Duration.millis(320), ContainerAnimations.SWIPE_LEFT)));
	}



	//	class FadeInRightTransition extends CachedTimelineTransition {
	//		/**
	//		 * Create new FadeInUpTransition
	//		 *
	//		 * @param node The node to affect
	//		 */
	//		public FadeInRightTransition(final Node node) {
	//			super(
	//					node,
	//					new Timeline(					
	//							new KeyFrame(Duration.millis(0),
	//									new KeyValue(node.visibleProperty(), true, WEB_EASE),
	//									new KeyValue(node.opacityProperty(), 0, WEB_EASE),
	//									new KeyValue(node.translateXProperty(), 20, WEB_EASE)
	//									),
	//									new KeyFrame(Duration.millis(1000),
	//											new KeyValue(node.visibleProperty(), true, WEB_EASE),
	//											new KeyValue(node.opacityProperty(), 1, WEB_EASE),
	//											new KeyValue(node.translateXProperty(), 0, WEB_EASE)
	//											)
	//							)
	//					);
	//			setCycleDuration(Duration.seconds(1));
	//			setDelay(Duration.seconds(0.2));
	//		}
	//	}






}

