package com.ownspec.center.model;

import com.ownspec.center.model.user.User;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.Instant;

/**
 * Created by lyrold on 01/10/2016.
 */
@Data
@Entity
public class Notification {

  @Id
  @GeneratedValue
  private Long id;

  private String content;

  @Column(columnDefinition = "boolean default true")
  private boolean unread;

  @ManyToOne
  private User recipient;

  @CreatedDate
  private Instant createdDate;

}
