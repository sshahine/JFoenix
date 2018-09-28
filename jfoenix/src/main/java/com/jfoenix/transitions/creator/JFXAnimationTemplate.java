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
public class JFXAnimationTemplate<N> {

  static final String DEFAULT_ANIMATION_OBJECT_KEY = "_DefaultKey_";

  private final Builder<N> builder;

  private JFXAnimationTemplate(Builder<N> builder) {
    this.builder = builder;
  }

  public static <N> TemplateProcess<N> create(Class<N> animationObjectType) {
    return new Builder<>(animationObjectType);
  }

  public static TemplateProcess<Node> create() {
    return create(Node.class);
  }

  public Map<Double, List<JFXAnimationTemplateAction<?, ?>>> buildAndGetAnimationValues() {

    Map<Double, List<JFXAnimationTemplateAction<?, ?>>> animationValueMap = new HashMap<>();
    builder.creatorValueBuilderFunctions.forEach(
        (percent, animationValueBuilderFunctions) -> {
          List<JFXAnimationTemplateAction<?, ?>> animationValues =
              animationValueBuilderFunctions
                  .stream()
                  .map(
                      builderFunction ->
                          builderFunction
                              .apply(
                                  JFXAnimationTemplateAction.builder(
                                      builder.animationObjectType, DEFAULT_ANIMATION_OBJECT_KEY))
                              .build(builder.animationObjects::get))
                  .collect(Collectors.toList());
          animationValueMap.put(percent, animationValues);
        });
    return animationValueMap;
  }

  public JFXAnimationTemplateConfig buildAndGetTemplateConfig() {
    return builder.creatorConfigBuilderFunction.apply(JFXAnimationTemplateConfig.builder()).build();
  }

  public static final class Builder<N> implements TemplateConfig<N> {

    private final Set<Double> percents = new HashSet<>();
    private final Map<
            Double,
            List<
                Function<
                    JFXAnimationTemplateAction.InitBuilder<N>,
                    JFXAnimationTemplateAction.Builder<?, ?>>>>
        creatorValueBuilderFunctions = new HashMap<>();
    final Map<String, Object> animationObjects = new HashMap<>();
    private final Class<N> animationObjectType;
    private Function<JFXAnimationTemplateConfig.Builder, JFXAnimationTemplateConfig.Builder>
        creatorConfigBuilderFunction;
    private boolean clearPercents;

    private Builder(Class<N> animationObjectType) {
      this.animationObjectType = animationObjectType;
    }

    @Override
    public TemplateAction<N> percent(double first, double... rest) {
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
    public TemplateConfig<N> action(
        Function<
                JFXAnimationTemplateAction.InitBuilder<N>, JFXAnimationTemplateAction.Builder<?, ?>>
            valueBuilderFunction) {
      for (Double percent : percents) {
        creatorValueBuilderFunctions.get(percent).add(valueBuilderFunction);
      }
      clearPercents = true;
      return this;
    }

    @Override
    public Builder<N> config(
        Function<JFXAnimationTemplateConfig.Builder, JFXAnimationTemplateConfig.Builder>
            configBuilderFunction) {
      creatorConfigBuilderFunction = configBuilderFunction;
      return this;
    }

    @SafeVarargs
    public final <B> B build(
        Function<JFXAnimationTemplate<N>, B> builderFunction,
        N animationObject,
        Pair<String, ?>... animationObjects) {
      this.animationObjects.put(DEFAULT_ANIMATION_OBJECT_KEY, animationObject);
      for (Pair<String, ?> pair : animationObjects) {
        this.animationObjects.put(pair.getKey(), pair.getValue());
      }
      return builderFunction.apply(new JFXAnimationTemplate<>(this));
    }

    @SafeVarargs
    public final Timeline build(N animationObject, Pair<String, ?>... animationObjects) {
      return build(JFXAnimationTemplates::buildTimeline, animationObject, animationObjects);
    }
  }

  public interface TemplateProcess<N> {

    TemplateAction<N> percent(double percent, double... percents);

    default TemplateAction<N> from() {
      return percent(0);
    }

    default TemplateAction<N> to() {
      return percent(100);
    }
  }

  public interface TemplateAction<N> extends TemplateProcess<N> {

    TemplateConfig<N> action(
        Function<
                JFXAnimationTemplateAction.InitBuilder<N>, JFXAnimationTemplateAction.Builder<?, ?>>
            valueBuilderFunction);

    default TemplateConfig<N> action(
        JFXAnimationTemplateAction.Builder<?, ?> animationValueBuilder) {
      return action(builder -> animationValueBuilder);
    }
  }

  public interface TemplateConfig<N> extends TemplateAction<N> {

    Builder<N> config(
        Function<JFXAnimationTemplateConfig.Builder, JFXAnimationTemplateConfig.Builder>
            configBuilderFunction);

    default Builder<N> config(JFXAnimationTemplateConfig.Builder configBuilder) {
      return config(builder -> configBuilder);
    }
  }
}
