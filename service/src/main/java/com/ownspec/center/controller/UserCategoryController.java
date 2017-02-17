package com.ownspec.center.controller;

import com.ownspec.center.dto.user.UserCategoryDto;
import com.ownspec.center.repository.user.UserCategoryRepository;
import com.ownspec.center.service.component.ComponentConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by nlabrot on 17/02/17.
 */
@RestController
@RequestMapping("/api/user-categories")
public class UserCategoryController {

  @Autowired
  private UserCategoryRepository userCategoryRepository;

  @Autowired
  private ComponentConverter componentConverter;

  @GetMapping
  public List<UserCategoryDto> findAll(){
    return userCategoryRepository.findAll().stream()
        .map(UserCategoryDto::fromUserCategory)
        .collect(Collectors.toList());
  }
}
