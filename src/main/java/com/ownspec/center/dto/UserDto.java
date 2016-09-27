package com.ownspec.center.dto;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by nlabrot on 27/09/16.
 */
@Value.Immutable
@Value.Style(builder = "newUserDto")
@JsonSerialize(as = ImmutableUserDto.class)
@JsonDeserialize(as = ImmutableUserDto.class)
public interface UserDto {

    String getusername();
    String getEmail();
    String getFirstName();
    String getLastName();
    String getCompany();

}
