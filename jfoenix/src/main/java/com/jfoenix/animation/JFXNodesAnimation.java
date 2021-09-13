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

package com.jfoenix.animation;

import javafx.animation.Animation;
import javafx.scene.Node;

public abstract class JFXNodesAnimation<S extends Node, T extends Node> {

    protected S fromNode;
    protected T toNode;

    public JFXNodesAnimation(S fromNode, T toNode) {
        this.fromNode = fromNode;
        this.toNode = toNode;
    }

    public void animate() {
        init();
        Animation exitAnimation = animateExit();
        Animation sharedAnimation = animateSharedNodes();
        Animation entranceAnimation = animateEntrance();
        exitAnimation.setOnFinished(finish -> sharedAnimation.play());
        sharedAnimation.setOnFinished(finish -> entranceAnimation.play());
        entranceAnimation.setOnFinished(finish -> end());
        exitAnimation.play();
    }

    public abstract Animation animateExit();

    public abstract Animation animateSharedNodes();

    public abstract Animation animateEntrance();

    public abstract void init();

    public abstract void end();

}
