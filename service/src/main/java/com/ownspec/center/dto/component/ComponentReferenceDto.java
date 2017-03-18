package com.ownspec.center.dto.component;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.dto.AuditableDto;
import org.immutables.value.Value;

import javax.annotation.Nullable;

/**
 * Created by nlabrot on 24/09/16.
 */
@Value.Immutable
@Value.Style(builder = "newComponentReferenceDto")
@JsonSerialize(as = ImmutableComponentReferenceDto.class)
@JsonDeserialize(as = ImmutableComponentReferenceDto.class)
public interface ComponentReferenceDto extends AuditableDto {

  @Nullable
  Long getId();

  ComponentVersionDto getSource();

  ComponentVersionDto getTarget();
}
