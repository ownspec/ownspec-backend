package com.ownspec.center.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.dto.user.UserDto;
import com.ownspec.center.model.Project;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Created by nlabrot on 04/10/16.
 */
@Value.Immutable
@Value.Style(builder = "newProjectDto")
@JsonSerialize(as = ImmutableProjectDto.class)
@JsonDeserialize(as = ImmutableProjectDto.class)
public interface ProjectDto {

  @Nullable
  Long getId();

  String getTitle();

  @Nullable
  String getDescription();

  @Nullable
  Instant getCreatedDate();

  @Nullable
  Instant getLastModifiedDate();

  @Nullable
  UserDto getCreatedUser();

  @Nullable
  UserDto getLastModifiedUser();

  @Nullable
  UserDto getManager();

  String getKey();

  @Nullable
  List<UserDto> getProjectUsers();


  public static ImmutableProjectDto.Builder newBuilderFromProject(Project project) {
    ImmutableProjectDto.Builder builder = ImmutableProjectDto.newProjectDto()
        .id(project.getId())
        .title(project.getTitle())
        .key(project.getComponentCodeCounter().getKey())
        .description(project.getDescription())
        .createdDate(project.getCreatedDate())
        .lastModifiedDate(project.getLastModifiedDate())
        .createdUser(UserDto.fromUser(project.getCreatedUser()))
        .lastModifiedUser(UserDto.fromUser(project.getLastModifiedUser()));

    if (project.getManager() != null) {
      builder.manager(UserDto.fromUser(project.getManager()));
    }
    return builder;
  }

  public static ProjectDto fromProject(Project project) {
    return newBuilderFromProject(project).build();
  }
}
