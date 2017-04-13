package com.ownspec.center.service.component;

import com.google.common.collect.ImmutableMap;
import com.ownspec.center.model.component.ComponentVersion;
import com.ownspec.center.model.user.User;
import com.ownspec.center.notification.AssignationChangeEvent;
import com.ownspec.center.service.EmailService;
import com.ownspec.center.service.UserService;
import com.ownspec.center.service.composition.CompositionService;
import com.ownspec.center.util.AbstractMimeMessage;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by nlabrot on 13/04/17.
 */
@Service
public class ComponentNotificationService {

  @Autowired
  private EmailService emailService;

  @Autowired
  private CompositionService compositionService;

  @Autowired
  private UserService userService;

  @Autowired
  private ComponentVersionService componentVersionService;

  @Transactional
  public void sendAssignmentChangeEmail(AssignationChangeEvent event) {

    User user = userService.findOne(event.getNewUserId());
    ComponentVersion componentVersion = componentVersionService.findOne(event.getComponentVersionId());

    // Build verification url
    String verificationUrl = "";

    emailService.send(buildConfirmRegistrationMessage(user, componentVersion, verificationUrl));
  }

  public AbstractMimeMessage buildConfirmRegistrationMessage(User user, ComponentVersion componentVersion, String verificationUrl) {


    // Compose email body
    String emailBody = compositionService.compose("email/abstract_notification",
        ImmutableMap.builder()
            .put("contentTmpl", "component_assignment_notification.ftl")
            .put("firstName", user.getFirstName())
            .put("project", ObjectUtils.defaultIfNull(componentVersion.getComponent().getProject(), ""))
            .put("componentVersion", componentVersion)
            .put("componentEditUrl", verificationUrl)
            .build()
    );
    return AbstractMimeMessage.builder()
        .addRecipient(user.getEmail())
        .subject("Ownspec - Component Assigment") //todo to be internationalized + context
        .body(emailBody);
  }

}
