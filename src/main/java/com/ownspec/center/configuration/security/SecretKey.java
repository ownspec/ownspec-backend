package com.ownspec.center.configuration.security;

/**
 * Created by nlabrot on 19/11/16.
 */
public class SecretKey {
  private String value;

  public SecretKey(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
