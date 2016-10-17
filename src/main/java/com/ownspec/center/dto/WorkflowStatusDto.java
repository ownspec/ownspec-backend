package com.ownspec.center.dto;

import javax.annotation.Nullable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import java.time.Instant;
import java.util.List;

import org.immutables.value.Value;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.model.workflow.WorkflowStatus;

/**
 * Created by nlabrot on 27/09/16.
 */
@Value.Immutable
@Value.Style(builder = "newWorkflowStatusDto")
@JsonSerialize(as = ImmutableWorkflowStatusDto.class)
@JsonDeserialize(as = ImmutableWorkflowStatusDto.class)
public interface WorkflowStatusDto {
  Long getId();

  Status getStatus();

  @Nullable
  Instant getCreatedDate();

  @Nullable
  UserDto getCreatedUser();

  List<ChangeDto> getChanges();


  static ImmutableWorkflowStatusDto.Builder newBuilderFromWorkflowStatus(WorkflowStatus user) {
    ImmutableWorkflowStatusDto.Builder builder = ImmutableWorkflowStatusDto.newWorkflowStatusDto()
        .id(user.getId())
        .status(user.getStatus())
        .createdDate(user.getCreatedDate());

    if (user.getCreatedUser() != null) {
      builder.createdUser(UserDto.createFromUser(user.getCreatedUser()));
    }

    return builder;
  }
}
