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

package com.cctintl.jfx.transitions.hamburger;

import javafx.animation.Transition;

import com.cctintl.jfx.controls.JFXHamburger;

public interface HamburgerTransition {
	public Transition getAnimation(JFXHamburger burger);
}
