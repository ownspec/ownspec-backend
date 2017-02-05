package com.ownspec.center.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.user.UserCategory;
import org.immutables.value.Value;

import javax.annotation.Nullable;

/**
 * Created by nlabrot on 27/09/16.
 */
@Value.Immutable
@Value.Style(builder = "newUserDto")
@JsonSerialize(as = ImmutableUserDto.class)
@JsonDeserialize(as = ImmutableUserDto.class)
public interface UserDto {

  @Nullable
  Long getId();

  String getUsername();

  @Nullable
  String getPassword();

  @Nullable
  String getFirstName();

  @Nullable
  String getLastName();

  @Nullable
  String getEmail();

  @Nullable
  String getRole();

  @Nullable
  UserCategory getCategory();

  static UserDto fromUser(User user) {
    return ImmutableUserDto.newUserDto()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .role(user.getRole())
        .category(user.getCategory())
        .build();
  }

}
