package com.ownspec.center.model.qa;

import com.ownspec.center.model.MainSequenceConstants;
import com.ownspec.center.model.Project;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by nlabrot on 28/04/17.
 */
@Data
@Entity
@Table(name = "QA_COMPONENT")
public class QaComponent {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;

  // Ease sorting
  @JoinColumn(name = "CODE")
  protected String code;

  @JoinColumn(name = "CODE_NUMBER")
  protected long codeNumber;


  // Project which owns this component, if any
  @ManyToOne
  @JoinColumn(name = "PROJECT_ID")
  protected Project project;


  @Enumerated(EnumType.STRING)
  @Column(name = "TYPE")
  protected QaComponentType type;

  @Override
  public String toString() {
    return "Component{" +
        "id=" + id +
        ", type=" + type +
        '}';
  }
}
