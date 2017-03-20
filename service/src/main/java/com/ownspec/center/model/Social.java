package com.ownspec.center.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Created on 19/03/2017
 *
 * @author lyrold
 */
@Data
@Embeddable
public class Social {

  @Column(name = "GITHUB") private String github;
  @Column(name = "LINKEDIN") private String linkedin;
  @Column(name = "TWITTER") private String twitter;
  @Column(name = "FACEBOOK") private String facebook;
  @Column(name = "GOOGLE") private String google;

}
