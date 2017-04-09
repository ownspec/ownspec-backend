package com.ownspec.center.util;

/**
 * Created by nlabrot on 06/04/17.
 */
public class InvalidDurationException extends RuntimeException {

  public InvalidDurationException() {
  }

  public InvalidDurationException(String s) {
    super(s);
  }

  public InvalidDurationException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public InvalidDurationException(Throwable throwable) {
    super(throwable);
  }

  public InvalidDurationException(String s, Throwable throwable, boolean b, boolean b1) {
    super(s, throwable, b, b1);
  }
}
