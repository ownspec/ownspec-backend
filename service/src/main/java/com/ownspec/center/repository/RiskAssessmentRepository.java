package com.ownspec.center.repository;

import com.ownspec.center.model.riskassessment.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 10/03/2017
 *
 * @author lyrold
 */
public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {

  RiskAssessment findOneByComponentVersionId(Long componentVersionId);
}
