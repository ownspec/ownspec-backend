package com.ownspec.center.model.user;

import com.ownspec.center.model.Company;
import com.ownspec.center.model.MainSequenceConstants;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created on 12/02/2017
 *
 * @author lyrold
 */
@Data
@Entity
@Table(name = "USER_COMPANY")
public class UserCompany {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "COMPANY_ID")
  private Company company;

  @ManyToOne
  @JoinColumn(name = "USER_ID")
  private User user;

  private boolean isLegalRepresentative;

}
