package com.ownspec.center.service.content;

import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Not used for instance
 * <p>
 * toc is generated on client side
 * <p>
 * <p>
 * Created by nlabrot on 29/11/16.
 */
public class TocGenerator {

  public static final int MAX_DEPTH = 10;

  private int[] counter;

  private Pattern titlePattern = Pattern.compile("h(\\d+)");

  private Document docTocBuilder;

  public TocGenerator() {
    counter = new int[MAX_DEPTH];
  }

  public Document generate(Element root) {
    docTocBuilder = Document.createShell("foo");
    construct(root, docTocBuilder.body().appendElement("ul"));
    return docTocBuilder;
  }


  public void construct(Element current, Element ul) {

    for (Element element : current.children()) {
      Matcher matcher = titlePattern.matcher(element.tagName());
      if (matcher.matches()) {
        int level = Integer.valueOf(matcher.group(1)) - 1;
        reset(level + 1);
        counter[level]++;

        Pair<String, String> idAndNumber = generateIdAndNumber(level + 1);

        // Set title id
        element.attr("id" , idAndNumber.getLeft());
        // Generate toc entry with anchor
        ul.appendElement("li").appendElement("a").attr("href", "#" + idAndNumber.getLeft()).text(idAndNumber.getRight() + " " + element.text());
      }
      construct(element, ul);
    }
  }

  private void reset(int level) {
    for (int i = level; i < counter.length; i++) {
      counter[level] = 0;
    }
  }

  private Pair<String, String> generateIdAndNumber(int level) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < level; i++) {
      builder.append(counter[i]).append(".");
    }

    String number = builder.toString();

    return Pair.of(number.replaceAll("\\." , "_") , number);
  }

}
