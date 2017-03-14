package com.ownspec.center.controller;

import com.ownspec.center.dto.ApplicationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 05/03/2017
 *
 * @author lyrold
 */
@RestController
@RequestMapping(value = "/api/application")
@Slf4j
public class ApplicationController {

  @GetMapping
  @ResponseBody
  public ApplicationDto get() {
    return ApplicationDto.toApplicationDto();
  }

}
