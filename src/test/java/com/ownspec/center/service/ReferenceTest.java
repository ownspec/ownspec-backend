package com.ownspec.center.service;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

import com.ctc.wstx.sax.WstxSAXParserFactory;
import org.cyberneko.html.parsers.DOMParser;
import org.cyberneko.html.parsers.SAXParser;
import org.junit.Test;
import org.nlab.xml.stream.XmlStreamSpec;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.FileInputStream;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMSource;

/**
 * Created by nlabrot on 23/10/16.
 */
public class ReferenceTest {

  @Test
  public void testExtractReference() throws Exception {
    DOMParser parser = new DOMParser();
    parser.parse(new InputSource("src/test/resources/reference/reference.html"));

    Document document = parser.getDocument();


    XmlStreamSpec.with(new DOMSource(document)).consumer()
        .matchCss("DIV[data-requirement-id]" , c -> {
          System.out.println(c.getNode().getAttribute("data-requirement-id"));
          System.out.println(c.getNode().getAttribute("data-workflow-instance-id"));
        }).consume();

  }
}
