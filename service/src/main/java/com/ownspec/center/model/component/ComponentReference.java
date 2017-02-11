package com.ownspec.center.model.component;

import com.ownspec.center.model.MainSequenceConstants;
import com.ownspec.center.model.audit.AbstractAuditable;
import com.ownspec.center.model.persistable.Persistable;
import com.ownspec.center.model.workflow.WorkflowInstance;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A reference is an oriented path between a source
 * <p>
 * Created by nlabrot on 23/09/16.
 */
@Data
@Entity
@Table(name = "COMPONENT_REFERENCE")
public class ComponentReference extends AbstractAuditable implements Persistable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "SOURCE_COMPONENT_VERSION_ID")
  private ComponentVersion source;

  @ManyToOne
  @JoinColumn(name = "TARGET_COMPONENT_VERSION_ID")
  private ComponentVersion target;
}
