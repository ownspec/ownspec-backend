package com.ownspec.center.dto;

import static org.reflections.util.ConfigurationBuilder.build;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.workflow.Status;
import org.immutables.value.Value;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

  boolean isFinal();

  List<String> getTransitions();

  static StatusDto createFromStatus(Status status) {
    return ImmutableStatusDto.newStatusDto()
        .name(status.name())
        .isEditable(status.isEditable())
        .isFinal(status.isFinal())
        .transitions(status.getTransitions().stream().map(Status::name).collect(Collectors.toList()))
        .build();
  }

  static List<StatusDto> createFromStatuses(){
    return Arrays.stream(Status.values())
        .map(StatusDto::createFromStatus)
        .collect(Collectors.toList());
  }

}
