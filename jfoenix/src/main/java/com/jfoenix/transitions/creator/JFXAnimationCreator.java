package com.jfoenix.transitions.creator;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.util.Pair;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-09-18
 */
public class JFXAnimationCreator<N> {

  static final String DEFAULT_CLASS_KEY = "_DefaultKey_";

  private final Map<
          Double,
          List<
              Function<
                  JFXAnimationCreatorValue.GenericBuilderWrapper<N>,
                  JFXAnimationCreatorValue.Builder<?, ?>>>>
      creatorValueBuilderFunctions;
  private final Map<String, Object> animationHelpers;
  private final Function<JFXAnimationCreatorConfig.Builder, JFXAnimationCreatorConfig.Builder>
      creatorConfigBuilderFunction;
  private final Class<N> mainHelperType;

  private JFXAnimationCreator(Builder<N> builder) {
    this.creatorValueBuilderFunctions = builder.creatorValueBuilderFunctions;
    this.animationHelpers = builder.animationHelpers;
    this.creatorConfigBuilderFunction = builder.creatorConfigBuilderFunction;
    mainHelperType = builder.mainHelperType;
  }

  public static <N> AnimationProcess<N> create(Class<N> clazz) {
    return new Builder<>(clazz);
  }

  public static AnimationProcess<Node> create() {
    return create(Node.class);
  }

  public Map<
          Double,
          List<
              Function<
                  JFXAnimationCreatorValue.GenericBuilderWrapper<N>,
                  JFXAnimationCreatorValue.Builder<?, ?>>>>
      getCreatorValueBuilderFunctions() {
    return creatorValueBuilderFunctions;
  }

  public Map<Double, List<JFXAnimationCreatorValue<?, ?>>> getAnimationValues() {

    Map<Double, List<JFXAnimationCreatorValue<?, ?>>> animationValueMap = new HashMap<>();
    getCreatorValueBuilderFunctions()
        .forEach(
            (percent, animationValueBuilderFunctions) -> {
              List<JFXAnimationCreatorValue<?, ?>> animationValues =
                  animationValueBuilderFunctions
                      .stream()
                      .map(
                          builderFunction ->
                              builderFunction
                                  .apply(JFXAnimationCreatorValue.builder(getMainHelperType()))
                                  .build(key -> getAnimationHelpers().get(key)))
                      .collect(Collectors.toList());
              animationValueMap.put(percent, animationValues);
            });
    return animationValueMap;
  }

  public Map<String, Object> getAnimationHelpers() {
    return animationHelpers;
  }

  public Function<JFXAnimationCreatorConfig.Builder, JFXAnimationCreatorConfig.Builder>
      getCreatorConfigBuilderFunction() {
    return creatorConfigBuilderFunction;
  }

  public Class<N> getMainHelperType() {
    return mainHelperType;
  }

  public static final class Builder<N> implements AnimationConfig<N> {

    private final Set<Double> percents = new HashSet<>();
    private final Map<
            Double,
            List<
                Function<
                    JFXAnimationCreatorValue.GenericBuilderWrapper<N>,
                    JFXAnimationCreatorValue.Builder<?, ?>>>>
        creatorValueBuilderFunctions = new HashMap<>();
    private final Map<String, Object> animationHelpers = new HashMap<>();
    private final Class<N> mainHelperType;
    private Function<JFXAnimationCreatorConfig.Builder, JFXAnimationCreatorConfig.Builder>
        creatorConfigBuilderFunction;
    private boolean clearPercents;

    private Builder(Class<N> mainHelperType) {
      this.mainHelperType = mainHelperType;
    }

    @Override
    public AnimationAction<N> percent(double first, double... rest) {
      if (clearPercents) {
        percents.clear();
        clearPercents = false;
      }
      // Clamp value between 0 and 100.
      percents.add(Math.max(0, Math.min(100, first)));
      creatorValueBuilderFunctions.put(first, new ArrayList<>());

      for (double percent : rest) {
        percents.add(Math.max(0, Math.min(100, percent)));
        creatorValueBuilderFunctions.put(percent, new ArrayList<>());
      }
      return this;
    }

    @Override
    public AnimationConfig<N> action(
        Function<
                JFXAnimationCreatorValue.GenericBuilderWrapper<N>,
                JFXAnimationCreatorValue.Builder<?, ?>>
            valueBuilderFunction) {
      for (Double percent : percents) {
        creatorValueBuilderFunctions.get(percent).add(valueBuilderFunction);
      }
      clearPercents = true;
      return this;
    }

    @Override
    public Builder<N> config(
        Function<JFXAnimationCreatorConfig.Builder, JFXAnimationCreatorConfig.Builder>
            configBuilderFunction) {
      creatorConfigBuilderFunction = configBuilderFunction;
      return this;
    }

    @SafeVarargs
    public final <B> B build(
        Function<JFXAnimationCreator<N>, B> builderFunction,
        N animationHelper,
        Pair<String, ?>... animationHelpers) {
      this.animationHelpers.put(DEFAULT_CLASS_KEY, animationHelper);
      for (Pair<String, ?> pair : animationHelpers) {
        this.animationHelpers.put(pair.getKey(), pair.getValue());
      }
      return builderFunction.apply(new JFXAnimationCreator<>(this));
    }

    @SafeVarargs
    public final Timeline build(N animationHelper, Pair<String, ?>... animationHelpers) {
      return build(JFXAnimationBuilder::buildTimeline, animationHelper, animationHelpers);
    }
  }

  public interface AnimationProcess<N> {

    AnimationAction<N> percent(double percent, double... percents);

    default AnimationAction<N> from() {
      return percent(0);
    }

    default AnimationAction<N> to() {
      return percent(100);
    }
  }

  public interface AnimationAction<N> extends AnimationProcess<N> {

    AnimationConfig<N> action(
        Function<
                JFXAnimationCreatorValue.GenericBuilderWrapper<N>,
                JFXAnimationCreatorValue.Builder<?, ?>>
            valueBuilderFunction);

    default AnimationConfig<N> action(
        JFXAnimationCreatorValue.Builder<?, ?> animationValueBuilder) {
      return action(builder -> animationValueBuilder);
    }
  }

  public interface AnimationConfig<N> extends AnimationAction<N> {

    Builder<N> config(
        Function<JFXAnimationCreatorConfig.Builder, JFXAnimationCreatorConfig.Builder>
            configBuilderFunction);

    default Builder<N> config(JFXAnimationCreatorConfig.Builder configBuilder) {
      return config(builder -> configBuilder);
    }
  }
}
