package com.ownspec.center.service;

import com.ownspec.center.AbstractTest;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
    Resource template = new ClassPathResource("templates/template.ftl");
    File savedComponentAsTemplateDir = new File("target/savedComponentAsTemplate");
    FileUtils.forceMkdir(savedComponentAsTemplateDir);
    File destFile = new File(savedComponentAsTemplateDir, "foo.html");
    FileUtils.copyFile(template.getFile(), destFile);

    Map<String, Object> model = new HashMap<>();
    model.put("firstname", "Lyrold");
    model.put("lastname", "Careto");
    model.put("phone", "+33 000 000 000");
    model.put("email", "lyrold@ownspec.com");

    String outputFilename = destFile.getAbsolutePath().replaceFirst("\\.html", "-composed.html");
    String outputContent = FileUtils.readLines(
        compositionService.compose(destFile, model, outputFilename), "UTF-8")
        .stream()
        .collect(Collectors.joining("\n"));

    String expected = "<p>--</p>\n" +
        "<p><strong>Lyrold Careto</strong></p>\n" +
        "<p><strong>Tel. :</strong>+33 000 000 000</p>\n" +
        "<p>E-Mail :&nbsp;<a href=\"mailto:lyrold@ownspec.com\" target=\"_blank\">lyrold@ownspec.com</a></p>";

    Assert.assertEquals(expected, outputContent);

  }

}