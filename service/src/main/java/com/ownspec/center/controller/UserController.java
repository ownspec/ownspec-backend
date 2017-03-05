package com.ownspec.center.controller;

import static com.ownspec.center.dto.StatusDto.createFromStatuses;

import com.google.common.collect.ImmutableMap;
import com.ownspec.center.dto.user.UserDto;
import com.ownspec.center.model.user.User;
import com.ownspec.center.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by nlabrot on 29/09/16.
 */
@RestController
@RequestMapping(value = "/api/users")
@Slf4j
public class UserController {

  @Autowired
  private UserService userService;


  @RequestMapping
  @ResponseBody
  public List<UserDto> findAll() {
    return userService.findAll().stream().map(UserDto::fromUser).collect(Collectors.toList());
  }

  @GetMapping(value = "/me")
  @ResponseBody
  public UserDto me(@AuthenticationPrincipal User user) {
    return UserDto.fromUser(user);
  }

  @GetMapping(value = "/me/profile")
  @ResponseBody
  public ResponseEntity profile(@AuthenticationPrincipal User user) {
    UserDto userDto = UserDto.fromUser(user);

    ImmutableMap.Builder<Object, Object> propertiesBuilder = ImmutableMap.builder();
    propertiesBuilder.put("statuses", createFromStatuses());

    return ResponseEntity.ok(ImmutableMap.builder()
        .put("user", userDto)
        .put("properties", propertiesBuilder.build()).build());
  }


  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping(value = "/new")
  @ResponseBody
  public ResponseEntity create(@RequestBody UserDto source, HttpServletRequest request) throws Exception {
    URL requestURL = new URL(request.getRequestURL().toString());
    userService.create(source, requestURL);
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = "/{id}")
  @ResponseBody
  public ResponseEntity update(@PathVariable("id") Long id, @RequestBody UserDto source) {
    userService.update(source, id);
    return ResponseEntity.ok().build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping(value = "/{id}")
  @ResponseBody
  public ResponseEntity delete(@PathVariable("id") Long id) {
    userService.delete(id);
    return ResponseEntity.ok().build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping(value = "/{id}/resetPassword")
  @ResponseBody
  public ResponseEntity resetPassword(@PathVariable("id") Long id) {
    userService.resetPassword(id);
    return ResponseEntity.ok().build();
  }

}
