package com.ownspec.center.dto;

import static com.ownspec.center.dto.ImmutableComponentVersionDto.newComponentVersionDto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.dto.user.UserComponentDto;
import com.ownspec.center.dto.user.UserDto;
import com.ownspec.center.model.DistributionLevel;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.ComponentVersion;
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
  Long getComponentId();

  @Nullable
  Long getProjectId();

  @Nullable
  String getCode();

  @Nullable
  Long getCodeNumber();

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
  List<ComponentReferenceDto> getComponentUsePoints();

  @Nullable
  Boolean getRequiredTest();

  List<EstimatedTimeDto> getEstimatedTimes();

  @Nullable
  UserDto getAssignedTo();

  @Nullable
  DistributionLevel getDistributionLevel();

  @Nullable
  RequirementType getRequirementType();

  @Nullable
  CoverageStatus getCoverageStatus();

  List<UserComponentDto> getComponentUsers();

  @Nullable
  String getUploadedFileId();

  @Nullable
  String getFilename();

  @Nullable
  String getVersion();

  @Nullable
  String getGitReference();

  List<String> getTags();

  @Nullable
  RiskAssessmentDto getRiskAssessment();

  static ImmutableComponentVersionDto.Builder newBuilderFromComponent(ComponentVersion c) {
    return newComponentVersionDto()
        .id(c.getId())
        .componentId(c.getComponent().getId())
        .version(c.getVersion())
        .projectId(c.getComponent().getProject() != null ? c.getComponent().getProject().getId() : null)
        .title(c.getTitle())
        .type(c.getComponent().getType())
        .code(c.getComponent().getCode())
        .codeNumber(c.getComponent().getCodeNumber())
        .createdDate(c.getCreatedDate())
        .createdUser(UserDto.fromUser(c.getCreatedUser()))
        .lastModifiedDate(c.getLastModifiedDate())
        .lastModifiedUser(UserDto.fromUser(c.getLastModifiedUser()))
        .requiredTest(c.isRequiredTest())
        .distributionLevel(c.getDistributionLevel())
        .coverageStatus(c.getCoverageStatus())
        .requirementType(c.getRequirementType())
        .gitReference(c.getGitReference());
  }
}
