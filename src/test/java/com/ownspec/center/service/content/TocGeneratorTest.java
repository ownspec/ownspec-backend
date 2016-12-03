package com.ownspec.center.service.content;

import org.junit.Test;

/**
 * Created by nlabrot on 29/11/16.
 */
public class TocGeneratorTest {

  String content = "<h1>test</h1>\n" +
      "\n" +
      "<p>fffffffdddddddddddddddddddddddddddddd</p>\n" +
      "\n" +
      "<h3>ddddddddd</h3>\n" +
      "\n" +
      "<p>ddddddddddd</p>\n" +
      "\n" +
      "<h3>ddddddddddddd</h3>\n" +
      "\n" +
      "<h2>d</h2>\n" +
      "\n" +
      "<h2>dddddddddddd</h2>\n" +
      "\n" +
      "<h3>ddddddddddddd</h3>\n" +
      "\n" +
      "<h1>ddddddddddddd</h1>\n" +
      "\n" +
      "<h3>ddddd</h3>\n" +
      "\n" +
      "<p>&nbsp;</p>\n" +
      "\n" +
      "<div class=\"requirements\" data-requirement-id=\"134\" data-workflow-instance-id=\"133\">\n" +
      "<div class=\"requirements-id\">134</div>\n" +
      "\n" +
      "<div class=\"requirements-content\" contenteditable=\"false\">test</div>\n" +
      "</div>\n" +
      "\n" +
      "<p>&nbsp;</p>\n" +
      "\n" +
      "<div class=\"requirements\" data-requirement-id=\"117\" data-workflow-instance-id=\"116\">\n" +
      "<div class=\"requirements-id\">117</div>\n" +
      "\n" +
      "<div class=\"requirements-content\" contenteditable=\"false\">test</div>\n" +
      "</div>\n" +
      "\n" +
      "<p>&nbsp;</p>\n";

  @Test
  public void name() throws Exception {


    TocGenerator tocGenerator = new TocGenerator();

    System.out.println(tocGenerator.generate(content));

  }
}
