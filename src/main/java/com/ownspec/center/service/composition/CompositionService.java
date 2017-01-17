package com.ownspec.center.service.composition;

import com.ownspec.center.exception.CompositionException;
import com.ownspec.center.service.FreeMarkerService;
import freemarker.template.Template;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.FileUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.ITextUserAgent;
import org.xhtmlrenderer.resource.XMLResource;
import org.xml.sax.InputSource;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
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

  @Value("${wkhtmltopdf.path}")
  private String wkhtmltopdfPath;

  @Autowired
  private FreeMarkerService freeMarkerService;



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
      Template template = freeMarkerService.getConfiguration().getTemplate(
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

 /* public Resource wkHtmlToPdf(Component component, Resource content) {
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
*/

  public Resource flyingHtmlToPdf(Path path) {
    try {

      Path fonts = Files.createDirectories(path.getParent().resolve("fonts"));

      try (InputStream inputStream = new ClassPathResource("fonts/fonts.zip").getInputStream()) {
        ZipUtil.unpack(inputStream, fonts.toFile());
      }


      CleanerProperties props = new CleanerProperties();

// set some properties to non-default values
      props.setTranslateSpecialEntities(true);
      props.setTransResCharsToNCR(true);
      props.setOmitComments(true);

// do parsing

      TagNode tagNode = new HtmlCleaner(props).clean(path.toFile());


      Path cleanXml = path.getParent().resolve(Paths.get("composition.xml"));


// serialize to xml file
      new PrettyXmlSerializer(props).writeToFile(
          tagNode, cleanXml.toString(), "utf-8"
      );




      ITextRenderer renderer = new ITextRenderer();
      ResourceLoaderUserAgent callback = new ResourceLoaderUserAgent(renderer.getOutputDevice());
      callback.setSharedContext(renderer.getSharedContext());
      renderer.getSharedContext ().setUserAgentCallback(callback);

      String url = cleanXml.toUri().toURL().toString();

      Document doc = XMLResource.load(new InputSource(url)).getDocument();

      Path pdfPath = cleanXml.getParent().resolve(Paths.get("composition.pdf"));

      try (OutputStream os = Files.newOutputStream(pdfPath)) {
        renderer.setDocument(doc, url);
        renderer.layout();
        renderer.createPDF(os);
      }



      return new FileSystemResource(pdfPath.toFile());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static class ResourceLoaderUserAgent extends ITextUserAgent
  {
    public ResourceLoaderUserAgent(ITextOutputDevice outputDevice) {
      super(outputDevice);
    }

    protected InputStream resolveAndOpenStream(String uri) {
      InputStream is = super.resolveAndOpenStream(uri);
      System.out.println("IN resolveAndOpenStream() " + uri);
      return is;
    }
  }

}
