package com.ownspec.center.service;

import static com.ownspec.center.util.OsUtils.mergeWithNotNullProperties;

import com.ownspec.center.dto.RiskAssessmentDto;
import com.ownspec.center.model.component.ComponentVersion;
import com.ownspec.center.model.riskassessment.RiskAssessment;
import com.ownspec.center.repository.RiskAssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created on 11/03/2017
 *
 * @author lyrold
 */
@Service
public class RiskAssessmentService {

  @Autowired
  private RiskAssessmentRepository riskAssessmentRepository;

  public void addOrUpdate(RiskAssessmentDto source, ComponentVersion componentVersion) {
    RiskAssessment riskAssessment = findOne(componentVersion);

    if (riskAssessment != null) {
      mergeWithNotNullProperties(source, riskAssessment);
    } else {
      riskAssessment = new RiskAssessment();
      riskAssessment.setComponentVersion(componentVersion);
      riskAssessment.setFailureImpactLevel(source.getFailureImpactLevel());
      riskAssessment.setFrequencyOfUse(source.getFrequencyOfUse());
      riskAssessment.setAcceptableFailureRate(source.getAcceptableFailureRate());
    }
    riskAssessmentRepository.save(riskAssessment);
  }

  public RiskAssessment findOne(ComponentVersion componentVersion) {
    return riskAssessmentRepository.findOneByComponentVersionId(componentVersion.getId());
  }
}
