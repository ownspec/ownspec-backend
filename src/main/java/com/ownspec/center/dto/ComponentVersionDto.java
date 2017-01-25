package com.ownspec.center.dto;

import static com.ownspec.center.dto.ImmutableComponentVersionDto.newComponentVersionDto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.DistributionLevel;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.CoverageStatus;
import com.ownspec.center.model.component.RequirementType;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Created by nlabrot on 22/01/17.
 */
@Value.Immutable
@Value.Style(builder = "newComponentVersionDto")
@JsonSerialize(as =ImmutableComponentVersionDto.class)
@JsonDeserialize(as= ImmutableComponentVersionDto.class)
public interface ComponentVersionDto {

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
  WorkflowInstanceDto getWorkflowInstance();

  @Nullable
  List<ComponentReferenceDto> getComponentReferences();

  @Nullable
  Boolean getRequiredTest();

  @Nullable
  List<EstimatedTimeDto> getEstimatedTimes();

  @Nullable
  UserDto getAssignedTo();

  @Nullable
  DistributionLevel getDistributionLevel();

  @Nullable
  RequirementType getRequirementType();

  @Nullable
  CoverageStatus getCoverageStatus();

  @Nullable
  List<UserComponentDto> getComponentUsers();

  @Nullable
  String getUploadedFileId();

  @Nullable
  String getFilename();

  List<String> getTags();

  static ImmutableComponentVersionDto.Builder newBuilderFromComponent(Component c) {
    return newComponentVersionDto()
        .id(c.getId())
        .projectId(c.getProject() != null ? c.getProject().getId() : null)
        .title(c.getTitle())
        .type(c.getType())
        .createdDate(c.getCreatedDate())
        .createdUser(UserDto.fromUser(c.getCreatedUser()))
        .requiredTest(c.isRequiredTest())
        .distributionLevel(c.getDistributionLevel())
        .coverageStatus(c.getCoverageStatus())
        .requirementType(c.getRequirementType());
  }
}
