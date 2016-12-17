package com.ownspec.center.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
  UserDto getCreatedUser();

  @Nullable
  UserDto getManager();

  @Nullable
  List<UserProjectDto> getProjectUsers();


  public static ImmutableProjectDto.Builder newBuilderFromProject(Project project) {
    return ImmutableProjectDto.newProjectDto()
        .id(project.getId())
        .title(project.getTitle())
        .description(project.getDescription())
        .createdDate(project.getCreatedDate())
        .createdUser(UserDto.createFromUser(project.getCreatedUser()));
  }

  public static ProjectDto fromProject(Project project) {
    return newBuilderFromProject(project).build();
  }
}
