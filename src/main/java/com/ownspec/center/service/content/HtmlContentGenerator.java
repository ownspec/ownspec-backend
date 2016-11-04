package com.ownspec.center.service.content;

import com.ownspec.center.model.component.Component;
import com.ownspec.center.repository.component.ComponentRepository;
import com.ownspec.center.service.ComponentService;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by nlabrot on 03/11/16.
 */
public class HtmlContentGenerator {

  @Autowired
  private ComponentService componentService;

  @Autowired
  private ComponentRepository componentRepository;

  @Value("${component.content.summary-length:80}")
  private int summaryLength;


  public Pair<String, String> generate(Component c){

    String content = componentService.getRawContent(c);

    Document document = Jsoup.parse(content);

    Element body = document.getElementsByTag("body").first();

    generateContent(c, document, body);

    String substring = body.text().replaceAll("(?<=.{"+summaryLength+"})\\b.*", "...");

    return Pair.of(body.html() , substring);
  }

  private void generateContent(Component c, Document doc, Element parent) {

    Deque<Element> stack = new LinkedList<>(parent.children());

    while (!stack.isEmpty()) {
      Element element = stack.pop();

      if ("div".equals(element.nodeName()) && element.hasAttr("data-requirement-id")) {
        Long targetComponentId = Long.valueOf(element.attr("data-requirement-id"));
        Component nestedComponent = componentRepository.findOne(targetComponentId);

        Document nestedDocument = Jsoup.parse(componentService.getRawContent(nestedComponent));
        Element nestedBody = nestedDocument.getElementsByTag("body").first();

        // Create title tag
        element.appendChild(doc.createElement("div").addClass("requirements-id").text(nestedComponent.getId().toString()));

        // Extract reference from the nested reference content (second children, first children is the title)
        generateContent(nestedComponent, nestedDocument, nestedBody);

        //Create content tag
        Element nestedContent = doc.createElement("div").addClass("requirements-content");
        new ArrayList<>(nestedBody.childNodes()).forEach(nestedContent::appendChild);
        element.appendChild(nestedContent);

      } else {
        stack.addAll(element.children());
      }
    }
  }

}
