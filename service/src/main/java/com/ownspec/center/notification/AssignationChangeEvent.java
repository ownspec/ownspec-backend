package com.ownspec.center.notification;

import lombok.Getter;

/**
 * Created by nlabrot on 13/04/17.
 */
@Getter
public class AssignationChangeEvent {

  private final Long oldUserId;
  private final Long newUserId;
  private final Long projectId;
  private final Long componentVersionId;

  public AssignationChangeEvent(Long oldUserId, Long newUserId, Long projectId, Long componentVersionId) {
    this.oldUserId = oldUserId;
    this.newUserId = newUserId;
    this.projectId = projectId;
    this.componentVersionId = componentVersionId;
  }
}
