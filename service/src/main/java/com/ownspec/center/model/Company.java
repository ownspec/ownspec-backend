package com.ownspec.center.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
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
public class Company {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;

  @Column(name = "NAME") private String name;
  @Column(name = "ADDRESS") private String address;
  @Column(name = "PHONE") private String phone;
  @Column(name = "FAX") private String fax;
  @Column(name = "CONTACT_EMAIL") private String contactEmail;
  @Column(name = "WEBSITE") private String website;
  @Column(name = "REGISTRATION_NUMBER") private String registrationNumber;
  @Column(name = "HOST") private boolean host;
}
