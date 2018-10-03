package com.jfoenix.transitions.creator;

import java.util.function.Function;

/**
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-10-04
 */
public interface TemplateConfig<N> extends TemplateAction<N> {

  TemplateBuilder<N> config(
      Function<JFXAnimationTemplateConfig.Builder, JFXAnimationTemplateConfig.Builder>
          configBuilderFunction);

  TemplateBuilder<N> config(JFXAnimationTemplateConfig.Builder configBuilder);
}
