package com.ownspec.center.util;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


public final class StaxUtils {

  private StaxUtils() {
  }

  /**
   * Stream filter to avoid indentation events
   *
   * @return StreamFilter
   */
  public static StreamFilter getIndentationFilter() {
    return new StreamFilter() {
      public boolean accept(XMLStreamReader r) {
        return !r.isWhiteSpace();
      }
    };
  }

  /**
   * Skip the current node (and its children) from xmlStreamReader
   *
   * @param reader
   * @throws XMLStreamException
   */
  public static void skipElement(XMLStreamReader reader) throws XMLStreamException {

    // number of elements read in
    int read = 0;
    int event = reader.getEventType();

    while (reader.hasNext()) {

      switch (event) {
        case XMLStreamConstants.START_ELEMENT:
          read++;
          break;
        case XMLStreamConstants.END_ELEMENT:
          read--;
          if (read <= 0) {
            return;
          }
          break;
      }
      event = reader.next();
    }
  }

  public static int nextTag(XMLStreamReader reader) throws XMLStreamException {
    int eventType = reader.next();
    while (eventType == XMLStreamConstants.CHARACTERS && reader.isWhiteSpace() ||
        eventType == XMLStreamConstants.CDATA && reader.isWhiteSpace() || eventType == XMLStreamConstants.SPACE ||
        eventType == XMLStreamConstants.PROCESSING_INSTRUCTION || eventType == XMLStreamConstants.COMMENT) {
      eventType = reader.next();
    }
    if (eventType != XMLStreamConstants.START_ELEMENT && eventType != XMLStreamConstants.END_ELEMENT) {
      throw new XMLStreamException("expected start or end tag", reader.getLocation());
    }
    return eventType;
  }
  public static int nextStartTag(XMLStreamReader reader) throws XMLStreamException {
    int eventType = reader.next();
    while (eventType == XMLStreamConstants.END_ELEMENT || eventType == XMLStreamConstants.CHARACTERS && reader.isWhiteSpace() ||
        eventType == XMLStreamConstants.CDATA && reader.isWhiteSpace() || eventType == XMLStreamConstants.SPACE ||
        eventType == XMLStreamConstants.PROCESSING_INSTRUCTION || eventType == XMLStreamConstants.COMMENT) {
      eventType = reader.next();
    }
    if (eventType != XMLStreamConstants.START_ELEMENT && eventType != XMLStreamConstants.END_ELEMENT) {
      throw new XMLStreamException("expected start or end tag", reader.getLocation());
    }
    return eventType;
  }

  /**
   * Execute {@link XMLStreamReader#nextTag()} and check the tag local name is equals to the given value.
   */
  public static void readNextTagAndValidateLocalName(final XMLStreamReader reader, final String expectedLocalName) throws XMLStreamException {
    reader.nextTag();

    String localName = reader.getLocalName();
    if (!localName.equals(expectedLocalName)) {
      throw new XMLStreamException("Invalid tag local name (expected: " + expectedLocalName + ", actual: " + localName + ").");
    }
  }

  /**
   * Execute {@link XMLStreamReader#next()} until the current value is a tag.
   */
  public static void readUntilNextTag(final XMLStreamReader reader) throws XMLStreamException {
    do {
      reader.next();
    } while (!reader.isStartElement() && !reader.isEndElement());
  }

  /**
   * Create a text element
   *
   * @param writer
   * @param name
   * @param text
   * @return
   * @throws XMLStreamException
   */
  public static XMLStreamWriter writeTextElement(XMLStreamWriter writer, String name, String text) throws XMLStreamException {
    writer.writeStartElement(name);
    writer.writeCharacters(text);
    writer.writeEndElement();
    return writer;
  }

}
