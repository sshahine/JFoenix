/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.jfoenix.transitions.template;

import javafx.animation.Timeline;

import java.util.*;
import java.util.function.Function;

/**
 * Class which provides the specific builder methods for the whole animation.
 *
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-10-04
 */
public interface JFXTemplateBuilder<N> {

  /**
   * Method which handles a specific builder{@link Function} which provides the {@link
   * JFXAnimationTemplate} and should return a specific representation of the animation e.g. a
   * {@link Timeline}. <br>
   * Furthermore the method provides a {@link Function} which provides a {@link
   * JFXAnimationObjectMapBuilder} for defining the default animation objects and/or named animation
   * objects.
   *
   * @param builderFunction the builder{@link Function} for a specific representation of the
   *     animation.
   * @param mapBuilderFunction the builder{@link Function} for defining the default animation
   *     objects and/or named animation objects.
   * @param <B> the specific animation representation type.
   * @return the specific animation representation.
   */
  <B> B build(
      Function<JFXAnimationTemplate<N>, B> builderFunction,
      Function<JFXAnimationObjectMapBuilder<N>, JFXAnimationObjectMapBuilder<N>>
          mapBuilderFunction);

  /**
   * Same method as {@link #build(Function, Function)} but takes directly one default animation
   * object.
   *
   * @see #build(Function, Function)
   * @param builderFunction the builder{@link Function} for a specific representation of the
   *     animation.
   * @param defaultAnimationObject the default animation object.
   * @param <B> the specific animation representation type.
   * @return the specific animation representation.
   */
  <B> B build(Function<JFXAnimationTemplate<N>, B> builderFunction, N defaultAnimationObject);

  /**
   * Same method as {@link #build(Function, Function)} but returns directly a {@link Timeline}
   * instance as specific animation representation.
   *
   * @see #build(Function, Function)
   * @param mapBuilderFunction the builder{@link Function} for defining the default animation
   *     objects and/or named animation objects.
   * @return the animation as {@link Timeline} instance.
   */
  Timeline build(
      Function<JFXAnimationObjectMapBuilder<N>, JFXAnimationObjectMapBuilder<N>>
          mapBuilderFunction);

  /**
   * Same method as {@link #build(Function, Object)} but returns directly a {@link Timeline}
   * instance as specific animation representation and takes a default animation object.
   *
   * @see #build(Function, Object)
   * @param defaultAnimationObject the default animation object.
   * @return the animation as {@link Timeline} instance.
   */
  Timeline build(N defaultAnimationObject);

  /**
   * Same method as {@link #build(Object)} )} but takes no default animation object.<br>
   * If you don't need a default animation object choose this method.<br>
   * In any case the default animation object will be {@code null}.
   *
   * @return the animation as {@link Timeline} instance.
   */
  Timeline build();

  class JFXAnimationObjectMapBuilder<N> {

    public static final String DEFAULT_ANIMATION_OBJECT_NAME = "_DefaultName_";
    private final Map<String, Collection<Object>> animationObjects = new HashMap<>();

    private JFXAnimationObjectMapBuilder() {}

    public static <N> JFXAnimationObjectMapBuilder<N> builder() {
      return new JFXAnimationObjectMapBuilder<>();
    }

    /**
     * Method for the default animation objects of the animation.<br>
     * The default type of the objects is a {@link javafx.scene.Node}, which is created when the
     * {@link JFXAnimationTemplate} is created:
     *
     * <pre>{@code
     * JFXAnimationTemplate.create()
     * }</pre>
     *
     * To set another type use the {@link JFXAnimationTemplate#create(Class)} method:
     *
     * <pre>{@code
     * JFXAnimationTemplate.create(MyType.class)
     * }</pre>
     *
     * The default objects can be used in every action(...) method:
     *
     * <pre>{@code
     * .action(b -> b.target(defaultNode -> defaultNode.scaleXProperty(), Node::scaleYProperty).endValue(1))
     * }</pre>
     *
     * If this method is called multiple times, all animation objects will be added to the {@link
     * #animationObjects}.
     *
     * @param animationObject the first animation object.
     * @param animationObjects rest of animation objects.
     * @return the {@link JFXAnimationObjectMapBuilder} instance.
     */
    @SafeVarargs
    public final JFXAnimationObjectMapBuilder<N> defaultObject(
        N animationObject, N... animationObjects) {
      Collection<N> collection = new ArrayList<>();
      collection.add(animationObject);
      collection.addAll(Arrays.asList(animationObjects));
      return defaultObjectsOf(collection);
    }

    /**
     * Same method as {@link #defaultObject(Object, Object[])} but with a {@link Collection} of
     * animation objects.
     *
     * @see #defaultObject(Object, Object[])
     * @param animationObjects the animation objects.
     * @return the {@link JFXAnimationObjectMapBuilder} instance.
     */
    public JFXAnimationObjectMapBuilder<N> defaultObjectsOf(Collection<N> animationObjects) {
      this.animationObjects
          .computeIfAbsent(DEFAULT_ANIMATION_OBJECT_NAME, key -> new ArrayList<>())
          .addAll(animationObjects);
      return this;
    }

    /**
     * Method for the named animation objects of the animation.<br>
     * The named animation objects are similar to the default objects {@link #defaultObject(Object,
     * Object[])}.<br>
     * The difference is that a named object needs an unique name as key and that they have to be
     * called specifically in every action(...) method.<br>
     * Furthermore the specific type of the animation object must be provided in every action(...)
     * method where the object is used.
     *
     * <p>Define a named animation object like (in this case e.g. also with a default animation
     * object):
     *
     * <pre>{@code
     * .build(b -> b.defaultObject(button).namedObject("label", label)))
     * }</pre>
     *
     * In our {@link JFXAnimationTemplate} we have e.g. two action(...) methods where the first uses
     * the default animation object.<br>
     * The second uses the named animation object:
     *
     * <pre>{@code
     * .action(b -> b.target(defaultObject -> defaultObject.translateYProperty()).endValue(0))
     * .action(b -> b.withAnimationObject(Label.class, "label").target(label -> label.translateYProperty()).endValue(5))
     * }</pre>
     *
     * If this method is called multiple times with the same name, all animation objects will be
     * added to the {@link #animationObjects}.
     *
     * @param name the unique name of the named animation objects.
     * @param animationObject the first animation object.
     * @param animationObjects rest of animation objects.
     * @return the {@link JFXAnimationObjectMapBuilder} instance.
     */
    public JFXAnimationObjectMapBuilder<N> namedObject(
        String name, Object animationObject, Object... animationObjects) {
      Collection<Object> collection = new ArrayList<>();
      collection.add(animationObject);
      collection.addAll(Arrays.asList(animationObjects));
      return namedObjectsOf(name, collection);
    }

    /**
     * Same method as {@link #namedObject(String, Object, Object...)} but with a {@link Collection}
     * of animation objects.
     *
     * @see #namedObject(String, Object, Object...)
     * @param name the unique name of the named animation objects.
     * @param animationObjects the animation objects.
     * @return the {@link JFXAnimationObjectMapBuilder} instance.
     */
    public JFXAnimationObjectMapBuilder<N> namedObjectsOf(
        String name, Collection<Object> animationObjects) {
      this.animationObjects
          .computeIfAbsent(name, key -> new ArrayList<>())
          .addAll(animationObjects);
      return this;
    }

    /**
     * Same method as {@link #namedObjectsOf(String, Collection)} but with a {@link Map} of
     * animation objects.
     *
     * @see #namedObjectsOf(String, Collection)
     * @param animationObjects the animation objects.
     * @return the {@link JFXAnimationObjectMapBuilder} instance.
     */
    public JFXAnimationObjectMapBuilder<N> namedObjectsOf(
        Map<String, Collection<Object>> animationObjects) {
      this.animationObjects.putAll(animationObjects);
      return this;
    }

    /**
     * Get the {@link #animationObjects}.
     *
     * @return the {@link #animationObjects}.
     */
    public Map<String, Collection<Object>> getAnimationObjects() {
      return animationObjects;
    }
  }
}
