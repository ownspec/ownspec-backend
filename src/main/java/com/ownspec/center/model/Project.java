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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * Created by lyrold on 23/08/2016.
 */
@Data
@Entity
public class Project implements Persistable<Long>, Auditable<User> {

  @Id
  @GeneratedValue
  private Long id;
  private String title;
  private String description;

  @CreatedDate
  protected Instant createdDate;
  @ManyToOne
  @CreatedBy
  protected User createdUser;
  @LastModifiedDate
  protected Instant lastModifiedDate;
  @ManyToOne
  @LastModifiedBy
  protected User lastModifiedUser;

  @ManyToOne
  protected User manager;

  @Override
  @Transient
  public boolean isNew() {
    return null == getId();
  }
}
