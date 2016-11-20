package com.ownspec.center.controller;

import com.ownspec.center.dto.UserDto;
import com.ownspec.center.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by nlabrot on 29/09/16.
 */
@RestController
@RequestMapping(value = "/api/auth")
public class AuthenticationController {


  @Autowired
  private AuthenticationService authenticationService;

  @PostMapping(value = "/login")
  @ResponseBody
  public ResponseEntity login(@RequestBody UserDto source) {
    return authenticationService.login(source);
  }

  @PostMapping(value = "/logout")
  public HttpServletResponse logout(HttpServletResponse response){
    return authenticationService.logOut(response);
  }


}
