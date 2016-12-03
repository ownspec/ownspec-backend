package com.ownspec.center.service.content;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Not used for instance
 *
 * toc is generated on client side
 *
 *
 * Created by nlabrot on 29/11/16.
 */
public class TocGenerator {

  public static final int MAX_DEPTH = 10;

  private int[] counter;

  private Pattern titlePattern = Pattern.compile("h(\\d+)");

  private StringBuilder tocBuilder = new StringBuilder();

  public TocGenerator() {
    counter = new int[MAX_DEPTH];
  }

  public String generate(String content) {

    Document document = Jsoup.parse(content);

    tocBuilder.append("<ul>\n");

    construct(document.body());

    tocBuilder.append("</ul>\n");

    return tocBuilder.toString();
  }

  public void construct(Element current) {

    for (Element element : current.children()) {
      Matcher matcher = titlePattern.matcher(element.tagName());
      if (matcher.matches()) {
        int level = Integer.valueOf(matcher.group(1)) - 1;
        reset(level + 1);
        counter[level]++;

        tocBuilder.append("<li><a>")
            .append(generateNumero(level + 1) + " " + element.text())
            .append("</a></li>\n");
      }
      construct(element);
    }
  }

  private void reset(int level) {
    for (int i = level; i < counter.length; i++) {
      counter[level] = 0;
    }
  }

  private String generateNumero(int level) {

    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < level; i++) {
      builder.append(counter[i]).append(".");
    }
    return builder.toString();
  }

}
