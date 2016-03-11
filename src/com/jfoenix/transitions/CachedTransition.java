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

import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * applies animation on a cahced node to improve the performance
 * 
 * @author  Shadi Shaheen
 * @version 1.0
 * @since   2016-03-09
 */
public class CachedTransition extends Transition {
	protected final Node node;
	protected ObjectProperty<Timeline> timeline = new SimpleObjectProperty<>();
	private CacheHint oldCacheHint = CacheHint.DEFAULT;
	private boolean oldCache = false;

	public CachedTransition(final Node node, final Timeline timeline) {
		this.node = node;
		this.timeline.set(timeline);
		statusProperty().addListener(new ChangeListener<Status>() {
			@Override public void changed(ObservableValue<? extends Status> ov, Status t, Status newStatus) {
				switch(newStatus) {
				case RUNNING:
					starting();
					break;
				default:
					stopping();
					break;
				}
			}
		});
	}
	/**
	 * Called when the animation is starting
	 */
	protected void starting() {
		oldCache = node.isCache();
		oldCacheHint = node.getCacheHint();
		node.setCache(true);
		node.setCacheHint(CacheHint.SPEED);
	}
	/**
	 * Called when the animation is stopping
	 */
	protected void stopping() {
		node.setCache(oldCache);
		node.setCacheHint(oldCacheHint);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override protected void interpolate(double d) {
		timeline.get().playFrom(Duration.seconds(d));
		timeline.get().stop();
	}
}