package com.ownspec.center.model.workflow;

import com.ownspec.center.model.MainSequenceConstants;
import com.ownspec.center.model.audit.AbstractAuditable;
import com.ownspec.center.model.audit.Auditable;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.persistable.Persistable;
import com.ownspec.center.model.user.User;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import javax.jdo.annotations.Join;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by nlabrot on 24/09/16.
 */
@Data
@Entity
@Table(name = "WORKFLOW_INSTANCE")
public class WorkflowInstance extends AbstractAuditable implements Persistable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "COMPONENT_ID")
  private Component component;

  @Column(name = "VERSION")
  protected Long version;

  @Column(name = "GIT_REFERENCE")
  private String gitReference;
}
