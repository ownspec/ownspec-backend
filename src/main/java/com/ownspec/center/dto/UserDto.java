package com.ownspec.center.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.user.User;
import org.immutables.value.Value;

/**
 * Created by nlabrot on 27/09/16.
 */
@Value.Immutable
@Value.Style(builder = "newUserDto")
@JsonSerialize(as = ImmutableUserDto.class)
@JsonDeserialize(as = ImmutableUserDto.class)
public interface UserDto {

  String getUsername();

  String getPassword();

  String getFirstName();

  String getLastName();

  String getEmail();

  String getRole();


  static UserDto createFromUser(User user) {
    return ImmutableUserDto.newUserDto()
        .username(user.getUsername())
        .password(user.getPassword()) //todo: TBC
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .role(user.getRole())
        .build();


  }

}
