package com.ownspec.center.service.content.generator;

import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.workflow.WorkflowInstance;
import com.ownspec.center.service.FreeMarkerService;
import com.ownspec.center.service.component.ComponentService;
import com.ownspec.center.service.content.ContentConfiguration;
import com.ownspec.center.service.content.parser.HtmlComponentContentParser;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nlabrot on 03/11/16.
 */
public class CompositionHtmlContentGenerator {

  @Autowired
  private ComponentService componentService;

  @Autowired
  private FreeMarkerService freeMarkerService;


  @Autowired
  private ContentConfiguration contentConfiguration;

  public Path generate(Component c, WorkflowInstance workflowInstance, boolean forComposition, Path outputDirectory) {
    Element body = generateComponentContent(c, workflowInstance, forComposition, outputDirectory);
    Path path = outputDirectory.resolve(Paths.get("composition.html"));

    try {
      try (FileWriter writer = new FileWriter(path.toFile())) {
        Map<String, Object> context = new HashMap<>();
        context.put("content" , body.html());
        freeMarkerService.process("composition/composition.ftl", context, writer);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return path;
  }

  private Element generateComponentContent(Component c, WorkflowInstance workflowInstance, boolean forComposition, Path outputDirectory) {
    HtmlGeneratorParserCallBack callBack = contentConfiguration.htmlGeneratorParserCallBack(forComposition, outputDirectory);

    HtmlComponentContentParser<Element> contentParser = new HtmlComponentContentParser();
    Resource resource = componentService.getContent(c, workflowInstance.getId());
    return contentParser.parse(resource, callBack);
  }
}
