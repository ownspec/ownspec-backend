package com.ownspec.center.controller;

import static com.ownspec.center.configuration.SecurityConfiguration.TOKEN_COOKIE_NAME;
import static com.ownspec.center.dto.StatusDto.createFromStatuses;

import com.google.common.collect.ImmutableMap;
import com.ownspec.center.dto.UserDto;
import com.ownspec.center.model.user.User;
import com.ownspec.center.service.SecurityService;
import com.ownspec.center.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by nlabrot on 29/09/16.
 */
@RestController
@RequestMapping(value = "/api/users")
@Slf4j
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private SecurityService securityService;

  @Autowired
  private AuthenticationService authenticationService;


  @RequestMapping
  @ResponseBody
  public List<User> findAll() {
    return userService.findAll();
  }

  @GetMapping(value = "/me")
  @ResponseBody
  public UserDto me(@AuthenticationPrincipal User user) {
    return UserDto.createFromUser(user);
  }

  @GetMapping(value = "/me/profile")
  @ResponseBody
  public ResponseEntity profile(@AuthenticationPrincipal User user) {
    UserDto userDto = UserDto.createFromUser(user);

    ImmutableMap.Builder<Object, Object> propertiesBuilder = ImmutableMap.builder();
    propertiesBuilder.put("statuses", createFromStatuses());

    return ResponseEntity.ok(ImmutableMap.builder()
        .put("user", userDto)
        .put("properties", propertiesBuilder.build()).build());
  }

  //    @PreAuthorize("hasRole('ADMIN')")
  @PostMapping(value = "/create")
  @ResponseBody
  public ResponseEntity create(@RequestBody UserDto source) {
    userService.create(source);
    return ResponseEntity.ok().build();
  }

  @PutMapping(value = "/{id}/update")
  @ResponseBody
  public ResponseEntity update(@PathVariable("id") Long id, @RequestBody UserDto source) {
    userService.update(source, id);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping(value = "/{id}/delete")
  @ResponseBody
  public ResponseEntity delete(@PathVariable("id") Long id) {
    userService.delete(id);
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = "/{id}/resetPassword")
  @ResponseBody
  public ResponseEntity resetPassword(@PathVariable("id") Long id) {
    userService.resetPassword(id);
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = "/login")
  @ResponseBody
  public ResponseEntity login(@RequestBody UserDto source) {
    LOG.info("Request login with username [{}]", source.getUsername());
    String token = userService.getLoginToken(source);
    LOG.info("Authentication succeed; built token is [{}]", token);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(HttpHeaders.SET_COOKIE, String.join("=", TOKEN_COOKIE_NAME, token) + "; path=/");
    return new ResponseEntity<String>(httpHeaders, HttpStatus.OK);

  }

  @PostMapping(value = "/logout")
  public void logout(HttpServletResponse response) {
    User user = securityService.getAuthenticatedUser();
    //todo update user's lastConnection
    LOG.info("Request logout for user with username [{}]", user.getUsername());
    Cookie cookie = new Cookie(TOKEN_COOKIE_NAME, "");
    cookie.setPath("/");
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }


}
