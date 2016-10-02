package com.ownspec.center.service;

import com.ownspec.center.model.Task;
import com.ownspec.center.model.user.User;
import org.springframework.stereotype.Service;

/**
 * Created by lyrold on 01/10/2016.
 */
@Service
public class EmailService {


    public void sendResetPasswordNotification(User user) {
        String redirectLink;


    }

    public void sendConfirmRegistrationNotification(User user) {
        String confirmationLink;

    }

    public void sendNewTaskNotification(Task task) {

    }
}
