package com.ownspec.center.model.component;

import com.ownspec.center.model.DistributionLevel;
import com.ownspec.center.model.MainSequenceConstants;
import com.ownspec.center.model.Project;
import com.ownspec.center.model.audit.AbstractAuditable;
import com.ownspec.center.model.audit.Auditable;
import com.ownspec.center.model.persistable.Persistable;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.workflow.WorkflowInstance;
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
 * Created by lyrold on 23/08/2016.
 */
@Data
@Entity
@Table(name = "COMPONENT")
// TODO: to refactor and split a COMPONENT into COMPONENT and COMPONENT_VERSION
public class Component extends AbstractAuditable implements Auditable<User>, Persistable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  protected Long id;

  @Column(name = "TITLE")
  protected String title;

  @Column(name = "FILENAME")
  protected String filename;

  @Column(name = "MEDIA_TYPE")
  protected String mediaType;

  // WorkflowInstance which owns this WorkflowStatus
  @ManyToOne
  @JoinColumn(name = "CURRENT_WORKFLOW_INSTANCE_ID")
  private WorkflowInstance currentWorkflowInstance;

  // Project which owns this WorkflowStatus
  @ManyToOne
  @JoinColumn(name = "PROJECT_ID")
  protected Project project;


  @Enumerated(EnumType.STRING)
  @Column(name = "TYPE")
  protected ComponentType type;

  // TODO: does it make sense? editable will depend too on the workflow
  @Column(name = "EDITABLE", columnDefinition = "boolean default true")
  protected boolean editable;

  @Column(name = "REQUIRE_TEST", columnDefinition = "boolean default false")
  protected boolean requireTest;

  @ManyToOne
  @JoinColumn(name = "ASSIGNED_TO_USER_ID")
  protected User assignedTo;

  @Enumerated(EnumType.STRING)
  @Column(name = "DISTRIBUTION_LEVEL")
  protected DistributionLevel distributionLevel = DistributionLevel.INTERNAL;

  @Enumerated(EnumType.STRING)
  @Column(name = "REQUIREMENT_TYPE")
  protected RequirementType requirementType;

  @Enumerated(EnumType.STRING)
  @Column(name = "COVERAGE_STATUS")
  protected CoverageStatus coverageStatus = CoverageStatus.UNCOVERED;

  @Transient
  public boolean isRequirement() {
    return ComponentType.REQUIREMENT == type;
  }

  @Override
  public String toString() {
    return "Component{" +
        "id=" + id +
        ", title='" + title + '\'' +
        ", filename='" + filename + '\'' +
        ", type=" + type +
        '}';
  }
}
