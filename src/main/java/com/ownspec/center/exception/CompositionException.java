package com.ownspec.center.exception;

/**
 * Created by lyrold on 09/10/2016.
 */
public class CompositionException extends RuntimeException {

  public CompositionException(Throwable cause) {
    super(cause);
  }

  public CompositionException(String message, Throwable cause) {
    super(message, cause);
  }
}
