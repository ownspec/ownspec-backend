package com.ownspec.center.service;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.ignoreCase;

import com.ownspec.center.model.user.UserCategory;
import com.ownspec.center.repository.user.UserCategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by lyrold on 23/08/2016.
 */
@Service
@Slf4j
@Transactional
public class UserCategoryService {

  @Autowired
  private UserCategoryRepository userCategoryRepository;

  public List<UserCategory> findAll(String query) {
    if (StringUtils.isNotBlank(query)) {
      UserCategory example = new UserCategory();
      example.setName(query);
      ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("name", ignoreCase().contains());
      return userCategoryRepository.findAll(Example.of(example, matcher), new Sort("name"));
    } else {
      return userCategoryRepository.findAll(new Sort("name"));
    }
  }

  public UserCategory findOne(Long id) {
    return userCategoryRepository.findOne(id);
  }
}
