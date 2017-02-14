package com.ownspec.center.dto;

import java.time.Instant;

import com.ownspec.center.dto.user.UserDto;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by nlabrot on 27/09/16.
 */
@Value.Immutable
@Value.Style(builder = "newChangeDto")
@JsonSerialize(as = ImmutableChangeDto.class)
@JsonDeserialize(as = ImmutableChangeDto.class)
public interface ChangeDto {
  String getRevision();

  Instant getDate();

  UserDto getUser();
}
