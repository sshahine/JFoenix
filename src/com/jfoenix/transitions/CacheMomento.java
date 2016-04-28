package com.jfoenix.transitions;

import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.Region;

public class CacheMomento{
	private boolean cache;
	private boolean cacheShape;
	private boolean snapToPixel;
	private CacheHint cacheHint = CacheHint.DEFAULT;
	private Node node;
	
	public CacheMomento(Node node) {
		this.node = node;
	}
	
	public void cache(){
		this.cache = node.isCache();
		this.cacheHint = node.getCacheHint();
		node.setCache(true);
		node.setCacheHint(CacheHint.SPEED);
		if(node instanceof Region){
			this.cacheShape = ((Region)node).isCacheShape();
			this.snapToPixel = ((Region)node).isSnapToPixel();
			((Region)node).setCacheShape(true);
			((Region)node).setSnapToPixel(true);
		}
	}
	
	public void restore(){
		node.setCache(cache);
		node.setCacheHint(cacheHint);
		if(node instanceof Region){
			((Region)node).setCacheShape(cacheShape);
			((Region)node).setSnapToPixel(snapToPixel);
		}
	}
}