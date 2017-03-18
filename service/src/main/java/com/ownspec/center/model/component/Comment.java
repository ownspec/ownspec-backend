package com.ownspec.center.model.component;

import com.ownspec.center.model.MainSequenceConstants;
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
 * Created by lyrold on 27/09/2016.
 */
@Data
@Entity
@Table(name = "COMMENT")
public class Comment extends AbstractAuditable implements Persistable{
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;

  @Column(name = "VALUE")
  private String value;

  @ManyToOne
  @JoinColumn(name = "COMPONENT_ID")
  private Component component;

}
