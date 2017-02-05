package com.ownspec.center.model;

import com.ownspec.center.model.audit.AbstractAuditable;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.persistable.Persistable;
import com.ownspec.center.model.user.UserCategory;
import lombok.Data;

import java.util.concurrent.TimeUnit;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
  @JoinColumn(name = "COMPONENT_ID")
  Component component;

  @Embedded
  UserCategory userCategory;

  // TODO: to refactor and convert in a duration
  @Column(name = "TIME")
  Double time;

  // TODO: to refactor and convert in a duration
  @Enumerated(EnumType.STRING)
  @Column(name = "TIME_UNIT")
  TimeUnit timeUnit;
}
