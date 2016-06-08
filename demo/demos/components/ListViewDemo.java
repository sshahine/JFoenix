package demos.components;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.scenicview.ScenicView;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;

public class ListViewDemo extends Application {

	int counter = 0 ;
	
	@Override
	public void start(Stage stage) throws Exception {

		
		JFXListView<Label> list = new JFXListView<Label>();		
		for(int i = 0 ; i < 4 ; i++) list.getItems().add(new Label("Item " + i));
		list.getStyleClass().add("mylistview");
		
		list.setCellFactory(new Callback<ListView<Label>, ListCell<Label>>() {
			
			@Override
			public ListCell<Label> call(ListView<Label> param) {
				 final JFXListCell<Label> cell = new JFXListCell<Label>() {
			          @Override
			          public void updateItem(Label item, boolean empty) {
			            super.updateItem(item, empty);
			            if (item != null && !empty) {
			              setText("SDFSDF");
			            } else {
			              setText(null);
			            }
			          }
			        };
			        return cell;
			}
		});
		
		ListView<JFXButton> javaList = new ListView<JFXButton>();
		for(int i = 0 ; i < 4 ; i++) javaList.getItems().add(new JFXButton("Item " + i));
						
		FlowPane pane = new FlowPane();
		pane.setStyle("-fx-background-color:WHITE");
		
		JFXButton button3D = new JFXButton("3D");
		button3D.setOnMouseClicked((e)-> list.depthProperty().set(++counter%2));
		
		JFXButton buttonExpand = new JFXButton("EXPAND");
		buttonExpand.setOnMouseClicked((e)-> {list.depthProperty().set(1);list.setExpanded(true);});
		
		JFXButton buttonCollapse = new JFXButton("COLLAPSE");
		buttonCollapse.setOnMouseClicked((e)-> {list.depthProperty().set(1);list.setExpanded(false);});
		
		pane.getChildren().add(button3D);
		pane.getChildren().add(buttonExpand);
		pane.getChildren().add(buttonCollapse);
		
		AnchorPane listsPane = new AnchorPane();
		listsPane.getChildren().add(list);
		AnchorPane.setLeftAnchor(list, 20.0);
		listsPane.getChildren().add(javaList);
		AnchorPane.setLeftAnchor(javaList, 300.0);
		
		VBox box = new VBox();
		box.getChildren().add(pane);
		box.getChildren().add(listsPane);
		box.setSpacing(40);
		
		StackPane main = new StackPane();
		main.getChildren().add(box);
		main.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		StackPane.setMargin(pane, new Insets(20,0,0,20));

		final Scene scene = new Scene(main, 600, 600, Color.WHITE);
		stage.setTitle("JFX ListView Demo ");
		scene.getStylesheets().add(ListViewDemo.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
		
		ScenicView.show(scene);
		
	}
	
	public static void main(String[] args) { launch(args); }

}
