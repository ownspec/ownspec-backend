package com.ownspec.center.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.component.ComponentType;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Created by nlabrot on 24/09/16.
 */
@Value.Immutable
@Value.Style(builder = "newComponentReferenceDto")
@JsonSerialize(as = ImmutableComponentReferenceDto.class)
@JsonDeserialize(as = ImmutableComponentReferenceDto.class)
public interface ComponentReferenceDto {

  @Nullable
  Long getId();

  ComponentDto getSource();
  WorkflowInstanceDto getSourceWorkflowInstance();

  ComponentDto getTarget();
  WorkflowInstanceDto getTargetWorkflowInstance();

  @Nullable
  Instant getCreatedDate();

  @Nullable
  UserDto getCreatedUser();

  @Nullable
  Instant getLastModifiedDate();

  @Nullable
  UserDto getLastModifiedUser();
}
