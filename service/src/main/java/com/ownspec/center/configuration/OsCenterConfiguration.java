package com.ownspec.center.configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.ownspec.center.service.AuthenticationService;
import org.apache.commons.lang.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ResourceBundle;


/**
 * Created by lyrold on 23/08/2016.
 */
@Configuration
@EnableTransactionManagement
@EnableAsync
@EnableJpaAuditing(auditorAwareRef = "")
@EnableJpaRepositories(basePackages = "com.ownspec.center")
@EntityScan(
    basePackages = {"com.ownspec.center.model"},
    basePackageClasses = {Jsr310JpaConverters.class}
)
public class OsCenterConfiguration {

  @Value("${git.repository.path.components}")
  private String componentsGitRepositoryPath;

  @Autowired
  private AuthenticationService authenticationService;


  @Bean
  public AuditorAware auditorAware() {
    return () -> authenticationService.getAuthenticatedUser();
  }


  public ResourceBundle translation() {
    return ResourceBundle.getBundle(
        "translation",
        LocaleUtils.toLocale(authenticationService.getAuthenticatedUser().getPreference().getLanguage())
    );
  }

  @Bean
  public Module collectionModule() {
    return new GuavaModule();
    //.registerModule(new HppcModule())
    //.registerModule(new PCollectionsModule())
  }
}
