package com.ownspec.center.model.component;

import com.ownspec.center.model.MainSequenceConstants;
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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by nlabrot on 05/02/17.
 */
@Data
@Entity
@Table(name = "COMPONENT_VERSION")
public class ComponentVersion extends AbstractAuditable implements Auditable<User>, Persistable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  protected Long id;

  @Column(name = "VERSION")
  protected String version;

  @Column(name = "TITLE")
  protected String title;

  @Column(name = "FILENAME")
  protected String filename;

  @Column(name = "MEDIA_TYPE")
  protected String mediaType;

  @Column(name = "GIT_REFERENCE")
  private String gitReference;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "COMPONENT_ID")
  private Component component;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "WORKFLOW_INSTANCE_ID")
  private WorkflowInstance workflowInstance;

  // TODO: does it make sense? editable will depend too on the workflow
  @Column(name = "EDITABLE", columnDefinition = "boolean default true")
  protected boolean editable;

  @Column(name = "REQUIRED_TEST", columnDefinition = "boolean default false")
  protected boolean requiredTest;

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

  public ComponentVersion(){

  }

  public ComponentVersion(ComponentVersion toCopy) {
    this.title = toCopy.title;
    this.filename = toCopy.filename;
    this.mediaType = toCopy.mediaType;
    this.gitReference = toCopy.gitReference;
    this.component = toCopy.component;
    this.editable = toCopy.editable;
    this.requiredTest = toCopy.requiredTest;
    this.distributionLevel = toCopy.distributionLevel;
    this.requirementType = toCopy.requirementType;
    this.coverageStatus = toCopy.coverageStatus;
  }

  @Override
  public String toString() {
    return "Component{" +
        "id=" + id +
        ", title='" + title + '\'' +
        ", filename='" + filename + '\'' +
        '}';
  }
}
