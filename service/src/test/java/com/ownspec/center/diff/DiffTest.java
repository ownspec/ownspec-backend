package com.ownspec.center.diff;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.coyote.http11.Constants.a;

import com.ownspec.center.AbstractTest;
import com.ownspec.center.dto.ImmutableComponentDto;
import com.ownspec.center.dto.WorkflowStatusDto;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.workflow.Status;
import org.junit.Assert;
import org.junit.Test;
import org.outerj.daisy.diff.DaisyDiff;
import org.outerj.daisy.diff.HtmlCleaner;
import org.outerj.daisy.diff.Main;
import org.outerj.daisy.diff.XslFilter;
import org.outerj.daisy.diff.html.HTMLDiffer;
import org.outerj.daisy.diff.html.HtmlSaxDiffOutput;
import org.outerj.daisy.diff.html.TextNodeComparator;
import org.outerj.daisy.diff.html.dom.DomTreeBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
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
