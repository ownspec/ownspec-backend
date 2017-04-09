package com.ownspec.center.controller.component;

import com.ownspec.center.dto.component.ComponentVersionDto;
import com.ownspec.center.repository.component.AnotherComponentVersionRepository;
import com.ownspec.center.repository.component.ComponentVersionRepository;
import com.ownspec.center.service.UploadService;
import com.ownspec.center.service.component.ComponentConverter;
import com.ownspec.center.service.component.ComponentService;
import com.ownspec.center.service.component.ComponentVersionService;
import com.ownspec.center.service.workflow.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lyrold on 20/09/2016.
 */
@RestController
@RequestMapping("/api/search/component-versions")
public class ComponentVersionSearchController {

  @Autowired
  private ComponentService componentService;

  @Autowired
  private ComponentVersionService componentVersionService;

  @Autowired
  private ComponentConverter componentConverter;

  @Autowired
  private UploadService uploadService;

  @Autowired
  private ComponentVersionRepository componentVersionRepository;

  @Autowired
  private WorkflowService workflowService;


  @Autowired
  private AnotherComponentVersionRepository anotherComponentVersionRepository;


  @PostMapping
  public List<ComponentVersionDto> findAllVersion(@RequestBody ComponentVersionSearchBean searchBean,
                                                  @RequestParam(value = "statuses", required = false, defaultValue = "false") Boolean statuses,
                                                  @RequestParam(value = "references", required = false, defaultValue = "false") Boolean references,
                                                  @RequestParam(value = "usePoints", required = false, defaultValue = "false") Boolean usePoints) {

    return anotherComponentVersionRepository.findAll(searchBean)
        .collect(Collectors.toList());
  }

}
