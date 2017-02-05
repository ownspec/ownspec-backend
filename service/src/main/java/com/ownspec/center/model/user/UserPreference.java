package com.ownspec.center.model.user;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Created by lyrold on 02/10/2016.
 */
@Data
@Embeddable
public class UserPreference {

  @Column(name = "LANGUAGE")
  private String language;

  @Column(name = "TIMEZONE")
  private String timezone;
}
