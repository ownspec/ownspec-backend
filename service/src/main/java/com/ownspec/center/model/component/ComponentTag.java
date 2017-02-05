package com.ownspec.center.model.component;

import com.ownspec.center.model.MainSequenceConstants;
import com.ownspec.center.model.Tag;
import com.ownspec.center.model.audit.AbstractAuditable;
import com.ownspec.center.model.persistable.Persistable;
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
 * Created by lyrold on 23/08/2016.
 */
@Data
@Entity
@Table(name = "COMPONENT_TAG")
public class ComponentTag extends AbstractAuditable implements Persistable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  protected Long id;

  // TODO: tag.id and component.id composed the ComponentTag id
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "TAG_ID")
  private Tag tag;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "COMPONENT_ID")
  private Component component;
}
