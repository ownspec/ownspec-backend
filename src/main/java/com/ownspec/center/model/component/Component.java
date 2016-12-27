package com.ownspec.center.model.component;

import com.ownspec.center.model.DistributionLevel;
import com.ownspec.center.model.EstimatedTime;
import com.ownspec.center.model.Project;
import com.ownspec.center.model.audit.Auditable;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.user.UserCategory;
import com.ownspec.center.model.workflow.WorkflowInstance;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * Created by lyrold on 23/08/2016.
 */
@Data
@Entity
public class Component implements Auditable<User>, Persistable<Long> {

  @Id
  @GeneratedValue
  protected Long id;
  protected String title;

  protected String filename;
  protected String mediaType;

  // WorkflowInstance which owns this WorkflowStatus
  @ManyToOne
  private WorkflowInstance currentWorkflowInstance;

  // Project which owns this WorkflowStatus
  @ManyToOne
  protected Project project;

//  @ElementCollection
//  @Embedded
//  protected List<UserCategoryEstimatedTime> userCategoriesEstimatedTime;

  @Enumerated(EnumType.STRING)
  protected ComponentType type;

  @Column(columnDefinition = "boolean default true")
  protected boolean editable;

  @CreatedDate
  protected Instant createdDate;

  @ManyToOne
  @CreatedBy
  protected User createdUser;

  @LastModifiedDate
  protected Instant lastModifiedDate;

  @ManyToOne
  @LastModifiedBy
  protected User lastModifiedUser;

  @Column(columnDefinition = "boolean default false")
  protected boolean requiredTest;

  @ManyToOne
  protected User assignedTo;

  @Enumerated(EnumType.STRING)
  protected DistributionLevel distributionLevel = DistributionLevel.INTERNAL;

  @Enumerated(EnumType.STRING)
  protected RequirementType requirementType;

  @Enumerated(EnumType.STRING)
  protected CoverageStatus coverageStatus = CoverageStatus.UNCOVERED;

  @Override
  @Transient
  public boolean isNew() {
    return null == getId();
  }

  @Transient
  public boolean isRequirement() {
    return ComponentType.REQUIREMENT.equals(type);
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
