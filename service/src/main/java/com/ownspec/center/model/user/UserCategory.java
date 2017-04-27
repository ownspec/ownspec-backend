package com.ownspec.center.model.user;

import com.ownspec.center.dto.user.UserCategoryDto;
import com.ownspec.center.model.MainSequenceConstants;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by lyrold on 25/09/2016.
 */
@Data
@Entity
@Table(name = "USER_CATEGORY")
public class UserCategory {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;

  @Column(name = "NAME")
  private String name;

  @Column(name = "HOURLY_PRICE")
  private Double hourlyPrice;


  public static UserCategory createFromDto(UserCategoryDto userCategoryDto) {
    UserCategory userCategory = new UserCategory();
    userCategory.setId(userCategoryDto.getId());
    userCategory.setName(userCategoryDto.getName());
    userCategory.setHourlyPrice(userCategoryDto.getHourlyPrice());
    return userCategory;

  }
}
