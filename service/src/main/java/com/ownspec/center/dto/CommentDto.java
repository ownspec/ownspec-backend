package com.ownspec.center.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.dto.user.UserDto;
import com.ownspec.center.model.component.Comment;
import org.immutables.value.Value;

import java.time.Instant;
import javax.annotation.Nullable;

/**
 * Created by nlabrot on 16/10/16.
 */
@Value.Immutable
@Value.Style(builder = "newCommentDto")
@JsonSerialize(as = ImmutableCommentDto.class)
@JsonDeserialize(as = ImmutableCommentDto.class)
public interface CommentDto {

  @Nullable
  Long getId();

  String getValue();

  @Nullable
  Instant getCreatedDate();

  @Nullable
  UserDto getCreatedUser();


  static CommentDto createFromComment(Comment user) {
    return ImmutableCommentDto.newCommentDto()
        .id(user.getId())
        .createdUser(UserDto.fromUser(user.getCreatedUser()))
        .createdDate(user.getCreatedDate())
        .value(user.getValue())
        .build();
  }
}
