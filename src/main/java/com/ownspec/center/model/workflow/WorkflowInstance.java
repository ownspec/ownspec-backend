package com.ownspec.center.model.workflow;

import com.ownspec.center.model.audit.Auditable;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.user.User;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by nlabrot on 24/09/16.
 */
@Data
@Entity
public class WorkflowInstance implements Auditable<User> {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private Component component;

  protected Long version;

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

  private String gitReference;
}
