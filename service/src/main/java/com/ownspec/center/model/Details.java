package com.ownspec.center.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created on 18/03/2017
 *
 * @author lyrold
 */
@Data
@Entity
@Table(name = "DETAILS")
public class Details {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;

  @Column(name = "NAME") private String name;
  @Column(name = "ADDRESS") private String address;
  @Column(name = "POSTAL_CODE") private String zipCode;
  @Column(name = "COUNTRY") private String country;

  @Column(name = "PHONE") private String phone;
  @Column(name = "FAX") private String fax;
  @Column(name = "EMAIL") private String email;
  @Column(name = "WEBSITE") private String website;

  @Column(name = "GITHUB") private String github;
  @Column(name = "LINKEDIN") private String linkedin;
  @Column(name = "TWITTER") private String twitter;
  @Column(name = "FACEBOOK") private String facebook;
  @Column(name = "GOOGLE") private String google;

  @Column(name = "BILLING_CURRENCY") private String billingCurrency; //todo Use http://fixer.io/ to get all the currencies with exchange rates
  @Column(name = "IS_DEFAULT") private boolean isDefault;

}
