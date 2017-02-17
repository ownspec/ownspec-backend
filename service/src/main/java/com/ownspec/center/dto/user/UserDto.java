package com.ownspec.center.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.user.UserCategory;
import com.ownspec.center.model.user.UserGroup;
import com.ownspec.center.model.user.UserPreference;
import org.immutables.value.Value;

import java.time.Instant;
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
  String getEmail();

  @Nullable
  String getPassword();

  @Nullable
  String getRole();

  @Nullable
  String getFirstName();

  @Nullable
  String getLastName();

  @Nullable
  String getPhone();

  @Nullable
  String getMobile();

  @Nullable
  Instant getLastConnection();

  @Nullable
  UserPreference getUserPreference();

  @Nullable
  UserCategoryDto getCategory();

  @Nullable
  UserGroup getGroup();

  @Nullable
  Boolean getLoggedIn();

  @Nullable
  Boolean getEnabled();

  @Nullable
  Boolean getAccountNonExpired();

  @Nullable
  Boolean getAccountNonLocked();

  @Nullable
  Boolean getCredentialsNonExpired();

  @Nullable
  Boolean getEmpoweredSecret();

  static UserDto fromUser(User user) {
    return ImmutableUserDto.newUserDto()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .role(user.getRole())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .phone(user.getPhone())
        .mobile(user.getMobile())
        .lastConnection(user.getLastConnection())
        .userPreference(user.getPreference())
        .category(UserCategoryDto.fromUserCategory(user.getCategory()))
        .group(user.getGroup())
        .loggedIn(user.isLoggedIn())
        .enabled(user.isEnabled())
        .accountNonExpired(user.isAccountNonExpired())
        .accountNonLocked(user.isAccountNonLocked())
        .credentialsNonExpired(user.isCredentialsNonExpired())
        .empoweredSecret(user.isEmpoweredSecret())
        .build();
  }

}
