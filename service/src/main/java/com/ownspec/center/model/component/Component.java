package com.ownspec.center.model.component;

import com.ownspec.center.model.MainSequenceConstants;
import com.ownspec.center.model.Project;
import com.ownspec.center.model.audit.AbstractAuditable;
import com.ownspec.center.model.audit.Auditable;
import com.ownspec.center.model.persistable.Persistable;
import com.ownspec.center.model.user.User;
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

  // Project which owns this component, if any
  @ManyToOne
  @JoinColumn(name = "PROJECT_ID")
  protected Project project;

  @Enumerated(EnumType.STRING)
  @Column(name = "TYPE")
  protected ComponentType type;

  @Column(name = "VCS_ID")
  protected String vcsId;

  @Transient
  public boolean isRequirement() {
    return ComponentType.REQUIREMENT == type;
  }

  @Override
  public String toString() {
    return "Component{" +
        "id=" + id +
        ", type=" + type +
        '}';
  }
}
