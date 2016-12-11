package com.ownspec.center.model;

import com.ownspec.center.model.audit.Auditable;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.user.UserCategory;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created on 11/12/2016
 *
 * @author lyrold
 */
@Data
@Entity
public class EstimatedTime implements Auditable<User>, Persistable<Long> {

  @Id
  @GeneratedValue
  private Long id;

  @Embedded
  UserCategory userCategory;

  Double time;
  @Enumerated(EnumType.STRING)
  TimeUnit timeUnit;

  @CreatedDate
  Instant createdDate;

  @ManyToOne
  @CreatedBy
  User createdUser;

  @LastModifiedDate
  Instant lastModifiedDate;

  @ManyToOne
  @LastModifiedBy
  User lastModifiedUser;

  @ManyToOne
  Component component;

  @Override
  public boolean isNew() {
    return null == getId();
  }
}
