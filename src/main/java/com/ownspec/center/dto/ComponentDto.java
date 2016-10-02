package com.ownspec.center.dto;

import javax.annotation.Nullable;

import java.time.Instant;
import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.workflow.Status;

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

    ComponentType getType();

    @Nullable
    Instant getCreatedDate();

    @Nullable
    UserDto getCreatedUser();

    @Nullable
    Instant getLastModifiedDate();

    @Nullable
    UserDto getLastModifiedUser();
    @Nullable
    String getContent();

    @Nullable
    Status getCurrentStatus();

    @Nullable
    List<WorkflowStatusDto> getWorkflowStatuses();

}
