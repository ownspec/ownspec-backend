package com.ownspec.center.model.qa.campaign;

import com.ownspec.center.model.MainSequenceConstants;
import com.ownspec.center.model.qa.testcase.TestCaseVersionExecution;
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
@Table(name = "QA_CAMPAIGN_VERSION_NODE_EXEC")
public class CampaignVersionExecutionNode {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;

  // owner
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "QA_CAMPAIGN_VERSION_EXEC_ID")
  private CampaignVersionExecution campaignVersionExecution;

  // the associated CampaignVersionNodeDefinition
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "QA_CAMPAIGN_VERSION_DEF_NODE_ID")
  private CampaignVersionDefinitionNode campaignVersionDefinitionNode;

  // the associated TestCaseVersionExecution
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "QA_TEST_CASE_VERSION_EXEC_ID")
  private TestCaseVersionExecution testCaseVersionExecution;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "WORKFLOW_INSTANCE_ID")
  private WorkflowInstance workflowInstance;
}
