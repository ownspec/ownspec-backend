package com.ownspec.center.service.composition;

import com.ownspec.center.exception.CompositionException;
import com.ownspec.center.service.FreeMarkerService;
import freemarker.template.Template;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

  @Autowired
  private FreeMarkerService freeMarkerService;

  @PostConstruct
  public void init() {
    File outputDir = new File(outputDirectory);
    if (!outputDir.exists()) {
      outputDir.mkdirs();
    }
  }

  public String compose(String templateName, Map model) {

    StringWriter writer = new StringWriter();

    try {
/*
      Template template = freeMarkerService.getConfiguration().getTemplate(
          templateName.split("\\.").length == 2 && templateName.matches(".*\\.*$") ?
          templateName :
          templateName + defaultTemplateExtension
*/

      Template template = freeMarkerService.getConfiguration().getTemplate(
          templateName.endsWith(defaultTemplateExtension) ?
              templateName :
              templateName + defaultTemplateExtension

      );
      template.process(model, writer);
    } catch (Exception e) {
      throw new CompositionException(e);
    }
    return writer.toString();
  }


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
      renderer.getSharedContext().setUserAgentCallback(callback);

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

  private static class ResourceLoaderUserAgent extends ITextUserAgent {
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
