package com.ownspec.center.dto;

import com.ownspec.center.model.DistributionLevel;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.CoverageStatus;
import com.ownspec.center.model.component.RequirementType;
import com.ownspec.center.model.riskassessment.FailureImpactLevel;
import com.ownspec.center.model.riskassessment.FrequencyOfUse;
import com.ownspec.center.model.workflow.Status;
import org.immutables.value.Value;

/**
 * Created on 05/03/2017
 *
 * @author lyrold
 */
@Value.Immutable
@Value.Style(builder = "newApplicationDto")
public interface ApplicationDto {

  DistributionLevel[] getDistributionLevels();

  Status[] getWorkflowStatuses();

  ComponentType[] getComponentTypes();

  RequirementType[] getRequirementTypes();

  CoverageStatus[] getCoverageStatuses();

  FrequencyOfUse[] getRiskAssessmentFrequenciesOfUser();

  FailureImpactLevel[] getFailureImpactLevels();


  static ApplicationDto toApplicationDto() {
    return ImmutableApplicationDto.newApplicationDto()
        .distributionLevels(DistributionLevel.values())
        .workflowStatuses(Status.values())
        .componentTypes(ComponentType.values())
        .requirementTypes(RequirementType.values())
        .coverageStatuses(CoverageStatus.values())
        .riskAssessmentFrequenciesOfUser(FrequencyOfUse.values())
        .failureImpactLevels(FailureImpactLevel.values())
        .build();
  }

}
