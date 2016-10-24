package com.ownspec.center.model.workflow;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.ownspec.center.model.audit.Auditable;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.user.User;
import lombok.Data;

/**
 * Created by nlabrot on 22/09/16.
 */
@Data
@Entity
public class WorkflowStatus implements Auditable<User> {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private WorkflowInstance workflowInstance;

  @Enumerated(EnumType.STRING)
  protected Status status;

  // WorkflowInstance which owns this WorkflowStatus
  @ManyToOne
  private WorkflowInstance currentWorkflowInstance;



  private String firstGitReference;
  private String lastGitReference;

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

}
