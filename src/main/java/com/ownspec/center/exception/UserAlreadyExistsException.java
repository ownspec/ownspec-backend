package com.ownspec.center.exception;

/**
 * Created by lyrold on 23/08/2016.
 */
public class UserAlreadyExistsException extends RuntimeException {

  public UserAlreadyExistsException(String username) {
    super("Username " + username + " already exists");
  }
}
