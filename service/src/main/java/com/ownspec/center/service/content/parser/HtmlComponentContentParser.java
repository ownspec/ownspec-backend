package com.ownspec.center.service.content.parser;

import static com.ownspec.center.service.content.HtmlContentSaver.DATA_COMPONENT_VERSION_ID;
import static com.ownspec.center.service.content.HtmlContentSaver.DATA_REFERENCE_ID;
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


  private final ParserCallBack<T> cb;

  public HtmlComponentContentParser(ParserCallBack<T> cb) {
    this.cb = cb;
  }

  public static <T> T parse(Resource content, ParserCallBack<T> cb){
    return new HtmlComponentContentParser<>(cb).parse(content);
  }

  public static <T> T parse(String content, ParserCallBack<T> cb){
    return new HtmlComponentContentParser<>(cb).parse(content);
  }

  public static <T> T parse(Element element, ParserCallBack<T> cb){
    return new HtmlComponentContentParser<>(cb).parse(element);
  }


  public T parse(Resource content) {
    try (InputStream inputStream = content.getInputStream()) {
      Document document = Jsoup.parse(inputStream, "UTF-8", "foo.html");
      return parseComponent(document.body());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public T parse(String content) {
    try {
      Document document = Jsoup.parse(content, "foo.html");
      return parseComponent(document.body());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  public T parse(Element elem) {
    try {
      return parseComponent(elem);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  private T parseComponent(Element parent) {
    Deque<Element> stack = new LinkedList<>(parent.children());
    while (!stack.isEmpty()) {
      Element element = stack.pop();

      if ("div".equals(element.nodeName()) && element.hasAttr(DATA_COMPONENT_VERSION_ID)) {
        ImmutableParserContext parserContext = newParserContext()
            .htmlComponentContentParser(this)
            .element(element)
            .nestedReferenceId(element.attr(DATA_REFERENCE_ID))
            .nestedComponentId(element.attr(DATA_COMPONENT_VERSION_ID))
            .build();


        cb.parseReference(parserContext);

      } else if ("img".equals(element.nodeName()) && element.hasAttr(DATA_COMPONENT_VERSION_ID)) {
        ImmutableParserContext parserContext = newParserContext()
            .htmlComponentContentParser(this)
            .element(element)
            .nestedReferenceId(element.attr(DATA_REFERENCE_ID))
            .nestedComponentId(element.attr(DATA_COMPONENT_VERSION_ID))
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
