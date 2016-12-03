package com.ownspec.center.dto;

import static com.ownspec.center.dto.ImmutableComponentDto.newComponentDto;

import javax.annotation.Nullable;

import java.time.Instant;
import java.util.List;

import com.ownspec.center.model.component.Component;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.Comment;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.workflow.Status;

/**
 * Created by nlabrot on 24/09/16.
 */
@Value.Immutable
@Value.Style(builder = "newComponentDto")
@JsonSerialize(as = ImmutableComponentDto.class)
@JsonDeserialize(as = ImmutableComponentDto.class)
public interface ComponentDto {

  @Nullable
  Long getId();

  @Nullable
  Long getProjectId();

  String getTitle();

  ComponentType getType();

  @Nullable
  Instant getCreatedDate();

  @Nullable
  UserDto getCreatedUser();

  @Nullable
  Instant getLastModifiedDate();

  @Nullable
  UserDto getLastModifiedUser();

  @Nullable
  String getContent();

  @Nullable
  String getSummary();

  @Nullable
  WorkflowStatusDto getCurrentWorkflowStatus();

  @Nullable
  List<WorkflowInstanceDto> getWorkflowInstances();

  @Nullable
  List<CommentDto> getComments();

  @Nullable
  List<ComponentReferenceDto> getComponentReferences();

  static ImmutableComponentDto.Builder newBuilderFromComponent(Component c){
    return newComponentDto()
        .id(c.getId())
        .projectId(c.getProject() != null ? c.getProject().getId() : null)
        .title(c.getTitle())
        .type(c.getType())
        .createdDate(c.getCreatedDate())
        .createdUser(UserDto.createFromUser(c.getCreatedUser()));
  }





}
