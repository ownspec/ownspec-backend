package com.ownspec.center.model.company;

import com.ownspec.center.model.Business;
import com.ownspec.center.model.MainSequenceConstants;
import com.ownspec.center.model.Social;
import com.ownspec.center.model.audit.AbstractAuditable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created on 12/02/2017
 *
 * @author lyrold
 */

@Data
@Entity
@Table(name = "COMPANY")
public class Company extends AbstractAuditable{

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;

  @Column(name = "REGISTRATION_NUMBER")
  private String registrationNumber;

  @Column(name = "HOST")
  private boolean host;

  @Column(name = "LOGO_URL")
  private String logoUrl;

  @Enumerated(EnumType.STRING)
  @Column(name = "BUSINESS_INDUSTRY")
  private Business.Industry businessIndustry;

  @Embedded
  private Social social;
}
