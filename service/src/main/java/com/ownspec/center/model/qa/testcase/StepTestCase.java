package com.ownspec.center.model.qa.testcase;

import com.ownspec.center.model.MainSequenceConstants;
import com.ownspec.center.model.audit.AbstractAuditable;
import com.ownspec.center.model.persistable.Persistable;
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
 * Created by nlabrot on 28/04/17.
 */
@Data
@Entity
@Table(name = "QA_STEP_TEST_CASE")
public class StepTestCase extends AbstractAuditable implements Persistable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;

  // Sibling step order
  private int order;

  @ManyToOne
  @JoinColumn(name = "TEST_CASE_ID")
  private TestCaseVersionDefinition testCase;

  @Column(name = "TITLE")
  protected String title;

}
