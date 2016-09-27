package com.ownspec.center.dto;

import javax.annotation.Nullable;

import java.time.Instant;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by nlabrot on 24/09/16.
 */
@Value.Immutable
@Value.Style(builder = "newComponentDto")
@JsonSerialize(as = ImmutableComponentDto.class)
@JsonDeserialize(as = ImmutableComponentDto.class)
public interface ComponentDto {

    @Nullable
    Long getId();

    String getTitle();

    @Nullable
    Instant getCreatedDate();
    @Nullable
    UserDto getCreatedUser();
    @Nullable
    Instant getLastModifiedDate();
    @Nullable
    UserDto getLastModifiedUser();

    String getContent();

    @Nullable
    WorkflowInstanceDto getCurrentWorkflowInstance();

}
