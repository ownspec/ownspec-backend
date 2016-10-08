package com.ownspec.center.service;

import com.ownspec.center.AbstractTest;
import com.ownspec.center.exception.EmailServiceException;
import com.ownspec.center.util.MimeMessageBuilder;
import org.junit.Test;
import org.nlab.smtp.pool.SmtpConnectionPool;
import org.nlab.smtp.transport.connection.ClosableSmtpConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.mail.internet.MimeMessage;

/**
 * Created by lyrold on 08/10/2016.
 */
public class EmailServiceTest extends AbstractTest {

    @Autowired
    private SmtpConnectionPool smtpConnectionPool;

    @Value("${email.from.no-reply}")
    private String fromNoReply;

    @Test
    public void send() throws Exception {

        try (ClosableSmtpConnection transport = smtpConnectionPool.borrowObject()) {
            MimeMessage mimeMessage = MimeMessageBuilder.newBuilder()
                    .setSession(transport.getSession())
                    .setFrom(fromNoReply)
                    .setSubject("Moien")
                    .setBody("Dear User, \n...")
                    .addRecipient("lyrold.careto@gmail.com")
                    .build();
            transport.sendMessage(mimeMessage);
        } catch (Exception e) {
            throw new EmailServiceException(e);
        }
    }

    @Test
    public void sendConfirmRegistrationNotification() throws Exception {

    }

}