package com.ownspec.center.model.company;

import com.ownspec.center.model.Client;
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
 * Created on 18/03/2017
 *
 * @author lyrold
 */
@Data
@Entity
@Table(name = "COMPANY_CLIENT")
public class CompanyClient {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "CLIENT_ID")
  private Client client;

  @ManyToOne
  @JoinColumn(name = "COMPANY_ID")
  private Company company;
}
