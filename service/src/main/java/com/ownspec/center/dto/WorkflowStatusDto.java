package com.ownspec.center.dto;

import javax.annotation.Nullable;

import java.time.Instant;
import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.workflow.WorkflowStatus;

/**
 * Created by nlabrot on 27/09/16.
 */
@Value.Immutable
@Value.Style(builder = "newWorkflowStatusDto")
@JsonSerialize(as = ImmutableWorkflowStatusDto.class)
@JsonDeserialize(as = ImmutableWorkflowStatusDto.class)
public interface WorkflowStatusDto {
  @Nullable
  Long getId();

  StatusDto getStatus();

  @Nullable
  String getReason();

  @Nullable
  Instant getCreatedDate();

  @Nullable
  UserDto getCreatedUser();

  List<ChangeDto> getChanges();


  static ImmutableWorkflowStatusDto.Builder newBuilderFromWorkflowStatus(WorkflowStatus workflowStatus) {
    ImmutableWorkflowStatusDto.Builder builder = ImmutableWorkflowStatusDto.newWorkflowStatusDto()
        .id(workflowStatus.getId())
        .status(StatusDto.createFromStatus(workflowStatus.getStatus()))
        .reason(workflowStatus.getReason())
        .createdDate(workflowStatus.getCreatedDate());

    if (workflowStatus.getCreatedUser() != null) {
      builder.createdUser(UserDto.fromUser(workflowStatus.getCreatedUser()));
    }

    return builder;
  }
}
