/*
 * Copyright (c) 2016 JFoenix
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.jfoenix.converters.base;

import javafx.scene.Node;

/**
 * Converter defines conversion behavior between Nodes and Objects.
 * The type of Objects are defined by the subclasses of Converter.
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public abstract class NodeConverter<T> {
    /**
     * Converts the object provided into its node form.
     * Styling of the returned node is defined by the specific converter.
     *
     * @return a node representation of the object passed in.
     */
    public abstract Node toNode(T object);

    /**
     * Converts the node provided into an object defined by the specific converter.
     * Format of the node and type of the resulting object is defined by the specific converter.
     *
     * @return an object representation of the node passed in.
     */
    public abstract T fromNode(Node node);

    /**
     * Converts the object provided into a String defined by the specific converter.
     * Format of the String is defined by the specific converter.
     *
     * @return a String representation of the node passed in.
     */
    public abstract String toString(T object);

}
