package com.ownspec.center.controller;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.dto.ImmutableComponentDto;
import com.ownspec.center.dto.WorkflowStatusDto;
import com.ownspec.center.model.Project;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.repository.ProjectRepository;
import com.ownspec.center.service.ComponentService;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.naming.SelectorContext.prefix;

/**
 * Created by nlabrot on 03/10/16.
 */
@RestController("/api/bootstrap")
public class BootstrapController {

  @Autowired
  protected ComponentService componentService;

  @Autowired
  protected ProjectRepository projectRepository;


  @RequestMapping
  @Transactional
  public void init(@RequestParam(value = "projectName", required = false) String projectName,
                   @RequestParam(value = "nbComponents", defaultValue = "5") Integer nbComponents,
                   @RequestParam(value = "nbRequirements", defaultValue = "5") Integer nbRequirements,
                   @RequestParam(value = "nbDocuments", defaultValue = "5") Integer nbDocuments
  ) {

    Long projectId = null;
    Project project = null;

    if (projectName != null) {
      project = new Project();
      project.setTitle(projectName);
      project = projectRepository.save(project);
      projectId = project.getId();
    }


    String prefix = "";
    if (project != null) {
      prefix = "[Project " + projectId + "]";
    }

    for (Integer index = 0; index < nbComponents; index++) {
      ComponentDto componentDto = ImmutableComponentDto.newComponentDto()
          .title(prefix + " COMPONENT " + index)
          .type(ComponentType.COMPONENT)
          .content("test1")
          .projectId(projectId)
          .build();

      Component component = componentService.create(componentDto);
    }


    for (Integer index = 0; index < nbRequirements; index++) {
      ComponentDto componentDto = ImmutableComponentDto.newComponentDto()
          .title(prefix + " REQUIREMENT " + index)
          .type(ComponentType.REQUIREMENT)
          .content("test1")
          .projectId(projectId)
          .build();

      Component component = componentService.create(componentDto);
    }


    for (Integer index = 0; index < nbDocuments; index++) {
      ComponentDto componentDto = ImmutableComponentDto.newComponentDto()
          .title(prefix + " DOCUMENT " + index)
          .type(ComponentType.DOCUMENT)
          .content("test1")
          .projectId(projectId)
          .build();

      Component component = componentService.create(componentDto);
    }

  }
}
