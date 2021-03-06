package com.ownspec.center.dto.component;

import static com.ownspec.center.dto.component.ImmutableComponentDto.newComponentDto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.dto.user.UserDto;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;
import org.immutables.value.Value;

import java.time.Instant;
import javax.annotation.Nullable;

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

  ComponentType getType();

  @Nullable
  Instant getCreatedDate();

  @Nullable
  UserDto getCreatedUser();

  @Nullable
  Instant getLastModifiedDate();

  @Nullable
  UserDto getLastModifiedUser();

  static ImmutableComponentDto.Builder newBuilderFromComponent(Component c) {
    return newComponentDto()
        .id(c.getId())
        .projectId(c.getProject() != null ? c.getProject().getId() : null)

        .type(c.getType())
        .createdDate(c.getCreatedDate())
        .createdUser(UserDto.fromUser(c.getCreatedUser()));
  }
}
