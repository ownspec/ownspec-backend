package com.ownspec.center.model.company;

import com.ownspec.center.model.Details;
import com.ownspec.center.model.MainSequenceConstants;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created on 18/03/2017
 *
 * @author lyrold
 */
@Data
@Entity
@Table(name = "COMPANY_DETAILS")
public class CompanyDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  private Long id;

  @OneToOne
  @JoinColumn(name = "COMPANY_ID")
  private Company company;

  @ManyToOne
  @JoinColumn(name = "DETAILS_ID")
  private Details details;
}
