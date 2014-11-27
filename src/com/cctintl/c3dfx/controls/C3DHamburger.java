package com.cctintl.c3dfx.controls;


import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

public class C3DHamburger extends VBox {
	public C3DHamburger() {
		SVGPath line1 = new SVGPath();
		line1.setContent("m 482.73642,457.05289 24,0 c 1.104,0 2,-0.896 2,-2 0,-1.104 -0.896,-2 -2,-2 l -24,0 c -1.104,0 -2,0.896 -2,2 0,1.104 0.896,2 2,2 z");
		SVGPath line2 = new SVGPath();
		line2.setContent("m 482.73642,457.05289 24,0 c 1.104,0 2,-0.896 2,-2 0,-1.104 -0.896,-2 -2,-2 l -24,0 c -1.104,0 -2,0.896 -2,2 0,1.104 0.896,2 2,2 z");
		SVGPath line3 = new SVGPath();
		line3.setContent("m 482.73642,457.05289 24,0 c 1.104,0 2,-0.896 2,-2 0,-1.104 -0.896,-2 -2,-2 l -24,0 c -1.104,0 -2,0.896 -2,2 0,1.104 0.896,2 2,2 z");
        this.getChildren().add(line1);
        this.getChildren().add(line2);
        this.getChildren().add(line3);
        this.getStyleClass().add("c3d-hamburger-icon");
	}
}
