package com.ownspec.center.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Created by lyrold on 23/08/2016.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


    @Autowired
    private UserDetailsService accountService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().permitAll();
//        http
//                .exceptionHandling().and()
//                .anonymous().and()
//                .servletApi().and()
//                .headers()
//                .cacheControl()
//        ;
//
//        http
//                .formLogin().loginPage("/login").permitAll().and()
//                .logout().logoutUrl("/logout").logoutSuccessUrl("/login")
//        ;
//
//        http.authorizeRequests()
//                .antMatchers("/api/users*").hasRole("ADMIN")
//                .antMatchers("/api/*").hasRole("USER")
//                .anyRequest().fullyAuthenticated()
//        ;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(accountService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }


}
