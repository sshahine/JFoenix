package com.jfoenix.transitions.creator;

/**
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-10-04
 */
public interface TemplateProcess<N> {

  TemplateAction<N> percent(double percent, double... percents);

  TemplateAction<N> from();

  TemplateAction<N> to();
}
