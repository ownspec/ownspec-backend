package com.ownspec.center.service;

import com.ownspec.center.AbstractTest;
import com.ownspec.center.util.AbstractMimeMessage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by lyrold on 08/10/2016.
 */
public class EmailServiceTest extends AbstractTest {

  @Autowired
  private EmailService emailService;

  @Test
  public void send() throws Exception {
    AbstractMimeMessage message = AbstractMimeMessage.builder()
        .subject("Moien2")
        .body("Dear User, \n...")
        .addRecipient("lyrold.careto@gmail.com");

    emailService.send(message);
  }

  @Test
  public void sendConfirmRegistrationNotification() throws Exception {

  }

}