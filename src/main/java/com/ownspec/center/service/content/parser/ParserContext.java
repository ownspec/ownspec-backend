package com.ownspec.center.service.content.parser;

import org.immutables.value.Value;
import org.jsoup.nodes.Element;

import javax.annotation.Nullable;

/**
 * Created by nlabrot on 15/01/17.
 */
@Value.Immutable
@Value.Style(builder = "newParserContext")
public interface ParserContext {

  HtmlComponentContentParser getHtmlComponentContentParser();

  Element getElement();

  @Nullable
  String getNestedComponentId();

  @Nullable
  String getNestedWorkflowInstanceId();

  @Nullable
  String getNestedReferenceId();
}
