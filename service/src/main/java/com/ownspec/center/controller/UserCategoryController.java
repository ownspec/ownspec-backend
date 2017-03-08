package com.ownspec.center.controller;

import com.ownspec.center.dto.user.UserCategoryDto;
import com.ownspec.center.model.user.UserCategory;
import com.ownspec.center.repository.user.UserCategoryRepository;
import com.ownspec.center.service.UserCategoryService;
import com.ownspec.center.service.component.ComponentConverter;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
  private UserCategoryService userCategoryService;


  @Autowired
  private ComponentConverter componentConverter;

  @GetMapping
  public List<UserCategoryDto> findAll(@RequestParam(value = "q", required = false) String query) {
    return userCategoryService.findAll(query).stream()
        .map(UserCategoryDto::fromUserCategory)
        .collect(Collectors.toList());
  }

  @GetMapping("{id}")
  public ResponseEntity<UserCategoryDto> findOne(@PathVariable("id") Long id) {
    UserCategory userCategory = userCategoryRepository.findOne(id);
    if (userCategory != null) {
      return ResponseEntity.ok(UserCategoryDto.fromUserCategory(userCategory));
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("{id}")
  public ResponseEntity delete(@PathVariable("id") Long id) {
    userCategoryRepository.delete(id);
    return ResponseEntity.ok().build();
  }

  //TODO: move into a service
  @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Transactional
  public UserCategoryDto create(@RequestBody UserCategoryDto userCategoryDto) throws IOException, GitAPIException {
    UserCategory userCategory = new UserCategory();
    userCategory.setName(userCategoryDto.getName());
    userCategory.setHourlyPrice(userCategoryDto.getHourlyPrice());
    userCategory = userCategoryRepository.save(userCategory);
    return UserCategoryDto.fromUserCategory(userCategory);
  }

  @PatchMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Transactional
  public UserCategoryDto update(@RequestBody UserCategoryDto userCategoryDto) throws IOException, GitAPIException {
    UserCategory userCategory = userCategoryRepository.findOne(userCategoryDto.getId());
    userCategory.setName(userCategoryDto.getName());
    userCategory.setHourlyPrice(userCategoryDto.getHourlyPrice());
    userCategory = userCategoryRepository.save(userCategory);
    return UserCategoryDto.fromUserCategory(userCategory);
  }

}
