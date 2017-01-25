package com.ownspec.center.model.component;

import static org.eclipse.jgit.lib.ObjectChecker.type;

import com.ownspec.center.model.DistributionLevel;
import com.ownspec.center.model.Project;
import com.ownspec.center.model.Tag;
import com.ownspec.center.model.audit.Auditable;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.workflow.WorkflowInstance;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * Created by lyrold on 23/08/2016.
 */
@Data
@Entity
public class ComponentTag implements Auditable<User>, Persistable<Long> {

  @Id
  @GeneratedValue
  protected Long id;

  // TODO: tag.id and component.id composed the ComponentTag id
  @ManyToOne(fetch = FetchType.EAGER)
  private Tag tag;

  @ManyToOne(fetch = FetchType.EAGER)
  private Component component;

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


  @Override
  @Transient
  public boolean isNew() {
    return null == getId();
  }

  @Transient
  public boolean isRequirement() {
    return ComponentType.REQUIREMENT.equals(type);
  }

}
