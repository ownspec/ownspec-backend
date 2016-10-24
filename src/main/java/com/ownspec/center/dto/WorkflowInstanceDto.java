package com.ownspec.center.dto;

import static com.ownspec.center.model.user.QUser.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.model.workflow.WorkflowInstance;
import com.ownspec.center.model.workflow.WorkflowStatus;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Created by nlabrot on 27/09/16.
 */
@Value.Immutable
@Value.Style(builder = "newWorkflowInstanceDto")
@JsonSerialize(as = ImmutableWorkflowInstanceDto.class)
@JsonDeserialize(as = ImmutableWorkflowInstanceDto.class)
public interface WorkflowInstanceDto {

  Long getId();

  StatusDto getCurrentStatus();

  @Nullable
  Instant getCreatedDate();

  @Nullable
  UserDto getCreatedUser();


  @Nullable
  List<WorkflowStatusDto> getWorkflowStatuses();



  static ImmutableWorkflowInstanceDto.Builder newBuilderFromWorkflowInstance(WorkflowInstance workflowInstance) {
    ImmutableWorkflowInstanceDto.Builder builder = ImmutableWorkflowInstanceDto.newWorkflowInstanceDto()
        .id(workflowInstance.getId())
        .currentStatus(StatusDto.createFromStatus(workflowInstance.getCurrentStatus()))
        .createdDate(workflowInstance.getCreatedDate())
        .createdUser(UserDto.createFromUser(workflowInstance.getCreatedUser()));

    return builder;
  }
}
