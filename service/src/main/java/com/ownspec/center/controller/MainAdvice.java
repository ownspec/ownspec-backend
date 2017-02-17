package com.ownspec.center.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Created by nlabrot on 16/02/17.
 */
@ControllerAdvice
public class MainAdvice {

  @ExceptionHandler
  public ResponseEntity exception(Exception e){
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }
}
