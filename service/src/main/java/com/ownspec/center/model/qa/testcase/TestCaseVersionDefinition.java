package com.ownspec.center.model.qa.testcase;

import com.ownspec.center.model.MainSequenceConstants;
import com.ownspec.center.model.audit.AbstractAuditable;
import com.ownspec.center.model.persistable.Persistable;
import com.ownspec.center.model.qa.QaComponent;
import com.ownspec.center.model.workflow.WorkflowInstance;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by nlabrot on 28/04/17.
 */
@Data
@Entity
@Table(name = "QA_TEST_CASE_VERSION_DEF")
public class TestCaseVersionDefinition extends AbstractAuditable implements Persistable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;


  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "QA_COMPONENT_ID")
  private QaComponent component;

  @Column(name = "TITLE")
  protected String title;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "WORKFLOW_INSTANCE_ID")
  private WorkflowInstance workflowInstance;
}
