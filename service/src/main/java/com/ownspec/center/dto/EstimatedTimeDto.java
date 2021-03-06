package com.ownspec.center.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.dto.user.UserCategoryDto;
import com.ownspec.center.dto.user.UserDto;
import com.ownspec.center.model.component.EstimatedTime;
import org.immutables.value.Value;

import java.time.Instant;
import javax.annotation.Nullable;

/**
 * Created on 11/12/2016
 *
 * @author lyrold
 */
@Value.Immutable
@Value.Style(builder = "newEstimatedTimeDto")
@JsonSerialize(as =ImmutableEstimatedTimeDto.class)
@JsonDeserialize(as= ImmutableEstimatedTimeDto.class)
public interface EstimatedTimeDto {

  @Nullable
  Long getId();

  UserCategoryDto getUserCategory();

  String getDuration();

  @Nullable
  Long getDurationInMs();

  @Nullable
  Instant getCreatedDate();

  @Nullable
  UserDto getCreatedUser();

  @Nullable
  Instant getLastModifiedDate();

  @Nullable
  UserDto getLastModifiedUser();


  static ImmutableEstimatedTimeDto createFromEstimatedTime(EstimatedTime source) {
    return ImmutableEstimatedTimeDto.newEstimatedTimeDto()
        .id(source.getId())
        .createdDate(source.getCreatedDate())
        .createdUser(UserDto.fromUser(source.getCreatedUser()))
        .lastModifiedUser(UserDto.fromUser(source.getLastModifiedUser()))
        .lastModifiedDate(source.getLastModifiedDate())
        .userCategory(UserCategoryDto.fromUserCategory(source.getUserCategory()))
        .duration(source.getDuration())
        .durationInMs(source.getDurationInMs())
        .build();
  }
}
