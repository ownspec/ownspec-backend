package com.ownspec.center.model.user;

import com.ownspec.center.model.MainSequenceConstants;
import com.ownspec.center.model.Project;
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
 * Created on 03/12/2016
 *
 * @author lyrold
 */
@Data
@Entity
@Table(name = "USER_PROJECT")
public class UserProject extends AbstractAuditable implements Persistable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "USER_ID")
  private User user;

  @ManyToOne
  @JoinColumn(name = "PROJECT_ID")
  private Project project;

  @JoinColumn(name = "FAVORITE")
  private boolean favorite;

  // TODO: to refactor, most visited project make sense on limited time range
  @JoinColumn(name = "VISITED_TIME")
  private Long visitedTime;

  //todo access mode (RO,RW...etc);
  // => should be implemeted using role


  public void addVisit() {
    visitedTime++;
  }
}
