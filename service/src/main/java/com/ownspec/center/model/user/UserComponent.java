package com.ownspec.center.model.user;

import com.ownspec.center.model.MainSequenceConstants;
import com.ownspec.center.model.audit.AbstractAuditable;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.persistable.Persistable;
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
 * Created on 16/12/2016
 *
 * @author lyrold
 */
@Data
@Entity
@Table(name = "USER_COMPONENT")
// TODO: what is the purpose of this class? is this class used only for visit?
public class UserComponent extends AbstractAuditable implements Persistable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "USER_ID")
  private User user;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "COMPONENT_ID")
  private Component component;

  @Column(name = "FAVORITE")
  private boolean favorite;

  @Column(name = "VISITED_TIME")
  private Long visitedTime;

  public void addVisit() {
    visitedTime++;
  }

}
