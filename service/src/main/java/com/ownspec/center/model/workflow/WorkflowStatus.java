package com.ownspec.center.model.workflow;

import com.ownspec.center.model.MainSequenceConstants;
import com.ownspec.center.model.audit.AbstractAuditable;
import com.ownspec.center.model.persistable.Persistable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by nlabrot on 22/09/16.
 */
@Data
@Entity
@Table(name = "WORKFLOW_STATUS")
public class WorkflowStatus extends AbstractAuditable implements Persistable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;

  @Column(name = "WSORDER")
  private int order;

  @ManyToOne
  @JoinColumn(name = "WORKFLOW_INSTANCE_ID")
  private WorkflowInstance workflowInstance;

  @Enumerated(EnumType.STRING)
  @Column(name = "STATUS")
  protected Status status;

  @Column(name = "REASON")
  private String reason;

  @Column(name = "FIRST_GIT_REFERENCE")
  private String firstGitReference;

  @Column(name = "LAST_GIT_REFERENCE")
  private String lastGitReference;

  @Transient
  public void updateGitReference(String hash) {
    if (getFirstGitReference() == null) {
      setFirstGitReference(hash);
      setLastGitReference(hash);
    } else {
      setLastGitReference(hash);
    }
  }

}
