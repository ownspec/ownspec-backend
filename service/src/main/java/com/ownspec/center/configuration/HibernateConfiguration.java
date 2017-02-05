package com.ownspec.center.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * Created by nlabrot on 27/09/16.
 */
@Configuration
public class HibernateConfiguration extends HibernateJpaAutoConfiguration {

  public HibernateConfiguration(DataSource dataSource, JpaProperties jpaProperties, ObjectProvider<JtaTransactionManager> jtaTransactionManager, ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
    super(dataSource, jpaProperties, jtaTransactionManager, transactionManagerCustomizers);
  }

  @Bean
  @Override
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      EntityManagerFactoryBuilder factoryBuilder) {
    LocalContainerEntityManagerFactoryBean ret = super.entityManagerFactory(factoryBuilder);
    ret.setMappingResources("META-INF/orm.xml");
    return ret;
  }
}

