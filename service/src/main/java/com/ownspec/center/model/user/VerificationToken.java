package com.ownspec.center.model.user;

import com.ownspec.center.model.MainSequenceConstants;
import lombok.Data;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created on 12/02/2017
 *
 * @author lyrold
 */
@Data
@Entity
@Table(name = "VERIFICATION_TOKEN")
public class VerificationToken {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  private Long id;

  @Column(name="TOKEN")
  private String token;

  @OneToOne
  @JoinColumn(name = "USER_ID",nullable = false)
  private User user;

  @Column(name="EXPIRY_DATE")
  private Date expiryDate;

}
