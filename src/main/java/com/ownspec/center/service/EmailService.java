package com.ownspec.center.service;

import com.ownspec.center.exception.EmailServiceException;
import com.ownspec.center.model.Task;
import com.ownspec.center.util.AbstractMimeMessage;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.nlab.smtp.pool.SmtpConnectionPool;
import org.nlab.smtp.transport.connection.ClosableSmtpConnection;
import org.nlab.smtp.transport.factory.SmtpConnectionFactory;
import org.nlab.smtp.transport.factory.SmtpConnectionFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 * Created by lyrold on 01/10/2016.
 */
@Service
public class EmailService {

  @Value("${email.host}")
  private String emailHost;

  @Value("${email.port}")
  private int emailPort;

  @Value("${email.protocol}")
  private String emailProtocol;

  @Value("${email.from.default}")
  private String defaultFromAddress;

  private SmtpConnectionPool smtpConnectionPool;
  private Session session;

  @PostConstruct
  public void init() {
    GenericObjectPoolConfig config = new GenericObjectPoolConfig();
    config.setMaxTotal(1);
    SmtpConnectionFactory smtpConnectionFactory = SmtpConnectionFactoryBuilder.newSmtpBuilder()
                                                                              .host(emailHost)
                                                                              .port(emailPort)
                                                                              .protocol(emailProtocol)
                                                                              .build();
    smtpConnectionPool = new SmtpConnectionPool(smtpConnectionFactory, config);
    session = smtpConnectionPool.getSession();
  }


  public void send(AbstractMimeMessage abstractMimeMessage) {
    boolean messageIsValid = validate(abstractMimeMessage);
    if (messageIsValid) {
      try (ClosableSmtpConnection transport = smtpConnectionPool.borrowObject()) {
        MimeMessage message = buildMessageFrom(abstractMimeMessage);
        transport.sendMessage(message);
      } catch (Exception e) {
        throw new EmailServiceException(e);
      }
    }
  }

  private boolean validate(AbstractMimeMessage abstractMimeMessage) {
    return true;
  }

  private MimeMessage buildMessageFrom(AbstractMimeMessage abstractMimeMessage) throws MessagingException {
    Objects.requireNonNull(session);
    MimeMessage mimeMessage = new MimeMessage(session);
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false);

    mimeMessageHelper.setFrom(abstractMimeMessage.getFrom() != null ?
                              abstractMimeMessage.getFrom() : defaultFromAddress
                             );
    mimeMessageHelper.setSubject(abstractMimeMessage.getSubject());
    mimeMessageHelper.setText(abstractMimeMessage.getBody(), true);

    //Add recipients
    for (String recipient : abstractMimeMessage.getRecipients()) {
      mimeMessageHelper.addTo(recipient);
    }
    for (String recipientCc : abstractMimeMessage.getRecipientsCc()) {
      mimeMessageHelper.addCc(recipientCc);
    }
    for (String recipientBcc : abstractMimeMessage.getRecipientsBcc()) {
      mimeMessageHelper.addBcc(recipientBcc);
    }

    //Add attachmentsPaths
    for (String attachmentPath : abstractMimeMessage.getAttachmentsPaths()) {
      File file = new File(attachmentPath);
      mimeMessageHelper.addAttachment(file.getName(), file);
    }

    return mimeMessage;
  }

  public void sendNewTaskNotification(Task task) {

  }
}
