package com.ownspec.center.service.content.parser;

import org.jsoup.nodes.Element;

/**
 * Created by nlabrot on 15/01/17.
 */
public interface ParserCallBack<T> {

  void parseReference(ParserContext parserContext);

  void parseResource(ParserContext parserContext);

  T endComponent(Element body);

  default void parseToc(Element parent, ParserContext parserContext){}
}
