package com.ownspec.center.service.component;

import static com.ownspec.center.dto.ComponentDto.newBuilderFromComponent;
import static com.ownspec.center.dto.ImmutableComponentReferenceDto.newComponentReferenceDto;
import static com.ownspec.center.dto.WorkflowInstanceDto.newBuilderFromWorkflowInstance;
import static com.ownspec.center.dto.WorkflowStatusDto.newBuilderFromWorkflowStatus;

import com.ownspec.center.dto.CommentDto;
import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.dto.ComponentReferenceDto;
import com.ownspec.center.dto.ImmutableComponentDto;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.repository.component.ComponentReferenceRepository;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;
import com.ownspec.center.service.CommentService;
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


  public ComponentDto toDto(Long id, boolean content, boolean workflow, boolean comments, boolean references) {
    return toDto(componentService.findOne(id), content, workflow, comments, references);
  }

  public ComponentDto toDto(Component c, boolean content, boolean workflow, boolean comments, boolean references) {
    ImmutableComponentDto.Builder builder = newBuilderFromComponent(c);

    Pair<String, String> contentPair = componentService.generateContent(c);

    if (content) {
      builder.content(contentPair.getLeft());
    }

    builder.summary(contentPair.getRight());

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
      builder.componentReferences(getComponentReferences(c));
    }

    return builder.build();
  }




  public List<ComponentReferenceDto> getComponentReferences(Component component) {

    return componentReferenceRepository.findAllBySourceIdAndSourceWorkflowInstanceId(component.getId(), component.getCurrentWorkflowInstance().getId())
        .stream()
        .map(r -> newComponentReferenceDto()
            .source(toDto(r.getSource() , false,false, false, false))
            .sourceWorkflowInstance(
                newBuilderFromWorkflowInstance(r.getSourceWorkflowInstance())
                    .currentWorkflowStatus(newBuilderFromWorkflowStatus(workflowStatusRepository.findLatestWorkflowStatusByWorkflowInstanceId(r.getSourceWorkflowInstance().getId())).build())
                    .build()
            )

            .target(toDto(r.getTarget() , false,false, false, false))
            .targetWorkflowInstance(
                newBuilderFromWorkflowInstance(r.getTargetWorkflowInstance())
                    .currentWorkflowStatus(newBuilderFromWorkflowStatus(workflowStatusRepository.findLatestWorkflowStatusByWorkflowInstanceId(r.getTargetWorkflowInstance().getId())).build())
                    .build())
            .build()
        ).collect(Collectors.toList());
  }
}
