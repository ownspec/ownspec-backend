package com.ownspec.center.service;

import com.ownspec.center.exception.CompositionException;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by lyrold on 09/10/2016.
 */
@Service
public class CompositionService {

  @Value("${composition.outputDirectory}")
  private String outputDirectory;

  @Value("${composition.template.extension}")
  private String defaultTemplateExtension;

  @Value("${composition.template.resourceLoaderPath}")
  private String resourceLoaderPath;

  @Value("${composition.template.componentLoaderPath}")
  private String componentLoaderPath;

  private Configuration configuration;

  @PostConstruct
  public void init() throws IOException {
    Files.createDirectories(Paths.get(componentLoaderPath));

    TemplateLoader[] loaders = new TemplateLoader[]{
        new FileTemplateLoader(new ClassPathResource(resourceLoaderPath).getFile()),
        new FileTemplateLoader(new File(componentLoaderPath))
    };
    MultiTemplateLoader multiTemplateLoader = new MultiTemplateLoader(loaders);

    configuration = new Configuration(Configuration.VERSION_2_3_25);
    configuration.setTemplateLoader(multiTemplateLoader);
  }

  public String compose(String templateName, Map model) {
    try {
      File outputFile = File.createTempFile("tmp-", ".html", new File(outputDirectory));
      File composedFile = compose(
          templateName,
          model,
          outputFile.getAbsolutePath());
      return FileUtils.readFileToString(composedFile, CharEncoding.UTF_8);
    } catch (IOException e) {
      throw new CompositionException(e);
    }
  }

  public File compose(String templateName, Map model, String outputFilePath) {
    File outputFile = new File(outputFilePath);
    try (Writer writer = new FileWriter(outputFile)) {
      Template template = configuration.getTemplate(
          templateName.split("\\.").length == 2 && templateName.matches(".*\\.*$") ?
              templateName :
              templateName + defaultTemplateExtension
      );
      template.process(model, writer);
    } catch (Exception e) {
      throw new CompositionException(e);
    }
    return outputFile;
  }

  public File compose(File source, Map model, String outputFilePath) {
    return compose(source.getName(), model, outputFilePath);
  }

  public File htmlToPdf(File source, String target) {
    return new File(target);
  }

  public File htmlToPdf(File source) {
    return htmlToPdf(source, "foo.pdf");
  }
}
