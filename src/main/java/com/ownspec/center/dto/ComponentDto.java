package com.ownspec.center.dto;

import static com.ownspec.center.dto.ImmutableComponentDto.newComponentDto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.DistributionLevel;
import com.ownspec.center.model.EstimatedTime;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.CoverageStatus;
import com.ownspec.center.model.component.RequirementType;
import com.ownspec.center.model.user.UserCategory;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.List;
import java.util.Map;
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

  static ImmutableComponentDto.Builder newBuilderFromComponent(Component c) {
    return newComponentDto()
        .id(c.getId())
        .projectId(c.getProject() != null ? c.getProject().getId() : null)
        .title(c.getTitle())
        .type(c.getType())
        .createdDate(c.getCreatedDate())
        .createdUser(UserDto.createFromUser(c.getCreatedUser()))
        .requiredTest(c.isRequiredTest())
        .distributionLevel(c.getDistributionLevel())
        .coverageStatus(c.getCoverageStatus());
  }


}
