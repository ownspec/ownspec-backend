package com.ownspec.center.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Created by nlabrot on 16/02/17.
 */
@ControllerAdvice
public class MainAdvice {

  private static final Logger LOG = LoggerFactory.getLogger(MainAdvice.class);

  @ExceptionHandler
  public ResponseEntity exception(Exception e){
    LOG.error(e.getMessage() , e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }
}
