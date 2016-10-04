package com.ownspec.center.dto;

import javax.annotation.Nullable;
import java.time.Instant;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.workflow.Status;

/**
 * Created by nlabrot on 27/09/16.
 */
@Value.Immutable
@Value.Style(builder = "newChangeDto")
@JsonSerialize(as = ImmutableChangeDto.class)
@JsonDeserialize(as = ImmutableChangeDto.class)
public interface ChangeDto {
    String getRevision();

    Instant getDate();

    UserDto getUser();
}
