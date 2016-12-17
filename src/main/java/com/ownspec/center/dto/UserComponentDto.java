package com.ownspec.center.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.user.UserComponent;
import org.immutables.value.Value;

import java.time.Instant;

/**
 * Created on 16/12/2016
 *
 * @author lyrold
 */
@Value.Immutable
@Value.Style(builder = "newUserComponentDto")
@JsonSerialize(as = ImmutableUserComponentDto.class)
@JsonDeserialize(as = ImmutableUserComponentDto.class)
public interface UserComponentDto {

  UserDto getUser();

  ComponentDto getComponent();

  Instant getLastModifiedDate();

  static UserComponentDto fromUserComponent(UserComponent uc) {
    return ImmutableUserComponentDto.newUserComponentDto()
        .user(UserDto.createFromUser(uc.getUser()))
        .component(ComponentDto.newBuilderFromComponent(uc.getComponent()).build())
        .lastModifiedDate(uc.getLastModifiedDate())
        .build();
  }

}
