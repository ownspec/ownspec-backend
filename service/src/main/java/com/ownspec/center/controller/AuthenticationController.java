package com.ownspec.center.controller;

import static java.util.Objects.requireNonNull;

import com.ownspec.center.dto.user.UserDto;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.user.VerificationToken;
import com.ownspec.center.service.AuthenticationService;
import com.ownspec.center.service.EmailService;
import com.ownspec.center.service.UserService;
import com.ownspec.center.util.AbstractMimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.time.Instant;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
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

  @Autowired
  private EmailService emailService;

  @PostMapping("/login")
  @ResponseBody
  public void login(@RequestBody UserDto source, HttpServletResponse response, HttpServletRequest request) {
    LOG.info("Request login with username [{}]", source.getUsername());
    try {
      String token = authenticationService.getLoginToken(source);
      LOG.info("Authentication succeed; built token is [{}]", token);
      User user = authenticationService.getAuthenticatedUser();
      user.setLastConnection(Instant.now());
      userService.update(user);

      Cookie newCookie = new Cookie(cookieName, token);
      newCookie.setMaxAge(cookieMaxAge);
      newCookie.setHttpOnly(true);
      newCookie.setPath("/");

      response.addCookie(newCookie);
      response.setStatus(200);
    } catch (Exception e) {
      LOG.warn(e.getMessage(), e);
      response.setStatus(400);
    }
  }

  @PostMapping("/logout")
  public void logout(HttpServletResponse response) {
    User user = authenticationService.getAuthenticatedUser();
    LOG.info("Request logout for user with username [{}]", user.getUsername());
    Cookie cookie = new Cookie(cookieName, "");
    cookie.setPath("/");
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }

  @PostMapping("/registration/confirmation/{token}")
  @ResponseBody
  public ResponseEntity confirmRegistration(@PathVariable("token") String token, @RequestBody String password, HttpServletResponse response) {

    // Check token
    VerificationToken verificationToken = authenticationService.getVerificationToken(token);
    if (verificationToken == null) {
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      return ResponseEntity.badRequest().body("Invalid token for registration confirmation");
    }
    //todo check token's expiry date

    // Update user
    User user = verificationToken.getUser();
    user.setPassword(encoder.encode(password));
    user.setEnabled(true);
    user.setAccountNonLocked(true);
    user.setAccountNonExpired(true);
    user.setCredentialsNonExpired(true);
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword()));
    userService.update(user);

    // Remove token
    authenticationService.deleteVerificationToken(verificationToken);

    return ResponseEntity.ok("Registration successfully completed");
  }

  @PostMapping("/registration/confirmation/resend/{userId}")
  @ResponseBody
  public ResponseEntity resendRegistrationConfirmationEmail(@PathVariable("userId") Long userId,
                                                            HttpServletRequest request,
                                                            HttpServletResponse response) throws Exception {
    //Check if users exists
    User user = userService.findOne(userId);
    if (user == null) {
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      return ResponseEntity.badRequest().body("No user found with id [" + userId + "]");
    }

    // Check if token exists for user
    VerificationToken verificationToken = authenticationService.getVerificationToken(user);
    if (verificationToken == null) {
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      return ResponseEntity.badRequest().body("No verification token found for user with id [" + userId + "]");
    }
    //todo check token's expiry date
    URL requestURL = new URL(request.getRequestURL().toString());
    AbstractMimeMessage message = userService.buildConfirmRegistrationMessage(user, verificationToken.getToken(), requestURL);
    emailService.send(message);

    return ResponseEntity.ok("Registration confirmation email successfully sent");
  }


  @PostMapping(value = "/user/{id}/password")
  @ResponseBody
  public ResponseEntity sendChangePasswordEmail(@PathVariable("id") Long id, HttpServletRequest request) throws Exception {
    URL requestURL = new URL(request.getRequestURL().toString());
    userService.sendChangePasswordEmail(id, requestURL);
    return ResponseEntity.ok().build();
  }

  @PatchMapping(value = "/user/{id}/password")
  @ResponseBody
  public ResponseEntity changePassword(@PathVariable("id") Long id,
                                       @RequestBody String password) throws Exception {
    User user = requireNonNull(userService.findOne(id));
    user.setPassword(encoder.encode(password));
    return ResponseEntity.ok(UserDto.fromUser(userService.update(user)));
  }
}
