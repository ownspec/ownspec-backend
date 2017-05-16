package com.ownspec;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.parser.microsoft.WordExtractor;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.apache.tika.sax.WriteOutContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by nlabrot on 11/05/17.
 */
public class WordTest {

  @Test
  public void name() throws Exception {

    ParseContext parseContext = new ParseContext();
    Metadata metadata = new Metadata();

    new WordExtractor(parseContext, metadata);

    TextExtractor textExtractor = new TextExtractor();
    textExtractor.process("src/test/resources/TDA08-02-DIE-01-15.doc");

    textExtractor.getString();
  }

  @Test
  public void name1() throws Exception {

    Tika tika = new Tika();
    tika.setMaxStringLength(10000000);
    String text = "";
    try {
      text = tika.parseToString(new FileInputStream("src/test/resources/TDA08-02-DIE-01-15.doc"));
    } catch (IOException ioe) {

    } catch (TikaException te) {

    }

    System.out.println(text);


  }

  @Test
  public void name3() throws Exception {
    OfficeParser officeParser = new OfficeParser();

    Metadata metadata = new Metadata();


    MyWordExtractor myWordExtractor = new MyWordExtractor(new ParseContext(), metadata);


    NPOIFSFileSystem npoifsFileSystem = new NPOIFSFileSystem(new File("src/test/resources/docx.docx"));


    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    StringWriter writer = new StringWriter();

    ToHTMLContentHandler toHTMLContentHandler1 = new ToHTMLContentHandler(byteArrayOutputStream, "UTF-8");


    WriteOutContentHandler handler =
        new WriteOutContentHandler(writer, 5000000);

    XHTMLContentHandler toHTMLContentHandler = new XHTMLContentHandler(toHTMLContentHandler1, metadata);

    toHTMLContentHandler.startDocument();

    myWordExtractor.parse(npoifsFileSystem, toHTMLContentHandler);

    //officeParser.parse(new FileInputStream("src/test/resources/TDA08-02-DIE-01-15.doc"), toHTMLContentHandler, metadata, new ParseContext());

    toHTMLContentHandler.endDocument();

    Files.write(Paths.get("target/foo.html"), byteArrayOutputStream.toByteArray());
    //Files.write(Paths.get("target/foo.html") , writer.toString().getBytes());


  }
}
