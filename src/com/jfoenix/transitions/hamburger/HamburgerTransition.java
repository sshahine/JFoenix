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

package com.jfoenix.transitions.hamburger;

import javafx.animation.Transition;

import com.jfoenix.controls.JFXHamburger;

public interface HamburgerTransition {
	public Transition getAnimation(JFXHamburger burger);
}
