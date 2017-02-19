package com.ownspec.center.controller;

import com.ownspec.center.dto.user.UserDto;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.user.VerificationToken;
import com.ownspec.center.service.AuthenticationService;
import com.ownspec.center.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
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

  @Autowired
  private UserService userService;
  @Autowired
  private PasswordEncoder encoder;

  @PostMapping("/login")
  @ResponseBody
  public void login(@RequestBody UserDto source, HttpServletResponse response) {
    LOG.info("Request login with username [{}]", source.getUsername());
    try {
      String token = authenticationService.getLoginToken(source);
      LOG.info("Built token [{}]");
      User user = userService.loadUserByUsername(source.getUsername());
      if (user.isLoggedIn()) {
        LOG.warn("User [{}] already logged in.", user.getUsername());
        response.setStatus(HttpStatus.SC_UNAUTHORIZED);
        return;
      }
      user.setLoggedIn(!user.isLoggedIn());
// todo:why I can not update user      userService.update(user);
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

  @PostMapping("/logout")
  public void logout(HttpServletResponse response) {
    User user = authenticationService.getAuthenticatedUser();
    user.setLastConnection(Instant.now());
    user.setLoggedIn(!user.isLoggedIn());
//    userService.update(user);

    LOG.info("Request logout for user with username [{}]", user.getUsername());
    Cookie cookie = new Cookie(cookieName, "");
    cookie.setPath("/");
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }

  @GetMapping("/registrationConfirmation/{token}")
  public ResponseEntity confirmRegistration(@RequestParam("token") String token, @RequestBody String password, HttpServletResponse response) {

    VerificationToken verificationToken = authenticationService.getVerificationToken(token);
    if (verificationToken == null) {
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      return ResponseEntity.badRequest().body("Invalid token for registration confirmation");
    }
    //todo check token's expiry date

    User user = verificationToken.getUser();
    user.setPassword(encoder.encode(password));
    user.setEnabled(true);
    user.setAccountNonLocked(true);
    user.setAccountNonExpired(true);
    user.setCredentialsNonExpired(true);
    userService.update(user);
    return ResponseEntity.ok("Registration successfully completed");
  }

}
