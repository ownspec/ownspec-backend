package com.ownspec.center.service.content.generator;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.workflow.WorkflowInstance;
import com.ownspec.center.service.component.ComponentService;
import com.ownspec.center.service.content.ContentConfiguration;
import com.ownspec.center.service.content.parser.HtmlComponentContentParser;
import com.ownspec.center.service.content.generator.HtmlGeneratorParserCallBack;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by nlabrot on 03/11/16.
 */
public class HtmlContentGenerator {

  @Autowired
  private ComponentService componentService;

  @Value("${component.content.summary-length:80}")
  private int summaryLength;

  @Autowired
  private ContentConfiguration contentConfiguration;

  public Pair<String, String> generate(Component c, WorkflowInstance workflowInstance) {
    Element body = generateComponentContent(c, workflowInstance, false, null);
    String substring = body.text().replaceAll("(?<=.{" + summaryLength + "})\\b.*", "...");
    return Pair.of(body.html(), substring);
  }

  public Path generateForComposition(Component c, WorkflowInstance workflowInstance, boolean forComposition, Path outputDirectory) {
    Element body = generateComponentContent(c, workflowInstance, forComposition, outputDirectory);
    Path path = outputDirectory.resolve(Paths.get("component.html"));

    try {
      Files.write(path , body.html().getBytes(UTF_8));
    } catch (IOException e) {
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
