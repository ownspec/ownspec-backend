package com.ownspec.center.service.component.importer;

import static java.nio.charset.StandardCharsets.UTF_8;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.jsoup.Jsoup;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;

/**
 * Created by nlabrot on 16/05/2017.
 */
@Service
@Transactional
@Slf4j
public class ComponentServiceImporter {

  public Resource importComponent(Path docxFile) throws Exception {
    XWPFWordExtractor extractor =
        new XWPFWordExtractor(POIXMLDocument.openPackage(docxFile.toString()));

    OsXWPFWordExtractorDecorator xwpfWordExtractorDecorator = new OsXWPFWordExtractorDecorator(new ParseContext(), extractor);

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ToHTMLContentHandler toHTMLContentHandler1 = new ToHTMLContentHandler(byteArrayOutputStream, "UTF-8");
    xwpfWordExtractorDecorator.getXHTML(toHTMLContentHandler1, new Metadata(), new ParseContext());

    String content = new String(byteArrayOutputStream.toByteArray(), UTF_8);

    return new ByteArrayResource(Jsoup.parse(content).body().attr("class", "cke_editable").html().getBytes(UTF_8));
  }

}
