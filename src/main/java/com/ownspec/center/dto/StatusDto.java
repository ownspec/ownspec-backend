package com.ownspec.center.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.workflow.Status;
import org.immutables.value.Value;

/**
 * Created by nlabrot on 21/10/16.
 */
@Value.Immutable
@Value.Style(builder = "newStatusDto")
@JsonSerialize(as = ImmutableStatusDto.class)
@JsonDeserialize(as = ImmutableStatusDto.class)
public interface StatusDto {

  String getName();

  boolean isEditable();

  static StatusDto createFromStatus(Status status) {
    return ImmutableStatusDto.newStatusDto()
        .name(status.name())
        .isEditable(status.isEditable())
        .build();
  }
}
