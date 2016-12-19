package com.ownspec.center.service.component;

import static com.ownspec.center.dto.ComponentDto.newBuilderFromComponent;
import static com.ownspec.center.dto.ImmutableComponentReferenceDto.newComponentReferenceDto;
import static com.ownspec.center.dto.WorkflowInstanceDto.newBuilderFromWorkflowInstance;
import static com.ownspec.center.dto.WorkflowStatusDto.newBuilderFromWorkflowStatus;

import com.ownspec.center.dto.CommentDto;
import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.dto.ComponentReferenceDto;
import com.ownspec.center.dto.EstimatedTimeDto;
import com.ownspec.center.dto.ImmutableComponentDto;
import com.ownspec.center.dto.UserComponentDto;
import com.ownspec.center.dto.UserDto;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.CoverageStatus;
import com.ownspec.center.model.user.UserComponent;
import com.ownspec.center.repository.component.ComponentReferenceRepository;
import com.ownspec.center.repository.user.UserComponentRepository;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;
import com.ownspec.center.service.CommentService;
import com.ownspec.center.service.EstimatedTimeService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by nlabrot on 04/12/16.
 */
@org.springframework.stereotype.Component
public class ComponentConverter {


  @Autowired
  private ComponentService componentService;

  @Autowired
  private CommentService commentService;

  @Autowired
  private ComponentReferenceRepository componentReferenceRepository;

  @Autowired
  private WorkflowStatusRepository workflowStatusRepository;

  @Autowired
  private EstimatedTimeService estimatedTimeService;

  @Autowired
  private UserComponentRepository userComponentRepository;

  public ComponentDto toDto(Long id, boolean content, boolean workflow, boolean comments, boolean references) {
    return toDto(componentService.findOne(id), content, workflow, comments, references);
  }

  public ComponentDto toDto(Component c, boolean content, boolean workflow, boolean comments, boolean references) {
    ImmutableComponentDto.Builder builder = newBuilderFromComponent(c);


    if (c.getType() != ComponentType.RESOURCE) {
      Pair<String, String> contentPair = componentService.generateContent(c);

      if (content) {
        builder.content(contentPair.getLeft());
      }

      builder.summary(contentPair.getRight());
    }

    builder.currentWorkflowStatus(newBuilderFromWorkflowStatus(componentService.getCurrentStatus(c.getId())).build());

    if (workflow) {
      builder.workflowInstances(componentService.getWorkflowStatuses(c.getId()));
    }

    if (comments) {
      builder.comments(commentService.getComments(c.getId()).stream()
          .map(CommentDto::createFromComment)
          .collect(Collectors.toList()));
    }

    if (references) {
      List<ComponentReferenceDto> componentReferences = getComponentReferences(c);
      builder.componentReferences(componentReferences);
//      builder.coverageStatus(getGlobalCoverageStatus(componentReferences));
    } else {
      builder.coverageStatus(c.getCoverageStatus());
    }

    if (c.isRequirement()) {
      builder.requirementType(c.getRequirementType());
    }
    if (c.getAssignedTo() != null) {
      builder.assignedTo(UserDto.fromUser(c.getAssignedTo()));
    }

    List<UserComponent> userComponents = userComponentRepository.findAllByComponentId(c.getId());
    if (userComponents != null) {
      builder.componentUsers(
          userComponents.stream()
              .map(UserComponentDto::fromUserComponent)
              .collect(Collectors.toList())
      );
    }
    builder.estimatedTimes(estimatedTimeService.getEstimatedTimes(c.getId()).stream()
        .map(EstimatedTimeDto::createFromEstimatedTime)
        .collect(Collectors.toList()));


    return builder.build();
  }

  private CoverageStatus getGlobalCoverageStatus(List<ComponentReferenceDto> componentReferences) {
    for (ComponentReferenceDto componentReference : componentReferences) {
      CoverageStatus coverageStatus = componentReference.getSource().getCoverageStatus();
      if (CoverageStatus.FAILED.equals(coverageStatus)) {
        return CoverageStatus.FAILED;
      } else {
        if (CoverageStatus.UNCOVERED.equals(coverageStatus) || CoverageStatus.IN_PROGRESS.equals(coverageStatus)) {
          return CoverageStatus.UNCOVERED;
        }
        return CoverageStatus.OK;
      }
    }
    return null;
  }


  public List<ComponentReferenceDto> getComponentReferences(Component component) {

    return componentReferenceRepository.findAllBySourceIdAndSourceWorkflowInstanceId(component.getId(), component.getCurrentWorkflowInstance().getId())
        .stream()
        .map(r -> newComponentReferenceDto()
            .source(toDto(r.getSource(), false, false, false, false))
            .sourceWorkflowInstance(
                newBuilderFromWorkflowInstance(r.getSourceWorkflowInstance())
                    .currentWorkflowStatus(
                        newBuilderFromWorkflowStatus(workflowStatusRepository.findLatestWorkflowStatusByWorkflowInstanceId(r.getSourceWorkflowInstance().getId())).build())
                    .build()
            )

            .target(toDto(r.getTarget(), false, false, false, false))
            .targetWorkflowInstance(
                newBuilderFromWorkflowInstance(r.getTargetWorkflowInstance())
                    .currentWorkflowStatus(
                        newBuilderFromWorkflowStatus(workflowStatusRepository.findLatestWorkflowStatusByWorkflowInstanceId(r.getTargetWorkflowInstance().getId())).build())
                    .build())
            .build()
        ).collect(Collectors.toList());
  }
}
