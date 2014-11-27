package com.cctintl.c3dfx.controls;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class BlurPane extends StackPane {
	 
    private ImageView imageView;
 
    public BlurPane() {
        imageView = new ImageView();
        imageView.setFocusTraversable(false);
        BoxBlur bb = new BoxBlur();
        bb.setWidth(8);
        bb.setHeight(8);
        bb.setIterations(3);
        imageView.setEffect(bb);
    }
 
    @Override protected void layoutChildren() {
        super.layoutChildren();
        if (getParent() != null && isVisible()) {
            setVisible(false);
            getChildren().remove(imageView);
             
            SnapshotParameters parameters = new SnapshotParameters();
            Point2D startPointInScene = this.localToScene(0, 0);
             
            Rectangle2D toPaint = new Rectangle2D(startPointInScene.getX(), startPointInScene.getY(), getParent().getLayoutBounds().getWidth(), getParent().getLayoutBounds().getHeight());           
            parameters.setViewport(toPaint);
            WritableImage image = new WritableImage((int) toPaint.getWidth(), (int) toPaint.getHeight());
            image = getScene().getRoot().snapshot(parameters, image);
            imageView.setImage(image);
             
            getChildren().add(imageView);
            imageView.toBack();
            setClip(new Rectangle(toPaint.getWidth(), toPaint.getHeight()));
            setVisible(true);
        }
    }
}