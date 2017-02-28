package com.ownspec.center.service.component;

import static com.ownspec.center.dto.ImmutableComponentReferenceDto.newComponentReferenceDto;
import static com.ownspec.center.dto.WorkflowStatusDto.newBuilderFromWorkflowStatus;

import com.ownspec.center.dto.ChangeDto;
import com.ownspec.center.dto.ComponentReferenceDto;
import com.ownspec.center.dto.ComponentVersionDto;
import com.ownspec.center.dto.EstimatedTimeDto;
import com.ownspec.center.dto.ImmutableComponentVersionDto;
import com.ownspec.center.dto.ImmutableWorkflowInstanceDto;
import com.ownspec.center.dto.user.UserComponentDto;
import com.ownspec.center.dto.user.UserDto;
import com.ownspec.center.dto.WorkflowInstanceDto;
import com.ownspec.center.dto.WorkflowStatusDto;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentReference;
import com.ownspec.center.model.component.ComponentVersion;
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


  public List<ComponentReferenceDto> getComponentReferences(ComponentVersion componentVersion) {
    return convertReferences(componentReferenceRepository.findAllBySourceId(componentVersion.getId()));
  }


  public List<ComponentReferenceDto> getComponentUsePoints(ComponentVersion component, WorkflowInstance workflowInstance) {
    return convertReferences(componentReferenceRepository.findAllByTargetId(component.getId()));
  }

  public List<ComponentReferenceDto> convertReferences(List<ComponentReference> refs) {

    return refs.stream()
        .map(r -> newComponentReferenceDto()
            .id(r.getId())
            .source(toComponentVersionDto(r.getSource(), false, false, false))
            .target(toComponentVersionDto(r.getTarget(), false, false, false))
            .build()
        ).collect(Collectors.toList());
  }





  public WorkflowInstanceDto convert(ComponentVersion component, WorkflowInstance workflowInstance, boolean statuses) {

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

  public ComponentVersionDto toComponentVersionDto(ComponentVersion cv, boolean statuses, boolean references, Boolean usePoints){
    ImmutableComponentVersionDto.Builder builder = ComponentVersionDto.newBuilderFromComponent(cv);

    Component c = cv.getComponent();

    builder.workflowInstance(convert(cv, cv.getWorkflowInstance(), statuses));

    builder.tags(componentTagRepository.findAllByComponentVersionId(cv.getId()).stream()
        .map(ct -> ct.getTag().getLabel())
        .collect(Collectors.toList()));

    builder.code(c.getCode());

    if (references) {
      builder.componentReferences(getComponentReferences(cv));
    }

    builder.coverageStatus(cv.getCoverageStatus());


    if (references || usePoints){
      List<ComponentReference> list = componentReferenceRepository.findAllByTargetId(cv.getId());
      builder.componentUsePoints(convertReferences(list));
    }


    if (c.isRequirement()) {
      builder.requirementType(cv.getRequirementType());
    }

    if (cv.getAssignedTo() != null) {
      builder.assignedTo(UserDto.fromUser(cv.getAssignedTo()));
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
