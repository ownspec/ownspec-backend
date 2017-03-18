package com.ownspec.center.diff;

import org.junit.Test;
import org.outerj.daisy.diff.DaisyDiff;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

import java.util.Locale;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by nlabrot on 01/10/16.
 */
public class DiffTest {
  @Test
  public void name() throws Exception {

    ClassPathResource a = new ClassPathResource("diff/a.html");
    ClassPathResource b = new ClassPathResource("diff/b.html");

    SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory
        .newInstance();
    TransformerHandler result = tf.newTransformerHandler();
    // If the file path were malformed, then the following
    result.setResult(new StreamResult("target/out.html"));


    result.startDocument();
    result.startElement("", "diffreport", "diffreport", new AttributesImpl());
    //doCSS(css, postProcess);
    result.startElement("", "diff", "diff", new AttributesImpl());


    DaisyDiff.diffHTML(new InputSource(a.getInputStream()) , new InputSource(b.getInputStream())  , result , "diff" , Locale.getDefault());
    //DaisyDiff.diffTag(new BufferedReader(new InputStreamReader(a.getInputStream())), new BufferedReader(new InputStreamReader(b.getInputStream())), result);


    result.endElement("", "diff", "diff");
    result.endElement("", "diffreport", "diffreport");
    result.endDocument();

  }

  @Test
  public void name2() throws Exception {
    //Main.main(new String[]{"src/test/resources/diff/a.html", "src/test/resources/diff/b.html"});

    ClassPathResource a = new ClassPathResource("diff/a.html");
    ClassPathResource b = new ClassPathResource("diff/b.html");
    //doDiff(a,b);

  }


}
