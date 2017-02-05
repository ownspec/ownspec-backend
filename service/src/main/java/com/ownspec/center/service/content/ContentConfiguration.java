package com.ownspec.center.service.content;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import com.ownspec.center.service.content.generator.CompositionHtmlContentGenerator;
import com.ownspec.center.service.content.generator.HtmlContentGenerator;
import com.ownspec.center.service.content.generator.HtmlGeneratorParserCallBack;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.nio.file.Path;

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

  @Bean
  @Scope(SCOPE_PROTOTYPE)
  public CompositionHtmlContentGenerator compositionHtmlContentGenerator() {
    return new CompositionHtmlContentGenerator();
  }

  @Bean
  @Scope(SCOPE_PROTOTYPE)
  public HtmlGeneratorParserCallBack htmlGeneratorParserCallBack(boolean forComposition, Path outputDirectory) {
    return new HtmlGeneratorParserCallBack(forComposition, outputDirectory);
  }

}
