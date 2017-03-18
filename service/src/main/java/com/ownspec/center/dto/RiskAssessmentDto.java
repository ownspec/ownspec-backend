package com.ownspec.center.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.riskassessment.FailureImpactType;
import com.ownspec.center.model.riskassessment.FrequencyOfUse;
import com.ownspec.center.model.riskassessment.Level;
import com.ownspec.center.model.riskassessment.RiskAssessment;
import org.immutables.value.Value;

import javax.annotation.Nullable;

/**
 * Created on 10/03/2017
 *
 * @author lyrold
 */
@Value.Immutable
@Value.Style(builder = "newRiskAssessmentDto")
@JsonSerialize(as = ImmutableRiskAssessmentDto.class)
@JsonDeserialize(as = ImmutableRiskAssessmentDto.class)
public interface RiskAssessmentDto {

  @Nullable
  Long getId();

  @Nullable
  String getRiskDescription();

  @Nullable
  FrequencyOfUse getFrequencyOfUse();

  @Nullable
  Level getFailureProbability();

  @Nullable
  Level getFailureImpactLevel();

  @Nullable
  FailureImpactType getFailureImpactType();

  @Nullable
  String getFailureProcedure();

  static ImmutableRiskAssessmentDto createFromRiskAssessment(RiskAssessment source) {
    return ImmutableRiskAssessmentDto.newRiskAssessmentDto()
        .id(source.getId())
        .riskDescription(source.getRiskDescription())
        .frequencyOfUse(source.getFrequencyOfUse())
        .failureProbability(source.getFailureProbability())
        .failureImpactLevel(source.getFailureImpactLevel())
        .failureImpactType(source.getFailureImpactType())
        .failureProcedure(source.getFailureProcedure())
        .build();
  }

}
