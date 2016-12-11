package com.ownspec.center.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.EstimatedTime;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.user.UserCategory;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

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

  UserCategory getUserCategory();

  Double getTime();

  TimeUnit getTimeUnit();

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
        .createdUser(UserDto.createFromUser(source.getCreatedUser()))
        .lastModifiedUser(UserDto.createFromUser(source.getLastModifiedUser()))
        .lastModifiedDate(source.getLastModifiedDate())
        .userCategory(source.getUserCategory())
        .time(source.getTime())
        .timeUnit(source.getTimeUnit())
        .build();
  }
}
