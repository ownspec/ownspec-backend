package com.ownspec.center.dto;

import java.time.Instant;
import javax.annotation.Nullable;

/**
 * Created by nlabrot on 09/02/17.
 */
public interface AuditableDto {

  @Nullable
  Instant getCreatedDate();

  @Nullable
  UserDto getCreatedUser();

  @Nullable
  Instant getLastModifiedDate();

  @Nullable
  UserDto getLastModifiedUser();
}
