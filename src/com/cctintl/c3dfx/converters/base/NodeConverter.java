/*
 * Copyright (c) 2015, CCT and/or its affiliates. All rights reserved.
 * CCT PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.cctintl.c3dfx.converters.base;

import javafx.scene.Node;

/**
 * Converter defines conversion behavior between Nodes and Objects.
 * The type of Objects are defined by the subclasses of Converter.
 * @author sshahine 
 */
public abstract class NodeConverter<T> {
    /**
    * Converts the object provided into its node form.
    * Styling of the returned node is defined by the specific converter.
    * @return a node representation of the object passed in.
    */
    public abstract Node toNode(T object);

    /**
    * Converts the node provided into an object defined by the specific converter.
    * Format of the node and type of the resulting object is defined by the specific converter.
    * @return an object representation of the node passed in.
    */
    public abstract T fromNode(Node node);
    
    /**
    * Converts the object provided into a String defined by the specific converter.
    * Format of the String is defined by the specific converter.
    * @return a String representation of the node passed in.
    */
    public abstract String toString(T object);
    
}