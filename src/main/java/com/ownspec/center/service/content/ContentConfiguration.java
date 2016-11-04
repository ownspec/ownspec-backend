package com.ownspec.center.service.content;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Created by nlabrot on 03/11/16.
 */
@Configuration
public class ContentConfiguration {

  @Bean
  @Scope(SCOPE_PROTOTYPE)
  public HtmlContentSaver htmlContentSaver() {
    return new HtmlContentSaver();
  }

  @Bean
  @Scope(SCOPE_PROTOTYPE)
  public HtmlContentGenerator htmlContentGenerator() {
    return new HtmlContentGenerator();
  }

}
