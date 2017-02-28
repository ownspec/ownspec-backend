package com.ownspec.center.model.component;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created by nlabrot on 26/02/17.
 */
@Data
@Entity
@Table(name = "COMPONENT_CODE_COUNTER")
public class ComponentCodeCounter  extends AbstractAuditable implements Persistable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;

  @Column(name = "COMPONENT_KEY")
  private String key;

  @Column(name = "COUNTER")
  private long counter;

  @Column(name = "GENERIC")
  private boolean generic;

  public long incrementAndGet(){
    return ++counter;

  }
}
