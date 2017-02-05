package com.ownspec.center.service.workflow;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import com.ownspec.center.model.component.Component;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Created by nlabrot on 03/11/16.
 */
@Configuration
public class WorkflowConfiguration {

  @Bean
  @Scope(SCOPE_PROTOTYPE)
  public ChangesExtractor changesExtractor(Component c) {
    return new ChangesExtractor(c);
  }
}
