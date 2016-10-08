package com.ownspec.center.configuration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.nlab.smtp.pool.SmtpConnectionPool;
import org.nlab.smtp.transport.factory.SmtpConnectionFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by lyrold on 08/10/2016.
 */
@Configuration
public class EmailConfiguration {

    @Value("${email.host}")
    private String emailHost;

    @Value("${email.port}")
    private int emailPort;

    @Value("${email.protocol}")
    private String emailProtocol;

    @Bean
    public SmtpConnectionPool smtpConnectionPool() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(1);

        return new SmtpConnectionPool(SmtpConnectionFactoryBuilder.newSmtpBuilder()
                .host(emailHost)
                .port(emailPort)
                .protocol(emailProtocol)
                .build());
    }
}
