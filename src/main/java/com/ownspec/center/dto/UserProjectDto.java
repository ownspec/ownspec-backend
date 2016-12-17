package com.ownspec.center.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.user.UserComponent;
import com.ownspec.center.model.user.UserProject;
import org.immutables.value.Value;

import java.time.Instant;
import javax.annotation.Nullable;

/**
 * Created on 16/12/2016
 *
 * @author lyrold
 */
@Value.Immutable
@Value.Style(builder = "newUserProjectDto")
@JsonSerialize(as = ImmutableUserProjectDto.class)
@JsonDeserialize(as = ImmutableUserProjectDto.class)
public interface UserProjectDto {

  UserDto getUser();

  ProjectDto getProject();

  @Nullable
  Instant getLastModifiedDate();

  static UserProjectDto fromUserProject(UserProject up) {
    return ImmutableUserProjectDto.newUserProjectDto()
        .user(UserDto.createFromUser(up.getUser()))
        .project(ProjectDto.newBuilderFromProject(up.getProject()).build())
        .lastModifiedDate(up.getLastModifiedDate())
        .build();
  }
}
