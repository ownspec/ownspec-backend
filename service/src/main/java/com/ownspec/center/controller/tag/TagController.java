package com.ownspec.center.controller.tag;

import com.ownspec.center.dto.TagDto;
import com.ownspec.center.dto.component.ComponentDto;
import com.ownspec.center.repository.tag.ComponentTagRepository;
import com.ownspec.center.repository.tag.TagRepository;
import com.ownspec.center.service.component.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by nlabrot on 18/01/17.
 */
@RestController
@RequestMapping("/api/tags")
public class TagController {

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private ComponentTagRepository componentTagRepository;


  @Autowired
  private ComponentService componentService;



  @RequestMapping
  public List<TagDto> findAll() {
    return tagRepository.findAll().stream()
        .map(TagDto::createFromTag)
        .collect(Collectors.toList());
  }


  @RequestMapping("{tag}")
  public List<ComponentDto> findComponentsByTag(@PathVariable("tag") String tag) {
    componentTagRepository.findAll();

    return null;
/*
    return tagRepository.findAll().stream()
        .map(TagDto::createFromTag)
        .collect(Collectors.toList());
*/
  }




}
