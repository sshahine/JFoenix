package customui.components;


import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class C3DToolbar extends BorderPane {
	
	private HBox leftBox;
	private HBox rightBox;

	public C3DToolbar() {
		leftBox = new HBox();
		rightBox = new HBox();
		rightBox.getStyleClass().add("hboxRight");		
		this.setLeft(leftBox);
		this.setRight(rightBox);
		this.getStyleClass().add("c3dToolbar");
	}


	public void addLeftNode(Node node){
		this.leftBox.getChildren().add(node);
	}
	
	public void addRightNode(Node node){
		this.rightBox.getChildren().add(node);
	}
	



}
