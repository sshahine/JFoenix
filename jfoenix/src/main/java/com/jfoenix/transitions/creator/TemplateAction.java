package com.jfoenix.transitions.creator;

import java.util.function.Function;

/**
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-10-04
 */
public interface TemplateAction<N> extends TemplateProcess<N> {

  TemplateConfig<N> action(
      Function<JFXAnimationTemplateAction.InitBuilder<N>, JFXAnimationTemplateAction.Builder<?, ?>>
          valueBuilderFunction);

  TemplateConfig<N> action(JFXAnimationTemplateAction.Builder<?, ?> animationValueBuilder);
}
