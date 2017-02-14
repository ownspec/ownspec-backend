package com.ownspec.center.model.user;

import com.ownspec.center.model.MainSequenceConstants;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created on 12/02/2017
 *
 * @author lyrold
 */
@Data
@Entity
@Table(name = "USER_GROUP")
public class UserGroup {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;

  @Column(name = "NAME") private String name;

}