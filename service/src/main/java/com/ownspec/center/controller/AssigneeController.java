package com.ownspec.center.controller;

import com.ownspec.center.dto.user.UserDto;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.repository.component.ComponentVersionRepository;
import com.ownspec.center.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by nlabrot on 29/09/16.
 */
@RestController
@RequestMapping(value = "/api/assignees")
@Slf4j
public class AssigneeController {

  @Autowired
  private UserService userService;

  @Autowired
  private ComponentVersionRepository componentVersionRepository;


  @GetMapping
  public List<UserDto> findAssignees(

      @RequestParam(value = "projectId", required = false) Long projectId,
      @RequestParam(value = "q", required = false) String query,
      @RequestParam(value = "sort", required = false) String sort,
      @RequestParam(value = "types", required = false) Optional<List<ComponentType>> types,
      @RequestParam(value = "statuses", required = false, defaultValue = "false") Boolean statuses,
      @RequestParam(value = "references", required = false, defaultValue = "false") Boolean references,
      @RequestParam(value = "usePoints", required = false, defaultValue = "false") Boolean usePoints,
      @RequestParam(value = "generic", required = false, defaultValue = "false") Boolean generic) {

    return componentVersionRepository.findAllAssignee(projectId, generic, types.orElse(Collections.emptyList()), query, null)
        .stream()
        .map(UserDto::fromUser)
        .collect(Collectors.toList());
  }

}
