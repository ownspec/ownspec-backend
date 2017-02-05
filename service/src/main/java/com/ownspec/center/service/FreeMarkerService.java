package com.ownspec.center.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.SpringTemplateLoader;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

/**
 * Created by nlabrot on 15/01/17.
 */
@Service
public class FreeMarkerService {

  private Configuration configuration;

  @Value("${composition.template.componentLoaderPath}")
  private String componentLoaderPath;

  @Value("${composition.template.resourceLoaderPath}")
  private String resourceLoaderPath;

  @Autowired
  private ResourceLoader resourceLoader;


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

  public Configuration getConfiguration() {
    return configuration;
  }


  public void process(String templatePath, Map<String, Object> context, Writer writer) throws IOException, TemplateException {
    Template coverTemplate = configuration.getTemplate(templatePath);

    Map<String, Object> copyContext = new HashMap<>(context);

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
