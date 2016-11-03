package com.ownspec.center.model;

import com.ownspec.center.model.audit.Auditable;
import com.ownspec.center.model.user.User;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.Instant;
import java.util.Date;

/**
 * Created by lyrold on 02/10/2016.
 */
@Data
@Entity
public class Task implements Auditable<User>, Persistable<Long> {

  @Id
  @GeneratedValue
  private Long id;
  private String description;
  private Double progress;
  private Date deadline;
  @ManyToOne
  private User owner;
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


  @Override
  public boolean isNew() {
     return null == getId();
  }
}
