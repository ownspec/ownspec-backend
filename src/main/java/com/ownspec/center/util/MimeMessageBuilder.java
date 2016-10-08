package com.ownspec.center.util;

import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.annotation.Nonnull;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by lyrold on 08/10/2016.
 */
public class MimeMessageBuilder {
    private Session session;
    private String from;
    private String subject = "";
    private String body = "";
    private List<String> recipients = new ArrayList<>();
    private List<String> recipientsCc = new ArrayList<>();
    private List<String> recipientsBcc = new ArrayList<>();
    private List<String> attachmentsPaths = new ArrayList<>();

    private MimeMessageBuilder() {
    }

    public static MimeMessageBuilder newBuilder() {
        return new MimeMessageBuilder();
    }

    public MimeMessageBuilder setSession(@Nonnull Session session) {
        this.session = session;
        return this;
    }

    public MimeMessageBuilder setFrom(@Nonnull String from) {
        this.from = from;
        return this;
    }

    public MimeMessageBuilder setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public MimeMessageBuilder setBody(String body) {
        this.body = body;
        return this;
    }

    public MimeMessageBuilder setBody(@Nonnull Resource resource) {
        try (Stream<String> lines = Files.lines(Paths.get(resource.getURI()))) {
            body = lines.collect(Collectors.joining());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return this;
    }


    public MimeMessageBuilder addRecipient(String recipient) {
        this.recipients.add(recipient);
        return this;
    }

    public MimeMessageBuilder addRecipients(List<String> recipients) {
        this.recipients.addAll(recipients);
        return this;
    }

    public MimeMessage build() throws MessagingException {
        Objects.requireNonNull(session);
        MimeMessage mimeMessage = new MimeMessage(session);
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false);
        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(body, true);

        //Add recipients
        for (String recipient : recipients) {
            mimeMessageHelper.addTo(recipient);
        }
        for (String recipientCc : recipientsCc) {
            mimeMessageHelper.addCc(recipientCc);
        }
        for (String recipientBcc : recipientsBcc) {
            mimeMessageHelper.addBcc(recipientBcc);
        }

        //Add attachmentsPaths
        for (String attachmentPath : attachmentsPaths) {
            File file = new File(attachmentPath);
            mimeMessageHelper.addAttachment(file.getName(), file);
        }

        return mimeMessage;
    }

}
