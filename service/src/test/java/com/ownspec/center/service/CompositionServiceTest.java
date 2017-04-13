package com.ownspec.center.service;

import com.google.common.collect.ImmutableMap;
import com.ownspec.center.AbstractTest;
import com.ownspec.center.service.composition.CompositionService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lyrold on 09/10/2016.
 */
public class CompositionServiceTest extends AbstractTest {

  @Autowired
  private CompositionService compositionService;


  @Test
  public void compose() throws Exception {


    Map<String, Object> model = new HashMap<>();
    model.put("firstname", "Lyrold");
    model.put("lastname", "Careto");
    model.put("phone", "+33 000 000 000");
    model.put("email", "lyrold@ownspec.com");

    String outputContent = compositionService.compose("template", model);
    String expected = "<p>--</p>\n" +
        "<p><strong>Lyrold Careto</strong></p>\n" +
        "<p><strong>Tel. :</strong>+33 000 000 000</p>\n" +
        "<p>E-Mail :&nbsp;<a href=\"mailto:lyrold@ownspec.com\" target=\"_blank\">lyrold@ownspec.com</a></p>";

    Assert.assertEquals(expected, outputContent);
  }

  @Test
  public void compose_fromFile() throws IOException {

    Map<String, Object> model = new HashMap<>();
    model.put("firstname", "Lyrold");
    model.put("lastname", "Careto");
    model.put("phone", "+33 000 000 000");
    model.put("email", "lyrold@ownspec.com");

    String outputContent = compositionService.compose("template.ftl", model);

    String expected = "<p>--</p>\n" +
        "<p><strong>Lyrold Careto</strong></p>\n" +
        "<p><strong>Tel. :</strong>+33 000 000 000</p>\n" +
        "<p>E-Mail :&nbsp;<a href=\"mailto:lyrold@ownspec.com\" target=\"_blank\">lyrold@ownspec.com</a></p>";

    Assert.assertEquals(expected, outputContent);
  }



  @Test
  public void testPdf() throws Exception {

    // Build verification url
    String verificationUrl = "dddd";
    // Compose email body
    String content = compositionService.compose(
        "email/confirm_registration_content",
        ImmutableMap.of("verificationUrl", verificationUrl));

    String emailBody = compositionService.compose(
        "email/abstract_notification",
        ImmutableMap.builder()
            .put("foobar", "confirm_registration_content.ftl")
            .put("firstName", "first name")
            .put("content", content)
            .put("verificationUrl", verificationUrl).build()
    );

    System.out.println("ok");
  }
}