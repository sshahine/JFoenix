/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.jfoenix.transitions;

import javafx.animation.Animation;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

/**
 * applies animation on a cahced node to improve the performance
 * 
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2016-03-09
 */
public class CachedAnimation {
	protected ObjectProperty<Animation> animation = new SimpleObjectProperty<>();
	private CacheMomento[] momentos = null;

	public CachedAnimation(final Animation animation, Node...cachedNodes) {
		if(cachedNodes != null){
			momentos = new CacheMomento[cachedNodes.length];	
			for (int i = 0; i < cachedNodes.length; i++) {
				momentos[i] = new CacheMomento(cachedNodes[i]);
			}
		}
		this.animation.set(animation);
		this.animation.get().statusProperty().addListener((o,oldStatus,newStatus)->{
			switch(newStatus) {
			case RUNNING:
				starting();
				break;
			default:
				stopping();
				break;
			}
		});
	}

	/**
	 * Called when the animation is starting
	 */
	protected void starting() {
		if(momentos!=null){
			for (int i = 0; i < momentos.length; i++) {
				momentos[i].cache();
			}
		}
	}
	/**
	 * Called when the animation is stopping
	 */
	protected void stopping() {
		if(momentos!=null){
			for (int i = 0; i < momentos.length; i++) {
				momentos[i].restore();
			}
		}
	}
	/**
	 * @return the animation object
	 */
	public Animation getAnimation() {
		return animation.get();
	}
	
}