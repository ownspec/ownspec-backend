package com.ownspec.center.model;

import com.ownspec.center.model.user.User;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.time.Instant;

/**
 * Created by lyrold on 09/10/2016.
 */
@Data
@Entity
public class Invoice {

  /**
   * Invoice will be used to automatically invoicing a project,
   * based on a quantifiable component map,
   * or the ownspec's customer, based on active users and their assets
   */

  @Id
  @GeneratedValue
  private Long id;

  @CreatedDate
  private Instant createdDate;

  @OneToOne
  @CreatedBy
  private User author;

  @ManyToOne
  private User recipient;


}
