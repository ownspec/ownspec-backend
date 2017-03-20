package com.ownspec.center.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created on 18/03/2017
 *
 * @author lyrold
 */
@Data
@Entity
@Table(name = "CLIENT")
public class Client {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "DETAILS_ID")
  private Details details;

  @Enumerated(EnumType.STRING)
  @Column(name = "BUSINESS_INDUSTRY")
  private Business.Industry businessIndustry;

  @Column(name = "LOGO_URL")
  private String logoUrl;

  @Embedded
  private Social social;
}
