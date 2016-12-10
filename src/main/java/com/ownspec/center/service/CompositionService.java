package com.ownspec.center.service;

import com.ownspec.center.exception.CompositionException;
import com.ownspec.center.model.component.Component;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.StringModel;
import freemarker.template.Configuration;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.SpringTemplateLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

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

  @Value("${wkhtmltopdf.path}")
  private String wkhtmltopdfPath;

  @Autowired
  private ResourceLoader resourceLoader;


  private Configuration configuration;

  @PostConstruct
  public void init() throws IOException {
    Files.createDirectories(Paths.get(componentLoaderPath));

    TemplateLoader[] loaders = new TemplateLoader[]{
        new SpringTemplateLoader(resourceLoader, resourceLoaderPath),
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

  public Resource htmlToPdf(Component component, Resource content) {
    try {
      File coverFile = File.createTempFile("html", ".html");

      try (FileWriter writer = new FileWriter(coverFile)) {
        Map<String, Object> context = new HashMap<>();
        context.put("component", component);
        context.put("createdDate" , LocalDateTime.ofInstant(component.getCreatedDate() , ZoneOffset.UTC));
        process("cover/cover.ftl", context, writer);
      }

      File tempFile = File.createTempFile("html", ".html");

      File pdfFile = File.createTempFile("pdf", ".pdf");

      try (InputStream inputStream = content.getInputStream(); FileOutputStream os = new FileOutputStream(tempFile)) {
        IOUtils.copy(inputStream, os);
      }

      String line = wkhtmltopdfPath + " cover " + coverFile.getAbsolutePath() + " toc " + tempFile.getAbsolutePath() + " " + pdfFile.getAbsolutePath();
      CommandLine cmdLine = CommandLine.parse(line);
      DefaultExecutor executor = new DefaultExecutor();
      executor.setExitValue(0);
      ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
      executor.setWatchdog(watchdog);
      int exitValue = executor.execute(cmdLine);

      return new FileSystemResource(pdfFile);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  public Configuration getConfiguration() {
    return configuration;
  }


  private void process(String templatePath, Map<String, Object> context, Writer writer) throws IOException, TemplateException {
    Template coverTemplate = configuration.getTemplate(templatePath);

    Map<String,Object> copyContext = new HashMap<>(context);

    copyContext.put("formatDateTime", new FormatDateTimeMethodModel());

    coverTemplate.process(copyContext, writer);
  }


  public static class FormatDateTimeMethodModel implements TemplateMethodModelEx {
    public Object exec(List args) throws TemplateModelException {
      if (args.size() != 2) {
        throw new TemplateModelException("Wrong arguments");
      }
      TemporalAccessor time = (TemporalAccessor) ((StringModel) args.get(0)).getWrappedObject();

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(((SimpleScalar) args.get(1)).getAsString());
      return formatter.format(time);
    }
  }
}
