package com.jfoenix.transitions.creator;

import javafx.animation.Timeline;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-10-04
 */
public interface TemplateBuilder<N> {

    <B> B build(
        Function<JFXAnimationTemplate<N>, B> builderFunction,
        N animationObject,
        Map<String, ?> animationObjects);

    <B> B build(
        Function<JFXAnimationTemplate<N>, B> builderFunction,
        N animationObject);

      Timeline build(N animationObject, Map<String, ?> animationObjects);

      Timeline build(N animationObject);
  }
