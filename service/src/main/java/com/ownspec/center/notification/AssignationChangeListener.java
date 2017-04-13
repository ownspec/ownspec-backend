package com.ownspec.center.notification;

import com.ownspec.center.service.component.ComponentNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Created by nlabrot on 13/04/17.
 */
@Component
public class AssignationChangeListener {

  @Autowired
  private ComponentNotificationService componentNotificationService;

  @EventListener
  public void assigneeChange(AssignationChangeEvent event) {
    componentNotificationService.sendAssignmentChangeEmail(event);
  }
}
