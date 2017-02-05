package com.ownspec.center.service.content;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.junit.Test;

import java.io.StringReader;

/**
 * Created by nlabrot on 15/01/17.
 */
public class TestCleaner {

  @Test
  public void name() throws Exception {


    CleanerProperties props = new CleanerProperties();

// set some properties to non-default values
    props.setTranslateSpecialEntities(true);
    props.setTransResCharsToNCR(true);
    props.setOmitComments(true);

// do parsing

    TagNode tagNode = new HtmlCleaner(props).clean(new StringReader("<html><body><img src='foooo.png'>&nbsp;</body></html>"));


// serialize to xml file
    new PrettyXmlSerializer(props).writeToFile(
        tagNode, "chinadaily.xml", "utf-8"
    );


  }
}
