package com.ownspec.center.dto.user;

import static com.ownspec.center.dto.user.ImmutableUserCategoryDto.newUserCategoryDto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.user.UserCategory;
import org.immutables.value.Value;

import javax.annotation.Nullable;

/**
 * Created by nlabrot on 17/02/17.
 */
@Value.Immutable
@Value.Style(builder = "newUserCategoryDto")
@JsonSerialize(as = ImmutableUserCategoryDto.class)
@JsonDeserialize(as = ImmutableUserCategoryDto.class)
public interface UserCategoryDto {

  @Nullable
  Long getId();

  String getName();

  Double getHourlyPrice();

  static UserCategoryDto fromUserCategory(UserCategory userCategory) {
    return newUserCategoryDto()
        .id(userCategory.getId())
        .name(userCategory.getName())
        .hourlyPrice(userCategory.getHourlyPrice())
        .build();
  }
}
