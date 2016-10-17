package com.ownspec.center.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ownspec.center.model.user.User;

/**
 * Created by nlabrot on 02/10/16.
 */
@Service
public class SecurityService {

  @Autowired
  private User user;


  public User getAuthentifiedUser() {
    return user;
  }
}
