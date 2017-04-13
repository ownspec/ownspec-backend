package com.ownspec.center;

import com.dumbster.smtp.ServerOptions;
import com.dumbster.smtp.SmtpServer;
import com.dumbster.smtp.SmtpServerFactory;
import com.ownspec.emailstore.PersistentMailStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

/**
 * Created by nlabrot on 13/04/17.
 */
@Configuration
public class TestConfiguration {


  @Value("${email.port}")
  private int emailPort;

  @Bean
  public PersistentMailStore persistentMailStore() {
    return new PersistentMailStore();
  }

  @Bean
  public SmtpServer smtpServer() {
    ServerOptions serverOptions = new ServerOptions();
    serverOptions.port = emailPort;
    serverOptions.mailStore = persistentMailStore();
    return SmtpServerFactory.startServer(serverOptions);
  }

  @PreDestroy
  public void stopServer() {
    smtpServer().stop();
  }
}
