package com.ownspec.center.model.component;

import com.ownspec.center.model.MainSequenceConstants;
import com.ownspec.center.model.audit.AbstractAuditable;
import com.ownspec.center.model.persistable.Persistable;
import com.ownspec.center.model.user.UserCategory;
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
 * Created on 11/12/2016
 *
 * @author lyrold
 */
@Data
@Entity
@Table(name = "ESTIMATED_TIME")
public class EstimatedTime extends AbstractAuditable implements Persistable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "COMPONENT_VERSION_ID")
  private ComponentVersion componentVersion;

  @ManyToOne
  @JoinColumn(name = "USER_CATEGORY_ID")
  private UserCategory userCategory;

  // TODO: to refactor and convert in a duration
  @Column(name = "DURATION")
  private String duration;

  // TODO: to refactor and convert in a duration
  @Column(name = "DURATION_IN_MS")
  private Long durationInMs;

}
