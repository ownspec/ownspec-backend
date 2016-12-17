package com.ownspec.center.model.user;

import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created on 16/12/2016
 *
 * @author lyrold
 */
@Data
@Entity
public class UserComponent implements Persistable<Long> {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private User user;

  @ManyToOne
  private Component component;

  @Enumerated(EnumType.STRING)
  private ComponentType componentType;

  private boolean favorite;

  private Long visitedTime;

  @CreatedDate
  protected Instant createdDate;

  @LastModifiedDate
  protected Instant lastModifiedDate;

  @Override
  public boolean isNew() {
    return null == getId();
  }

  public void addVisit() {
    visitedTime++;
  }

}
