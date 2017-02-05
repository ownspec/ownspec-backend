package com.ownspec.center.model.user;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created on 05/02/2017
 *
 * @author lyrold
 */
@Data
@Entity
public class UserCategory {

  @Id
  @GeneratedValue
  private Long id;

  private String name;
  private Double hourlyPrice;
}
