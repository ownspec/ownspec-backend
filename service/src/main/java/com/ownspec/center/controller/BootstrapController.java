package com.ownspec.center.controller;

import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.dto.ComponentVersionDto;
import com.ownspec.center.dto.EstimatedTimeDto;
import com.ownspec.center.dto.ImmutableComponentDto;
import com.ownspec.center.dto.ImmutableComponentVersionDto;
import com.ownspec.center.dto.ImmutableEstimatedTimeDto;
import com.ownspec.center.model.DistributionLevel;
import com.ownspec.center.model.Project;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.CoverageStatus;
import com.ownspec.center.model.component.RequirementType;
import com.ownspec.center.model.user.UserCategory;
import com.ownspec.center.repository.ProjectRepository;
import com.ownspec.center.repository.user.UserRepository;
import com.ownspec.center.service.component.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by nlabrot on 03/10/16.
 */
@RestController("/api/bootstrap")
public class BootstrapController {

  private static final List<EstimatedTimeDto> ESTIMATED_TIMES = Arrays.asList(
      ImmutableEstimatedTimeDto.newEstimatedTimeDto()
          .userCategory(new UserCategory("Analyst", 430.00))
          .time(1d)
          .timeUnit(TimeUnit.DAYS)
          .build(),

      ImmutableEstimatedTimeDto.newEstimatedTimeDto()
          .userCategory(new UserCategory("Developer", 585.34))
          .time(6d)
          .timeUnit(TimeUnit.DAYS)
          .build(),

      ImmutableEstimatedTimeDto.newEstimatedTimeDto()
          .userCategory(new UserCategory("Tester", 480.65))
          .time(3d)
          .timeUnit(TimeUnit.DAYS)
          .build()
  );


  @Autowired
  protected ComponentService componentService;

  @Autowired
  protected ProjectRepository projectRepository;

  @Autowired
  private UserRepository userRepository;


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
      project = new Project();
      project.setTitle(projectName);
      project.setManager(userRepository.findAll().get(0));
      project = projectRepository.save(project);
      projectId = project.getId();
    }


    String prefix = "";
    if (project != null) {
      prefix = "[Project " + projectId + "]";
    }

    for (Integer index = 0; index < nbComponents; index++) {
      ComponentVersionDto componentDto = ImmutableComponentVersionDto.newComponentVersionDto()
          .title(prefix + " COMPONENT " + index)
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
          .title(prefix + " REQUIREMENT " + index)
          .type(ComponentType.REQUIREMENT)
          .content("test1")
          .projectId(projectId)
          .distributionLevel(DistributionLevel.values()[new Random().nextInt(DistributionLevel.values().length)])
          .coverageStatus(CoverageStatus.values()[new Random().nextInt(CoverageStatus.values().length)])
          .requirementType(RequirementType.values()[new Random().nextInt(RequirementType.values().length)])
          .requiredTest(index % 5 == 0)
          .estimatedTimes(ESTIMATED_TIMES)
          .build();

      Component component = componentService.create(componentDto);
    }


    for (Integer index = 0; index < nbDocuments; index++) {
      ComponentVersionDto componentDto = ImmutableComponentVersionDto.newComponentVersionDto()

          .title(prefix + " DOCUMENT " + index)
          .type(ComponentType.DOCUMENT)
          .content("test1")
          .projectId(projectId)
          .distributionLevel(DistributionLevel.INTERNAL)
          .coverageStatus(CoverageStatus.UNCOVERED)
          .requiredTest(false)
          .build();

      Component component = componentService.create(componentDto);
    }

  }
}