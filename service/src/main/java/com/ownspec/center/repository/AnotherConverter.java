package com.ownspec.center.repository;

import com.ownspec.center.dto.StatusDto;
import com.ownspec.center.dto.component.ImmutableComponentDto;
import com.ownspec.center.dto.component.ImmutableComponentVersionDto;
import com.ownspec.center.dto.user.ImmutableUserDto;
import com.ownspec.center.dto.workflow.ImmutableWorkflowInstanceDto;
import com.ownspec.center.dto.workflow.ImmutableWorkflowStatusDto;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.CoverageStatus;
import com.ownspec.center.model.component.DistributionLevel;
import com.ownspec.center.model.component.RequirementType;
import com.ownspec.center.model.tables.records.ComponentRecord;
import com.ownspec.center.model.tables.records.ComponentVersionRecord;
import com.ownspec.center.model.tables.records.OsuserRecord;
import com.ownspec.center.model.tables.records.WorkflowInstanceRecord;
import com.ownspec.center.model.tables.records.WorkflowStatusRecord;
import com.ownspec.center.model.workflow.Status;
import org.springframework.stereotype.Service;

/**
 * Created by nlabrot on 23/03/17.
 */
@Service
public class AnotherConverter {


  public ImmutableComponentDto.Builder convert(ComponentRecord r) {
    return ImmutableComponentDto.newComponentDto().id(r.getId())
        .projectId(r.getProjectId())
        .type(ComponentType.valueOf(r.getType()))
        .createdDate(r.getCreatedDate().toInstant())
        .lastModifiedDate(r.getLastModifiedDate().toInstant());
  }

  public ImmutableComponentVersionDto.Builder convert(ComponentRecord r, ComponentVersionRecord cv, OsuserRecord cvCreated, OsuserRecord cvUpdated, OsuserRecord cvAssignee) {
    ImmutableComponentVersionDto.Builder builder = ImmutableComponentVersionDto.newComponentVersionDto()
        .id(cv.getId())
        .componentId(r.getId())
        .projectId(r.getProjectId())
        .type(ComponentType.valueOf(r.getType()))
        .code(r.getCode())
        .codeNumber(r.getCodeNumber())
        .title(cv.getTitle())
        .createdUser(convert(cvCreated).build())
        .createdDate(cv.getCreatedDate().toInstant())
        .lastModifiedDate(cv.getLastModifiedDate().toInstant())
        .lastModifiedUser(convert(cvUpdated).build())
        .requiredTest(cv.getRequiredTest())
        .distributionLevel(valueOf(DistributionLevel.class, cv.getDistributionLevel()))
        .requirementType(valueOf(RequirementType.class, cv.getRequirementType()))
        .coverageStatus(valueOf(CoverageStatus.class, cv.getCoverageStatus()))
        .filename(cv.getFilename())
        .version(cv.getVersion())
        .gitReference(cv.getGitReference());


    if (cvAssignee.getId() != null) {
      builder.assignedTo(convert(cvAssignee).build());
    }
    return builder;
  }

  public ImmutableUserDto.Builder convert(OsuserRecord osuser) {
    return ImmutableUserDto.newUserDto()
        .id(osuser.getId())
        .email(osuser.getEmail())
        .username(osuser.getUsername());
  }


  public ImmutableWorkflowInstanceDto.Builder convert(WorkflowInstanceRecord record, WorkflowStatusRecord statusRecord) {
    return ImmutableWorkflowInstanceDto.newWorkflowInstanceDto()
        .id(record.getId())
        .currentWorkflowStatus(convert(statusRecord).build());
  }

  public ImmutableWorkflowStatusDto.Builder convert(WorkflowStatusRecord statusRecord) {
    return ImmutableWorkflowStatusDto.newWorkflowStatusDto()
        .id(statusRecord.getId())
        .createdDate(statusRecord.getCreatedDate().toInstant())
        .status(StatusDto.createFromStatus(Status.valueOf(statusRecord.getStatus())));
  }


  private <T extends Enum> T valueOf(Class<T> clazz, String value) {
    for (T t : clazz.getEnumConstants()) {
      if (t.name().equals(value)) {
        return t;
      }
    }
    return null;
  }

}
