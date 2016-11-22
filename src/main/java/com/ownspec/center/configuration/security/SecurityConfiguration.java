package com.ownspec.center.configuration.security;

import static java.util.Arrays.asList;

import com.ownspec.center.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;
import java.util.UUID;
import javax.servlet.Filter;

/**
 * Created by lyrold on 23/08/2016.
 */
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {


  @Value("${jwt.cookie.name}")
  private String cookieName;

  @Autowired
  private UserService userService;

  @Bean
  public FilterChainProxy configure() throws Exception {
    return new FilterChainProxy(asList(
        new DefaultSecurityFilterChain(new AntPathRequestMatcher("/api/auth/**")),
        new DefaultSecurityFilterChain(new AntPathRequestMatcher("/**"), jwtFilter())));
  }

  @Bean
  public AuthenticationProvider authenticationProvider(){
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(encoder());
    provider.setUserDetailsService(userService);
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager(){
    return new ProviderManager(Arrays.asList(authenticationProvider()));
  }


  public Filter jwtFilter() {
    return new JwtFilter(cookieName, secretKey(), userService);
  }

  @Bean
  protected PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }


  @Bean
  public SecretKey secretKey() {
    return new SecretKey(UUID.randomUUID().toString());
  }

}
