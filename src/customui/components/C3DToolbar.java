package customui.components;


import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class C3DToolbar extends StackPane {
	
	private BorderPane toolBar = new  BorderPane();
	private BorderPane container = new BorderPane();
	
	private HBox leftBox = new HBox();
	private HBox rightBox = new HBox();

	public C3DToolbar() {
		toolBar.setLeft(leftBox);
		toolBar.setRight(rightBox);
		toolBar.getStyleClass().add("c3d-tool-bar");
		DepthManager.setDepth(toolBar, 1);
		container.setTop(toolBar);
		this.getChildren().add(container);
	}
	
	public void setLeftItems(Node... nodes){
		this.leftBox.getChildren().addAll(nodes);
	}
	
	public ObservableList<Node> getLeftItems(){
		return this.leftBox.getChildren();
	}
	
	public void setRightItems(Node... nodes){
		this.rightBox.getChildren().addAll(nodes);
	}
	
	public ObservableList<Node> getRightItems(){
		return this.rightBox.getChildren();
	}
	
	public void setContent(Node node){
		this.container.setCenter(node);
	}

	public Node getContent(){
		return this.container;
	}

}
