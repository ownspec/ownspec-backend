package com.ownspec.center.service.component;

import static com.ownspec.center.dto.ComponentDto.newBuilderFromComponent;
import static com.ownspec.center.dto.ImmutableComponentReferenceDto.newComponentReferenceDto;
import static com.ownspec.center.dto.WorkflowStatusDto.newBuilderFromWorkflowStatus;

import com.ownspec.center.dto.ChangeDto;
import com.ownspec.center.dto.CommentDto;
import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.dto.ComponentReferenceDto;
import com.ownspec.center.dto.ComponentVersionDto;
import com.ownspec.center.dto.EstimatedTimeDto;
import com.ownspec.center.dto.ImmutableComponentDto;
import com.ownspec.center.dto.ImmutableComponentVersionDto;
import com.ownspec.center.dto.ImmutableWorkflowInstanceDto;
import com.ownspec.center.dto.UserComponentDto;
import com.ownspec.center.dto.UserDto;
import com.ownspec.center.dto.WorkflowInstanceDto;
import com.ownspec.center.dto.WorkflowStatusDto;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentReference;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.CoverageStatus;
import com.ownspec.center.model.user.UserComponent;
import com.ownspec.center.model.workflow.WorkflowInstance;
import com.ownspec.center.model.workflow.WorkflowStatus;
import com.ownspec.center.repository.tag.ComponentTagRepository;
import com.ownspec.center.repository.component.ComponentReferenceRepository;
import com.ownspec.center.repository.user.UserComponentRepository;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;
import com.ownspec.center.service.CommentService;
import com.ownspec.center.service.EstimatedTimeService;
import com.ownspec.center.service.workflow.WorkflowConfiguration;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

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

  @Autowired
  private ComponentTagRepository componentTagRepository;

  @Autowired
  private WorkflowConfiguration workflowConfiguration;



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

    WorkflowInstance currentWorkflowInstance = componentService.getCurrentWorkflowInstance(c.getId());

    builder.currentWorkflowInstance(convert(c, currentWorkflowInstance, workflow));

    if (workflow) {
      builder.workflowInstances(workflowConfiguration.changesExtractor(c).getWorkflowInstances());
    }

    if (comments) {
      builder.comments(commentService.getComments(c.getId()).stream()
          .map(CommentDto::createFromComment)
          .collect(Collectors.toList()));
    }

    builder.tags(componentTagRepository.findOneByComponentId(c.getId()).stream()
        .map(ct -> ct.getTag().getLabel())
        .collect(Collectors.toList()));

    if (references) {
      List<ComponentReferenceDto> componentReferences = getComponentReferences(c, c.getCurrentWorkflowInstance());
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


  public List<ComponentReferenceDto> getComponentReferences(Component component, WorkflowInstance workflowInstance) {
    return convertReferences(componentReferenceRepository.findAllBySourceIdAndSourceWorkflowInstanceId(component.getId(), workflowInstance.getId()));
  }

  public List<ComponentReferenceDto> getComponentUsePoints(Component component, WorkflowInstance workflowInstance) {
    return convertReferences(componentReferenceRepository.findAllByTargetIdAndTargetWorkflowInstanceId(component.getId(), workflowInstance.getId()));
  }

  public List<ComponentReferenceDto> convertReferences(List<ComponentReference> refs) {

    return refs.stream()
        .map(r -> newComponentReferenceDto()
            .id(r.getId())
            .source(toDto(r.getSource(), false, false, false, false))
            .sourceWorkflowInstance(convert(r.getSource(), r.getSourceWorkflowInstance(), false))
            .target(toDto(r.getTarget(), false, false, false, false))
            .targetWorkflowInstance(convert(r.getTarget(), r.getTargetWorkflowInstance(), false))
            .build()
        ).collect(Collectors.toList());
  }





  public WorkflowInstanceDto convert(Component component, WorkflowInstance workflowInstance, boolean statuses) {

    ImmutableWorkflowInstanceDto.Builder workflowInstanceBuilder = WorkflowInstanceDto.newBuilderFromWorkflowInstance(workflowInstance);

    workflowInstanceBuilder.currentWorkflowStatus(
        newBuilderFromWorkflowStatus(workflowStatusRepository.findLatestWorkflowStatusByWorkflowInstanceId(workflowInstance.getId())).build());

    if (statuses) {

      List<WorkflowStatus> workflowStatuses = workflowStatusRepository.findAllByWorkflowInstanceId(workflowInstance.getId(), new Sort(Sort.Direction.ASC, "id"));

      List<Pair<WorkflowStatus, List<ChangeDto>>> componentServiceChanges = workflowConfiguration.changesExtractor(component).getChanges(workflowStatuses);

      workflowInstanceBuilder.workflowStatuses(componentServiceChanges.stream()
          .map(s -> WorkflowStatusDto.newBuilderFromWorkflowStatus(s.getLeft()).changes(s.getRight()).build())
          .collect(Collectors.toList()));
    }

    return workflowInstanceBuilder.build();

  }

  public ComponentVersionDto toComponentVersionDto(Component c, WorkflowInstance workflowInstance, boolean content, boolean statuses, boolean references, Boolean usePoints){
    ImmutableComponentVersionDto.Builder builder = ComponentVersionDto.newBuilderFromComponent(c);


    if (c.getType() != ComponentType.RESOURCE) {
      Pair<String, String> contentPair = componentService.generateContent(c);

      if (content) {
        builder.content(contentPair.getLeft());
      }
      builder.summary(contentPair.getRight());
    }

    builder.workflowInstance(convert(c, workflowInstance, statuses));

    builder.tags(componentTagRepository.findOneByComponentId(c.getId()).stream()
        .map(ct -> ct.getTag().getLabel())
        .collect(Collectors.toList()));

    if (references) {
      List<ComponentReferenceDto> componentReferences = getComponentReferences(c, workflowInstance);
      builder.componentReferences(componentReferences);
    } else {
      builder.coverageStatus(c.getCoverageStatus());
    }

    if (usePoints){
      List<ComponentReference> componentUsePoints = componentService.findUsePoints(c.getId(), workflowInstance.getId());
      builder.componentUsePoints(convertReferences(componentUsePoints));
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



}
