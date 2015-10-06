/*
 * JFoenix
 * Copyright (c) 2015, JFoenix and/or its affiliates., All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package demos.components;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class TreeViewDemo extends Application {


	final TreeItem<User> root = new TreeItem<>(new User("Sales Department", "23"));

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {

			root.getChildren().add(new TreeItem<>(new User("Sales Department1", "23")));
			TreeItem<User> root1 = new TreeItem<>(new User("Sales Department2", "24"));
			root.getChildren().add(root1);
			//			root1.getChildren().add(new TreeItem<>(new User("Sales Department3", "25")));


			root.getChildren().add(new TreeItem<>(new User("Sales Department4", "22")));
			TreeItem<User> root2 = new TreeItem<>(new User("Sales Department5", "20"));
			root.getChildren().add(root2);
			//			root2.getChildren().add(new TreeItem<>(new User("Sales Department6", "21")));



			TreeView<User> treeView = new TreeView<User>(root);
			root.setExpanded(true);


			StackPane main = new StackPane();

			main.setPadding(new Insets(10));
			main.getChildren().add(treeView	);
			Scene scene = new Scene(main, 600, 400);
			scene.getStylesheets().add(TreeViewDemo.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

			//			ScenicView.show(scene);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	class User {
		String userName;
		String age;
		public User(String userName, String age) {
			this.userName = userName;
			this.age = age;
		}

		public String toString(){
			return userName + " : " + age;
		}

	}
}