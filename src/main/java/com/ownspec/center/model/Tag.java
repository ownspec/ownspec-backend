package com.ownspec.center.model;

import com.ownspec.center.model.audit.Auditable;
import com.ownspec.center.model.user.User;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by nlabrot on 29/12/16.
 */
@Data
@Entity
public class Tag implements Auditable<User>, Persistable<Long> {

  @Id
  @GeneratedValue
  private Long id;

  @Column(unique = true)
  private String label;

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

  @Override
  public boolean isNew() {
    return null == getId();
  }
}
