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
package com.jfoenix.converters.base;

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