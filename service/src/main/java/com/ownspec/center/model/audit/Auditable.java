package com.ownspec.center.model.audit;


import java.time.Instant;


/**
 * Created by nlabrot on 26/09/16.
 */
public interface Auditable<U> {
  Instant getCreatedDate();

  U getCreatedUser();

  Instant getLastModifiedDate();

  U getLastModifiedUser();

  void setCreatedDate(Instant createdDate);

  void setCreatedUser(U createdUser);

  void setLastModifiedDate(Instant lastModifiedDate);

  void setLastModifiedUser(U lastModifiedUser);
}
