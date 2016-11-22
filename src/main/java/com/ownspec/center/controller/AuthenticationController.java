package com.ownspec.center.controller;

import com.ownspec.center.dto.UserDto;
import com.ownspec.center.model.user.User;
import com.ownspec.center.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by nlabrot on 29/09/16.
 */
@RestController
@RequestMapping(value = "/api/auth")
@Slf4j
public class AuthenticationController {

  @Value("${jwt.cookie.name}")
  private String cookieName;

  @Value("${jwt.cookie.maxage}")
  private int cookieMaxAge;

  @Autowired
  private AuthenticationService authenticationService;

  @PostMapping(value = "/login")
  @ResponseBody
  public void login(@RequestBody UserDto source, HttpServletResponse response) {
    LOG.info("Request login with username [{}]", source.getUsername());
    try {
      String token = authenticationService.getLoginToken(source);
      LOG.info("Authentication succeed; built token is [{}]", token);

      Cookie newCookie = new Cookie(cookieName, token);
      newCookie.setMaxAge(cookieMaxAge);
      newCookie.setHttpOnly(true);
      newCookie.setPath("/");

      response.addCookie(newCookie);
      response.setStatus(200);
    } catch (Exception e) {
      response.setStatus(400);
    }
  }

  @PostMapping(value = "/logout")
  public void logout(HttpServletResponse response) {
    User user = authenticationService.getAuthenticatedUser();
    //todo update user's lastConnection
    LOG.info("Request logout for user with username [{}]", user.getUsername());
    Cookie cookie = new Cookie(cookieName, "");
    cookie.setPath("/");
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }


}
