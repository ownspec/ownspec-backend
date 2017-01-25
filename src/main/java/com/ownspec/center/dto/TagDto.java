package com.ownspec.center.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.Tag;
import org.immutables.value.Value;

/**
 * Created by nlabrot on 18/01/17.
 */
@Value.Immutable
@Value.Style(builder = "newTagDto")
@JsonSerialize(as = ImmutableTagDto.class)
@JsonDeserialize(as = ImmutableTagDto.class)
public interface TagDto {

  Long getId();

  String getLabel();

  public static TagDto createFromTag(Tag tag) {
    return ImmutableTagDto.newTagDto()
        .id(tag.getId())
        .label(tag.getLabel())
        .build();
  }
}
