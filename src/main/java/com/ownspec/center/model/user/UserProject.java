package com.ownspec.center.model.user;

import com.ownspec.center.model.Project;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created on 03/12/2016
 *
 * @author lyrold
 */
@Data
@Entity
public class UserProject implements Persistable<Long>{

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private User user;

  @ManyToOne
  private Project project;

  private boolean favorite;

  private Long visitedTime;

  //todo access mode (RO,RW...etc);

  @CreatedDate
  private Instant createdDate;
  @LastModifiedDate
  private Instant lastModifiedDate;

  @Override
  public boolean isNew() {
    return null == getId();
  }

  public void addVisit() {
    visitedTime++;
  }

}
