package com.ownspec.center.service;

import com.ownspec.center.model.Task;
import com.ownspec.center.model.user.User;
import org.nlab.smtp.pool.SmtpConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by lyrold on 01/10/2016.
 */
@Service
public class EmailService {

    @Value("${email.from.no-reply}")
    private String fromNoReply;

    @Autowired
    private SmtpConnectionPool smtpConnectionPool;


    public void sendResetPasswordNotification(User user) {
        String redirectLink;

    }

    public void sendConfirmRegistrationNotification(User user) {
        String confirmationLink;
    }

    public void sendNewTaskNotification(Task task) {

    }
}
