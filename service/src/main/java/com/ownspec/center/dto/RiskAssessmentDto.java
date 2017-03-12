package com.ownspec.center.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.riskassessment.FailureImpactLevel;
import com.ownspec.center.model.riskassessment.FrequencyOfUse;
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
  FrequencyOfUse getFrequencyOfUse();

  @Nullable
  FailureImpactLevel getFailureImpactLevel();

  @Nullable
  Double getAcceptableFailureRate();


  static ImmutableRiskAssessmentDto createFromRiskAssessment(RiskAssessment source) {
    return ImmutableRiskAssessmentDto.newRiskAssessmentDto()
        .id(source.getId())
        .frequencyOfUse(source.getFrequencyOfUse())
        .failureImpactLevel(source.getFailureImpactLevel())
        .acceptableFailureRate(source.getAcceptableFailureRate())
        .build();
  }

}
