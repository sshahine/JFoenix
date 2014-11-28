package com.cctintl.c3dfx.transitions.hamburger;

import javafx.animation.Transition;

import com.cctintl.c3dfx.controls.C3DHamburger;

public interface HamburgerTransition {
	public Transition getAnimation(C3DHamburger burger);
}
