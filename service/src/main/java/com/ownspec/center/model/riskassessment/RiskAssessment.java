package com.ownspec.center.model.riskassessment;

import com.ownspec.center.model.MainSequenceConstants;
import com.ownspec.center.model.component.ComponentVersion;
import com.ownspec.center.model.persistable.Persistable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created on 10/03/2017
 *
 * @author lyrold
 */

@Data
@Entity
@Table(name = "RISK_ASSESSMENT")
public class RiskAssessment implements Persistable {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;

  @OneToOne
  @JoinColumn(name = "COMPONENT_VERSION_ID")
  private ComponentVersion componentVersion;

  @Column(name = "RISK_DESCRIPTION")
  private String riskDescription;

  @Enumerated(EnumType.STRING)
  @Column(name = "FREQUENCY_OF_USE")
  private FrequencyOfUse frequencyOfUse;

  @Enumerated(EnumType.STRING)
  @Column(name = "FAILURE_PROBABILITY")
  private Level failureProbability;

  @Enumerated(EnumType.STRING)
  @Column(name = "FAILURE_IMPACT_LEVEL")
  private Level failureImpactLevel;

  @Enumerated(EnumType.STRING)
  @Column(name = "FAILURE_IMPACT_TYPE")
  private FailureImpactType failureImpactType;

  @Column(name = "FAILURE_PROCEDURE")
  private String failureProcedure;

}
