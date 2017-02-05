package com.ownspec.center.model.audit;


import com.ownspec.center.model.user.User;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;


/**
 * Created by nlabrot on 26/09/16.
 */
@Data
@MappedSuperclass
public abstract class AbstractAuditable implements Auditable<User> {

  @CreatedDate
  @Column(name = "CREATED_DATE")
  protected Instant createdDate;

  @ManyToOne
  @CreatedBy
  @JoinColumn(name = "CREATED_USER_ID")
  protected User createdUser;

  @LastModifiedDate
  @Column(name = "LAST_MODIFIED_DATE")
  protected Instant lastModifiedDate;

  @ManyToOne
  @LastModifiedBy
  @JoinColumn(name = "LAST_MODIFIED_USER_ID")
  protected User lastModifiedUser;

  public Instant getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Instant createdDate) {
    this.createdDate = createdDate;
  }

  public User getCreatedUser() {
    return createdUser;
  }

  public void setCreatedUser(User createdUser) {
    this.createdUser = createdUser;
  }

  public Instant getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(Instant lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public User getLastModifiedUser() {
    return lastModifiedUser;
  }

  public void setLastModifiedUser(User lastModifiedUser) {
    this.lastModifiedUser = lastModifiedUser;
  }
}
