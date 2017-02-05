package com.ownspec.center.service.content;

import com.ownspec.center.util.OsUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * Created by nlabrot on 29/11/16.
 */
public class TocGeneratorTest {


  @Test
  public void name() throws Exception {


    TocGenerator tocGenerator = new TocGenerator();


    Document document = Jsoup.parse(OsUtils.toString(new ClassPathResource("service/content/toc/input.txt")));

    Document toc = tocGenerator.generate(document.body());


    Assert.assertEquals(OsUtils.toString(new ClassPathResource("service/content/toc/output.txt")).replaceAll("\\s" , ""), document.html().replaceAll("\\s" , ""));
    Assert.assertEquals(OsUtils.toString(new ClassPathResource("service/content/toc/toc.txt")).replaceAll("\\s" , ""), toc.html().replaceAll("\\s" , ""));


  }
}
