package com.ownspec.center;

import com.ownspec.center.configuration.security.SecurityConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityFilterAutoConfiguration;
import org.springframework.context.annotation.Import;


@SpringBootApplication(exclude = {
    ManagementWebSecurityAutoConfiguration.class,
    SecurityAutoConfiguration.class,
    SecurityFilterAutoConfiguration.class,
    FreeMarkerAutoConfiguration.class
})
@Import({SecurityConfiguration.class})
public class OsCenterApplication {

  public static void main(String[] args) {
    SpringApplication.run(OsCenterApplication.class, args);
  }
}
