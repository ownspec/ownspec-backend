package com.ownspec.center.service.content.parser;

import static com.ownspec.center.service.content.HtmlContentSaver.DATA_REFERENCE_ID;
import static com.ownspec.center.service.content.HtmlContentSaver.DATA_REQUIREMENT_ID;
import static com.ownspec.center.service.content.HtmlContentSaver.DATA_WORKFLOW_INSTANCE_ID;
import static com.ownspec.center.service.content.parser.ImmutableParserContext.newParserContext;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by nlabrot on 15/01/17.
 */
public class HtmlComponentContentParser<T> {

  public T parse(Resource content, ParserCallBack<T> cb) {

    try (InputStream inputStream = content.getInputStream()) {
      Document document = Jsoup.parse(inputStream, "UTF-8", "foo.html");
      return parseComponent(document.body(), cb);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  private T parseComponent(Element parent, ParserCallBack<T> cb) {
    Deque<Element> stack = new LinkedList<>(parent.children());
    while (!stack.isEmpty()) {
      Element element = stack.pop();

      if ("div".equals(element.nodeName()) && element.hasAttr(DATA_REQUIREMENT_ID)) {
        ImmutableParserContext parserContext = newParserContext()
            .htmlComponentContentParser(this)
            .element(element)
            .nestedReferenceId(element.attr(DATA_REFERENCE_ID))
            .nestedComponentId(element.attr(DATA_REQUIREMENT_ID))
            .nestedWorkflowInstanceId(element.attr(DATA_WORKFLOW_INSTANCE_ID))
            .build();


        cb.parseReference(parserContext);

      } else if ("img".equals(element.nodeName()) && element.hasAttr(DATA_REQUIREMENT_ID)) {
        ImmutableParserContext parserContext = newParserContext()
            .htmlComponentContentParser(this)
            .element(element)
            .nestedReferenceId(element.attr(DATA_REFERENCE_ID))
            .nestedComponentId(element.attr(DATA_REQUIREMENT_ID))
            .nestedWorkflowInstanceId(element.attr(DATA_WORKFLOW_INSTANCE_ID))
            .build();

        cb.parseResource(parserContext);
      } else if ("div".equals(element.nodeName()) && element.hasClass("toc")) {
        ImmutableParserContext parserContext = newParserContext()
            .htmlComponentContentParser(this)
            .element(element)
            .build();

        cb.parseToc(parent,parserContext);

      } else {
        stack.addAll(element.children());
      }
    }
    return cb.endComponent(parent);
  }

}
