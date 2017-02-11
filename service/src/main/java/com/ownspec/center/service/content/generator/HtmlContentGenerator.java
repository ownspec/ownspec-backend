package com.ownspec.center.service.content.generator;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentVersion;
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

  public Pair<String, String> generate(ComponentVersion c) {
    Element body = generateComponentContent(c, false, null);
    String substring = body.text().replaceAll("(?<=.{" + summaryLength + "})\\b.*", "...");
    return Pair.of(body.html(), substring);
  }

  public Path generateForComposition(ComponentVersion c, boolean forComposition, Path outputDirectory) {
    Element body = generateComponentContent(c, forComposition, outputDirectory);
    Path path = outputDirectory.resolve(Paths.get("component.html"));

    try {
      Files.write(path , body.html().getBytes(UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return path;
  }

  private Element generateComponentContent(ComponentVersion c, boolean forComposition, Path outputDirectory) {
    HtmlGeneratorParserCallBack callBack = contentConfiguration.htmlGeneratorParserCallBack(forComposition, outputDirectory);

    HtmlComponentContentParser<Element> contentParser = new HtmlComponentContentParser(callBack);
    Resource resource = componentService.getContent(c);
    return contentParser.parse(resource);
  }
}
