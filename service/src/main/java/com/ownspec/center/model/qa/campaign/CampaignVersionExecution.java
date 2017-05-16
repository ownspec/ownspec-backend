package com.ownspec.center.model.qa.campaign;

import com.ownspec.center.model.MainSequenceConstants;
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
 * <p>
 * An execution of a CampaignVersion
 */
@Data
@Entity
@Table(name = "QA_CAMPAIGN_VERSION_EXEC")
public class CampaignVersionExecution {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "QA_CAMPAIGN_VERSION_DEF")
  private CampaignVersionDefinition campaignVersionDefinition;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "WORKFLOW_INSTANCE_ID")
  private WorkflowInstance workflowInstance;
}
