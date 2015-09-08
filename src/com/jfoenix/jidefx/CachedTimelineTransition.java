package com.jfoenix.jidefx;

import javafx.animation.Interpolator;
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
 * A Transition that uses a Timeline internally and turns SPEED caching on for
 * the animated node during the animation.
 *
 * @author Jasper Potts
 * 
 * updated By ssshahine
 * 
 */
public class CachedTimelineTransition extends Transition {
	protected static final Interpolator WEB_EASE = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);
	protected final Node node;
	
	protected ObjectProperty<Timeline> timeline = new SimpleObjectProperty<>();
	private boolean oldCache = false;
	private CacheHint oldCacheHint = CacheHint.DEFAULT;
	private final boolean useCache;
	/**
	 * Create new CachedTimelineTransition
	 *
	 * @param node The node that is being animated by the timeline
	 * @param timeline The timeline for the animation, it should be from 0 to 1 seconds
	 */
	public CachedTimelineTransition(final Node node, final Timeline timeline) {
		this(node, timeline, true);
	}
	/**
	 * Create new CachedTimelineTransition
	 *
	 * @param node The node that is being animated by the timeline
	 * @param timeline The timeline for the animation, it should be from 0 to 1 seconds
	 * @param useCache When true the node is cached as image during the animation
	 */
	public CachedTimelineTransition(final Node node, final Timeline timeline, final boolean useCache) {
		this.node = node;
		this.timeline.set(timeline);
		this.useCache = useCache;
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
		if (useCache) {
			oldCache = node.isCache();
			oldCacheHint = node.getCacheHint();
			node.setCache(true);
			node.setCacheHint(CacheHint.SPEED);
		}
	}
	/**
	 * Called when the animation is stopping
	 */
	protected void stopping() {
		if (useCache) {
			node.setCache(oldCache);
			node.setCacheHint(oldCacheHint);
		}
	}
	@Override protected void interpolate(double d) {
		timeline.get().playFrom(Duration.seconds(d));
		timeline.get().stop();
	}
}