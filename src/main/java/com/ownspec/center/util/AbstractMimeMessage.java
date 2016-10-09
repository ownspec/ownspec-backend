package com.ownspec.center.util;

import lombok.Data;
import org.springframework.core.io.Resource;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by lyrold on 09/10/2016.
 */
@Data
public class AbstractMimeMessage {
    private String from;
    private String subject = "";
    private String body = "";
    private List<String> recipients = new ArrayList<>();
    private List<String> recipientsCc = new ArrayList<>();
    private List<String> recipientsBcc = new ArrayList<>();
    private List<String> attachmentsPaths = new ArrayList<>();

    public static AbstractMimeMessage builder() {
        return new AbstractMimeMessage();
    }

    public AbstractMimeMessage from(String from) {
        this.from = from;
        return this;
    }

    public AbstractMimeMessage subject(String subject) {
        this.subject = subject;
        return this;
    }

    public AbstractMimeMessage body(String body) {
        this.body = body;
        return this;
    }

    public AbstractMimeMessage body(@Nonnull Resource resource) {
        try (Stream<String> lines = Files.lines(Paths.get(resource.getURI()))) {
            body = lines.collect(Collectors.joining());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return this;
    }


    public AbstractMimeMessage addRecipient(String recipient) {
        this.recipients.add(recipient);
        return this;
    }

    public AbstractMimeMessage addRecipients(List<String> recipients) {
        this.recipients.addAll(recipients);
        return this;
    }

    public AbstractMimeMessage addRecipientCc(String recipientCc) {
        this.recipientsCc.add(recipientCc);
        return this;
    }

    public AbstractMimeMessage addRecipientsCc(List<String> recipientsCc) {
        this.recipientsCc.addAll(recipientsCc);
        return this;
    }

    public AbstractMimeMessage addRecipientBcc(String recipientBcc) {
        this.recipientsBcc.add(recipientBcc);
        return this;
    }

    public AbstractMimeMessage addRecipientsBcc(List<String> recipientsBcc) {
        this.recipientsBcc.addAll(recipientsBcc);
        return this;
    }
}
