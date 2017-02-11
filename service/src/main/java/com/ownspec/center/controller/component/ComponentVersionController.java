package com.ownspec.center.controller.component;

import com.ownspec.center.dto.ComponentVersionDto;
import com.ownspec.center.repository.component.ComponentVersionRepository;
import com.ownspec.center.service.CommentService;
import com.ownspec.center.service.UploadService;
import com.ownspec.center.service.component.ComponentConverter;
import com.ownspec.center.service.component.ComponentService;
import com.ownspec.center.service.component.ComponentTagService;
import com.ownspec.center.service.component.ComponentVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lyrold on 20/09/2016.
 */
@RestController
@RequestMapping("/api/component-versions")
public class ComponentVersionController {

  @Autowired
  private ComponentService componentService;

  @Autowired
  private ComponentVersionService componentVersionService;

  @Autowired
  private CommentService commentService;

  @Autowired
  private ComponentConverter componentConverter;

  @Autowired
  private UploadService uploadService;

  @Autowired
  private ComponentTagService componentTagService;

  @Autowired
  private ComponentVersionRepository componentVersionRepository;


  @GetMapping
  public List<ComponentVersionDto> findAllVersion(@RequestParam(value = "statuses", required = false, defaultValue = "false") Boolean statuses,
                                                  @RequestParam(value = "references", required = false, defaultValue = "false") Boolean references,
                                                  @RequestParam(value = "usePoints", required = false, defaultValue = "false") Boolean usePoints) {

    return componentVersionRepository.findAll()
        .stream()
        .map(cv -> componentConverter.toComponentVersionDto(cv, statuses, references, usePoints))
        .collect(Collectors.toList());
  }

  @GetMapping("{id}")
  public ComponentVersionDto findOne(@PathVariable("id") Long id, @RequestParam(value = "statuses", required = false, defaultValue = "false") Boolean statuses,
                                           @RequestParam(value = "references", required = false, defaultValue = "false") Boolean references,
                                           @RequestParam(value = "usePoints", required = false, defaultValue = "false") Boolean usePoints) {

    return componentConverter.toComponentVersionDto(componentVersionRepository.findOne(id), statuses, references, usePoints );
  }


}
