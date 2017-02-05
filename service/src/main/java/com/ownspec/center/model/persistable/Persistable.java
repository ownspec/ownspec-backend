package com.ownspec.center.model.persistable;

import javax.persistence.Transient;

/**
 * Created by nlabrot on 05/02/17.
 */
public interface Persistable extends org.springframework.data.domain.Persistable<Long> {

  @Override
  @Transient
  default boolean isNew(){
    return null == getId();
  }
}
