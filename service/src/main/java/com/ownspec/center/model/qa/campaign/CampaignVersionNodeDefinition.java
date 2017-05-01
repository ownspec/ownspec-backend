package com.ownspec.center.model.qa.campaign;

import com.ownspec.center.model.MainSequenceConstants;
import com.ownspec.center.model.qa.testcase.TestCaseVersionDefinition;
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
@Table(name = "QA_CAMPAIGN_VERSION_NODE")
public class CampaignVersionNodeDefinition {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;

  // Sibling order
  private int order;

  // owner
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "QA_CAMPAIGN_VERSION_DEF_ID")
  private CampaignVersionDefinition campaignVersionDefinition;

  // parent node
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "QA_CAMPAIGN_VERSION_NODE_DEF_ID")
  private CampaignVersionNodeDefinition parent;

  // if this node is a leaf, the associated testcase version, otherwise null
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "QA_TEST_CASE_VERSION_DEF_ID")
  private TestCaseVersionDefinition testCaseVersionDefinition;

  // filled only if this node is not a leaf, it could be better to normalize into 2 tables, one for node with a title, one for leaf with a testcase
  @Column(name = "TITLE")
  protected String title;
}
