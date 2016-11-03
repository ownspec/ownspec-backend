package com.ownspec.center.controller;

import static java.util.stream.Collectors.toList;

import com.google.common.collect.ImmutableMap;
import com.ownspec.center.dto.StatusDto;
import com.ownspec.center.dto.UserDto;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by nlabrot on 29/09/16.
 */
@RestController
@RequestMapping(value = "/api/users")
public class UserController {

  @Autowired
  private UserService userService;


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
    propertiesBuilder.put("statuses", Arrays.stream(Status.values()).map(StatusDto::createFromStatus).collect(toList()));

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
    return userService.login(source);
  }

  @PostMapping(value = "/logout")
  public HttpServletResponse logout(HttpServletResponse response){
    return userService.logOut(response);
  }


}
