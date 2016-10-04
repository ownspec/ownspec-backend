package com.ownspec.center.dto;

import javax.annotation.Nullable;

import java.time.Instant;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by nlabrot on 04/10/16.
 */
@Value.Immutable
@Value.Style(builder = "newProjectDto")
@JsonSerialize(as = ImmutableProjectDto.class)
@JsonDeserialize(as = ImmutableProjectDto.class)
public interface ProjectDto {

    @Nullable
    Long getId();

    String getTitle();

    @Nullable
    String getDescription();


    @Nullable
    Instant getCreatedDate();

    @Nullable
    UserDto getCreatedUser();
}
