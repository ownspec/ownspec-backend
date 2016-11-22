package com.ownspec.center.service;

import com.ownspec.center.configuration.security.SecretKey;
import com.ownspec.center.dto.UserDto;
import com.ownspec.center.model.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Created by lyrold on 23/08/2016.
 */
@Service
@Slf4j
public class AuthenticationService {
  @Value("${jwt.cookie.name}")
  private String cookieName;

  @Autowired
  private SecretKey secretKey;

  @Autowired
  private AuthenticationManager authenticationManager;

  public String getLoginToken(UserDto user) {
    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
    Authentication authentication = authenticationManager.authenticate(token);
    User target = (User) authentication.getPrincipal();
    return Jwts.builder()
        .setSubject(user.getUsername())
        .claim("company", target.getCompany())
        .claim("role", target.getRole())
        .signWith(SignatureAlgorithm.HS512, secretKey.getValue())
        .compact();
  }


  public User getAuthenticatedUser() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
