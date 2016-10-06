package com.ownspec.center.configuration;

import com.ownspec.center.model.user.User;
import com.ownspec.center.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by lyrold on 23/08/2016.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


    @Autowired
    private UserDetailsService accountService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
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
                .passwordEncoder(encoder());
    }

    @Bean
    protected PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }


    // TODO: 29/09/16 temporary until we handle authentication
    @Bean
    public User currentUser() {
        return userRepository.findOne(1L);
    }

}
