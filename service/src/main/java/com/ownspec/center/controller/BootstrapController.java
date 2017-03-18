package com.ownspec.center.controller;

import com.ownspec.center.dto.component.ComponentVersionDto;
import com.ownspec.center.dto.component.ImmutableComponentVersionDto;
import com.ownspec.center.model.Project;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentCodeCounter;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.CoverageStatus;
import com.ownspec.center.model.component.DistributionLevel;
import com.ownspec.center.model.component.RequirementType;
import com.ownspec.center.repository.ComponentCodeCounterRepository;
import com.ownspec.center.repository.ProjectRepository;
import com.ownspec.center.repository.user.UserRepository;
import com.ownspec.center.service.component.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by nlabrot on 03/10/16.
 */
@RestController("/api/bootstrap")
public class BootstrapController {

  private static final String[] LOREM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque vel lorem quam. Etiam faucibus, enim in vehicula gravida, leo neque venenatis dui, vel consequat elit mi quis velit. Sed rhoncus sodales lacinia. Quisque fermentum fringilla ligula non porttitor. Cras tellus sapien, sodales eget laoreet et, interdum ac augue. Nunc molestie dui in turpis aliquet accumsan. Phasellus mollis lobortis nisl. Nullam pharetra placerat erat, quis euismod sem molestie ac. Mauris nec elit et mauris mattis tristique eget at metus. Maecenas blandit sagittis risus. Nunc at libero orci. Maecenas viverra est at eros tempus, at fringilla neque tempus.".split(" ");

  @Autowired
  protected ComponentService componentService;

  @Autowired
  protected ProjectRepository projectRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ComponentCodeCounterRepository componentCodeCounterRepository;

  @RequestMapping
  @Transactional
  public void init(@RequestParam(value = "projectName", required = false) String projectName,
                   @RequestParam(value = "nbComponents", defaultValue = "5") Integer nbComponents,
                   @RequestParam(value = "nbRequirements", defaultValue = "20") Integer nbRequirements,
                   @RequestParam(value = "nbDocuments", defaultValue = "5") Integer nbDocuments
  ) {

    Long projectId = null;
    Project project = null;

    if (projectName != null) {
      ComponentCodeCounter ccc = new ComponentCodeCounter();
      ccc.setKey(projectName.toUpperCase());
      ccc = componentCodeCounterRepository.save(ccc);


      project = new Project();
      project.setTitle(projectName);
      project.setManager(userRepository.findAll().get(0));
      project.setComponentCodeCounter(ccc);
      project = projectRepository.save(project);
      projectId = project.getId();
    }


    String prefix = "";
    if (project != null) {
      prefix = "[Project " + projectId + "]";
    }

    for (Integer index = 0; index < nbComponents; index++) {
      ComponentVersionDto componentDto = ImmutableComponentVersionDto.newComponentVersionDto()
          .title(generateTitle())
          .version("1")
          .type(ComponentType.COMPONENT)
          .content("test1")
          .projectId(projectId)
          .distributionLevel(DistributionLevel.INTERNAL)
          .coverageStatus(CoverageStatus.UNCOVERED)
          .requiredTest(true)
          .build();

      componentService.create(componentDto);
    }


    for (Integer index = 0; index < nbRequirements; index++) {

      ComponentVersionDto componentDto = ImmutableComponentVersionDto.newComponentVersionDto()
          .title(generateTitle())
          .version("1")
          .type(ComponentType.REQUIREMENT)
          .content("test1")
          .projectId(projectId)
          .distributionLevel(DistributionLevel.values()[new Random().nextInt(DistributionLevel.values().length)])
          .coverageStatus(CoverageStatus.values()[new Random().nextInt(CoverageStatus.values().length)])
          .requirementType(RequirementType.values()[new Random().nextInt(RequirementType.values().length)])
          .requiredTest(index % 5 == 0)
          .build();

      Component component = componentService.create(componentDto).getLeft();
    }


    for (Integer index = 0; index < nbDocuments; index++) {
      ComponentVersionDto componentDto = ImmutableComponentVersionDto.newComponentVersionDto()
          .title(generateTitle())
          .version("1")
          .type(ComponentType.DOCUMENT)
          .content("test1")
          .projectId(projectId)
          .distributionLevel(DistributionLevel.INTERNAL)
          .coverageStatus(CoverageStatus.UNCOVERED)
          .requiredTest(false)
          .build();

      Component component = componentService.create(componentDto).getLeft();
    }

  }


  private String generateTitle() {


    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < ThreadLocalRandom.current().nextInt(6, 16); i++) {
      builder.append(LOREM[ThreadLocalRandom.current().nextInt(LOREM.length)]).append(" ");
    }

    return builder.toString().trim();
  }
}
