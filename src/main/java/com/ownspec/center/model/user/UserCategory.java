package com.ownspec.center.model.user;

import lombok.Data;

import javax.persistence.Embeddable;

/**
 * Created by lyrold on 25/09/2016.
 */
@Data
@Embeddable
public class UserCategory {
  private String category;
  private Double manDayValue;

  public UserCategory(){

  }

  public UserCategory(String category, Double manDayValue){
    this.category = category;
    this.manDayValue = manDayValue;
  }

}
