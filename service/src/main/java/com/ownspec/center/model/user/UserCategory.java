package com.ownspec.center.model.user;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Created by lyrold on 25/09/2016.
 */
@Data
@Embeddable
// TODO: should not be embedded
public class UserCategory {

  @Column(name = "CATEGORY")
  private String category;

  @Column(name = "HOURLY_VALUE")
  private Double hourlyValue;

  public UserCategory(){
  }

  public UserCategory(String category, Double hourlyValue){
    this.category = category;
    this.hourlyValue = hourlyValue;
  }

}
