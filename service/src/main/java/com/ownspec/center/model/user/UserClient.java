package com.ownspec.center.model.user;

import com.ownspec.center.model.Client;
import com.ownspec.center.model.MainSequenceConstants;
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
 * Created on 18/03/2017
 *
 * @author lyrold
 */
@Data
@Entity
@Table(name = "USER_CLIENT")
public class UserClient {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "CLIENT_ID")
  private Client client;

  @ManyToOne
  @JoinColumn(name = "USER_ID")
  private User user;

  @Column(name = "IS_ACCOUNT_MANAGER")
  private boolean isAccountManager;

}
