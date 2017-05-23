package com.ownspec.center.service.component.importer;


import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.model.XWPFCommentsDecorator;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.BodyType;
import org.apache.poi.xwpf.usermodel.IBody;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.ICell;
import org.apache.poi.xwpf.usermodel.IRunElement;
import org.apache.poi.xwpf.usermodel.ISDTContent;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeaderFooter;
import org.apache.poi.xwpf.usermodel.XWPFHyperlink;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFSDT;
import org.apache.poi.xwpf.usermodel.XWPFSDTCell;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.WordExtractor;
import org.apache.tika.parser.microsoft.WordExtractor.TagAndStyle;
import org.apache.tika.parser.microsoft.ooxml.AbstractOOXMLExtractor;
import org.apache.tika.sax.XHTMLContentHandler;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

public class OsXWPFWordExtractorDecorator extends AbstractOOXMLExtractor {

  // could be improved by using the real delimiter in xchFollow [MS-DOC], v20140721, 2.4.6.3, Part 3, Step 3
  private static final String LIST_DELIMITER = " ";


  private XWPFDocument document;
  private XWPFStyles styles;

  public OsXWPFWordExtractorDecorator(ParseContext context, XWPFWordExtractor extractor) {
    super(context, extractor);

    document = (XWPFDocument) extractor.getDocument();
    styles = document.getStyles();
  }

  /**
   * @see org.apache.poi.xwpf.extractor.XWPFWordExtractor#getText()
   */
  @Override
  protected void buildXHTML(XHTMLContentHandler xhtml)
      throws SAXException, XmlException, IOException {
    XWPFHeaderFooterPolicy hfPolicy = document.getHeaderFooterPolicy();
    OsXWPFListManager listManager = new OsXWPFListManager(document);
    // headers
    if (hfPolicy != null) {
      extractHeaders(xhtml, hfPolicy, listManager);
    }

    // process text in the order that it occurs in
    extractIBodyText(document, listManager, xhtml);

    // then all document tables
    if (hfPolicy != null) {
      extractFooters(xhtml, hfPolicy, listManager);
    }
  }


  private ArrayDeque<ListContext> lists = new ArrayDeque<>();

  private static class ListContext {
    int abstractNumberingId = -1;
    int lastNumIlvl = -1;
    String format;
    String tag;

    public ListContext(int abstractNumberingId, int lastNumIlvl, String format, String tag) {
      this.abstractNumberingId = abstractNumberingId;
      this.lastNumIlvl = lastNumIlvl;
      this.format = format;
      this.tag = tag;
    }
  }


  private void extractIBodyText(IBody bodyElement, OsXWPFListManager listManager,
                                XHTMLContentHandler xhtml)
      throws SAXException, XmlException, IOException {
    for (IBodyElement element : bodyElement.getBodyElements()) {
      if (element instanceof XWPFParagraph) {

        XWPFParagraph paragraph = (XWPFParagraph) element;

        if (listManager.isList(paragraph)) {
          // List, get the list style id, list format and level

          String number = String.valueOf(listManager.getNumber(paragraph));

          int numIlvl = paragraph.getNumIlvl().intValue();
          Integer abstractNumberingId = listManager.getAbstractNumberingId(paragraph);
          String format = listManager.getNumberingType(paragraph);
          String tag = "bullet".equals(format) ? "ul" : "ol";
          ListContext currentContext = new ListContext(abstractNumberingId, numIlvl, format, tag);

          int currentLevel = lists.size() - 1;

          if (currentLevel == numIlvl) {
            // Same level, get the last list parameter
            reuseOrCreate(xhtml, abstractNumberingId, format, currentContext, lists.peek(), number);

          } else if (currentLevel < numIlvl) {
            // new level is greater
            // in case level jump is greater than 1, fill the gap
            while (currentLevel + 1 < numIlvl) {
              // fill with ul
              xhtml.startElement("ul", "start", number);
              lists.push(new ListContext(Integer.MAX_VALUE, lists.size(), "bullet", "ul"));
              currentLevel++;
            }
            // then open the new one
            lists.push(currentContext);
            xhtml.startElement(currentContext.tag, "start", number);


          } else {
            // new level is lower
            // in case level jump is lower than 1, close the gap
            while (currentLevel > numIlvl) {
              xhtml.endElement(lists.pop().tag);
              currentLevel--;
            }
            // Then reuseOrCreate the prevContext with the current one
            reuseOrCreate(xhtml, abstractNumberingId, format, currentContext, lists.peek(), number);
          }


        } else {
          // close the list tag
          while (!lists.isEmpty()) {
            xhtml.endElement(lists.pop().tag);
          }
        }

        extractParagraph(paragraph, listManager, xhtml);


      }
      if (element instanceof XWPFTable) {
        XWPFTable table = (XWPFTable) element;
        extractTable(table, listManager, xhtml);
      }
      if (element instanceof XWPFSDT) {
        extractSDT((XWPFSDT) element, xhtml);
      }

    }
  }

  /**
   * If the prevContext is equals to the current do nothing
   * If the prevContent and the current have a bullet format do nothing
   * Else, close the prevContext and open the current one
   *
   * @param xhtml
   * @param abstractNumberingId
   * @param format
   * @param currentContext
   * @param prevContext
   * @param number
   * @throws SAXException
   */
  private void reuseOrCreate(XHTMLContentHandler xhtml, Integer abstractNumberingId, String format, ListContext currentContext, ListContext prevContext, String number) throws SAXException {
    // Compare the list parameter with the current
    if (prevContext.abstractNumberingId == abstractNumberingId) {
      // Same list, do nothing
    } else {
      // If the format is the same and is bullet do nothing else close the previous list
      if (prevContext.format.equals(format) && "bullet".equals(format)) {
        // Same list, do nothing
      } else {
        // Close the previous list, then open the new one
        xhtml.endElement(lists.pop().tag);
        lists.push(currentContext);
        xhtml.startElement(currentContext.tag, "start", number);
      }
    }
  }

  private void extractSDT(XWPFSDT element, XHTMLContentHandler xhtml) throws SAXException,
      XmlException, IOException {
    ISDTContent content = element.getContent();
    String tag = "p";
    xhtml.startElement(tag);
    xhtml.characters(content.getText());
    xhtml.endElement(tag);
  }

  private void extractParagraph(XWPFParagraph paragraph, OsXWPFListManager listManager,
                                XHTMLContentHandler xhtml)
      throws SAXException, XmlException, IOException {
    // If this paragraph is actually a whole new section, then
    //  it could have its own headers and footers
    // Check and handle if so

    System.out.println(listManager.getAbstractNumberingId(paragraph) + " " + listManager.getNumberingType(paragraph));


    XWPFHeaderFooterPolicy headerFooterPolicy = null;
    if (paragraph.getCTP().getPPr() != null) {
      CTSectPr ctSectPr = paragraph.getCTP().getPPr().getSectPr();
      if (ctSectPr != null) {
        headerFooterPolicy =
            new XWPFHeaderFooterPolicy(document, ctSectPr);
        extractHeaders(xhtml, headerFooterPolicy, listManager);
      }
    }

    // Is this a paragraph, or a heading?
    String tag = "p";
    String styleClass = null;


    //if (listManager.getNumberingType(paragraph))
    if (listManager.isList(paragraph)) {
      tag = "li";
    } else if (paragraph.getStyleID() != null) {
      XWPFStyle style = styles.getStyle(paragraph.getStyleID());

      if (style != null && style.getName() != null) {
        TagAndStyle tas = WordExtractor.buildParagraphTagAndStyle(style.getName(), paragraph.getPartType() == BodyType.TABLECELL);
        tag = tas.getTag();
        styleClass = tas.getStyleClass();
      }
    }

    if (styleClass == null) {
      xhtml.startElement(tag);
    } else {
      xhtml.startElement(tag, "class", styleClass);
    }

    //writeParagraphNumber(paragraph, listManager, xhtml);
    // Output placeholder for any embedded docs:

    // TODO: replace w/ XPath/XQuery:
    for (XWPFRun run : paragraph.getRuns()) {
      XmlCursor c = run.getCTR().newCursor();
      c.selectPath("./*");
      while (c.toNextSelection()) {
        XmlObject o = c.getObject();
        if (o instanceof CTObject) {
          XmlCursor c2 = o.newCursor();
          c2.selectPath("./*");
          while (c2.toNextSelection()) {
            XmlObject o2 = c2.getObject();

            XmlObject embedAtt = o2.selectAttribute(new QName("Type"));
            if (embedAtt != null && embedAtt.getDomNode().getNodeValue().equals("Embed")) {
              // Type is "Embed"
              XmlObject relIDAtt = o2.selectAttribute(new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id"));
              if (relIDAtt != null) {
                String relID = relIDAtt.getDomNode().getNodeValue();
                AttributesImpl attributes = new AttributesImpl();
                attributes.addAttribute("", "class", "class", "CDATA", "embedded");
                attributes.addAttribute("", "id", "id", "CDATA", relID);
                xhtml.startElement("div", attributes);
                xhtml.endElement("div");
              }
            }
          }
          c2.dispose();
        }
      }

      c.dispose();
    }

    // Attach bookmarks for the paragraph
    // (In future, we might put them in the right place, for now
    //  we just put them in the correct paragraph)
    for (int i = 0; i < paragraph.getCTP().sizeOfBookmarkStartArray(); i++) {
      CTBookmark bookmark = paragraph.getCTP().getBookmarkStartArray(i);
      xhtml.startElement("a", "name", bookmark.getName());
      xhtml.endElement("a");
    }

    TmpFormatting fmtg = new TmpFormatting(false, false);

    //hyperlinks may or may not have hyperlink ids
    String lastHyperlinkId = null;
    boolean inHyperlink = false;
    // Do the iruns
    for (IRunElement run : paragraph.getIRuns()) {

      if (run instanceof XWPFHyperlinkRun) {
        XWPFHyperlinkRun hyperlinkRun = (XWPFHyperlinkRun) run;
        if (hyperlinkRun.getHyperlinkId() == null ||
            !hyperlinkRun.getHyperlinkId().equals(lastHyperlinkId)) {
          if (inHyperlink) {
            //close out the old one
            xhtml.endElement("a");
            inHyperlink = false;
          }
          lastHyperlinkId = hyperlinkRun.getHyperlinkId();
          fmtg = closeStyleTags(xhtml, fmtg);
          XWPFHyperlink link = hyperlinkRun.getHyperlink(document);
          if (link != null && link.getURL() != null) {
            xhtml.startElement("a", "href", link.getURL());
            inHyperlink = true;
          } else if (hyperlinkRun.getAnchor() != null && hyperlinkRun.getAnchor().length() > 0) {
            xhtml.startElement("a", "href", "#" + hyperlinkRun.getAnchor());
            inHyperlink = true;
          }
        }
      } else if (inHyperlink) {
        //if this isn't a hyperlink, but the last one was
        closeStyleTags(xhtml, fmtg);
        xhtml.endElement("a");
        lastHyperlinkId = null;
        inHyperlink = false;
      }

      if (run instanceof XWPFSDT) {
        fmtg = closeStyleTags(xhtml, fmtg);
        processSDTRun((XWPFSDT) run, xhtml);
        //for now, we're ignoring formatting in sdt
        //if you hit an sdt reset to false
        fmtg.setBold(false);
        fmtg.setItalic(false);
      } else {
        fmtg = processRun((XWPFRun) run, paragraph, xhtml, fmtg);
      }
    }
    closeStyleTags(xhtml, fmtg);
    if (inHyperlink) {
      xhtml.endElement("a");
    }


    // Now do any comments for the paragraph
    XWPFCommentsDecorator comments = new XWPFCommentsDecorator(paragraph, null);
    String commentText = comments.getCommentText();
    if (commentText != null && commentText.length() > 0) {
      xhtml.characters(commentText);
    }

    String footnameText = paragraph.getFootnoteText();
    if (footnameText != null && footnameText.length() > 0) {
      xhtml.characters(footnameText + "\n");
    }

    // Also extract any paragraphs embedded in text boxes:
    for (XmlObject embeddedParagraph : paragraph.getCTP().selectPath("declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' declare namespace wps='http://schemas.microsoft.com/office/word/2010/wordprocessingShape' .//*/wps:txbx/w:txbxContent/w:p")) {
      extractParagraph(new XWPFParagraph(CTP.Factory.parse(embeddedParagraph.xmlText()), paragraph.getBody()), listManager, xhtml);
    }

    // Finish this paragraph
    xhtml.endElement(tag);

    if (headerFooterPolicy != null) {
      extractFooters(xhtml, headerFooterPolicy, listManager);
    }
  }

  private void writeParagraphNumber(XWPFParagraph paragraph,
                                    OsXWPFListManager listManager,
                                    XHTMLContentHandler xhtml) throws SAXException {
    if (paragraph.getNumIlvl() == null) {
      return;
    }


    String number = listManager.getFormattedNumber(paragraph);
    if (number != null) {
      xhtml.characters(number);
    }

  }

  private TmpFormatting closeStyleTags(XHTMLContentHandler xhtml,
                                       TmpFormatting fmtg) throws SAXException {
    // Close any still open style tags
    if (fmtg.isItalic()) {
      xhtml.endElement("i");
      fmtg.setItalic(false);
    }
    if (fmtg.isBold()) {
      xhtml.endElement("b");
      fmtg.setBold(false);
    }
    return fmtg;
  }

  private TmpFormatting processRun(XWPFRun run, XWPFParagraph paragraph,
                                   XHTMLContentHandler xhtml, TmpFormatting tfmtg)
      throws SAXException, XmlException, IOException {
    // True if we are currently in the named style tag:
    if (run.isBold() != tfmtg.isBold()) {
      if (tfmtg.isItalic()) {
        xhtml.endElement("i");
        tfmtg.setItalic(false);
      }
      if (run.isBold()) {
        xhtml.startElement("b");
      } else {
        xhtml.endElement("b");
      }
      tfmtg.setBold(run.isBold());
    }

    if (run.isItalic() != tfmtg.isItalic()) {
      if (run.isItalic()) {
        xhtml.startElement("i");
      } else {
        xhtml.endElement("i");
      }
      tfmtg.setItalic(run.isItalic());
    }

    xhtml.characters(run.toString());

    // If we have any pictures, output them
    for (XWPFPicture picture : run.getEmbeddedPictures()) {
      if (paragraph.getDocument() != null) {
        XWPFPictureData data = picture.getPictureData();
        if (data != null) {
          AttributesImpl attr = new AttributesImpl();

          attr.addAttribute("", "src", "src", "CDATA", "embedded:" + data.getFileName());
          attr.addAttribute("", "alt", "alt", "CDATA", picture.getDescription());

          xhtml.startElement("img", attr);
          xhtml.endElement("img");
        }
      }
    }

    return tfmtg;
  }

  private void processSDTRun(XWPFSDT run, XHTMLContentHandler xhtml)
      throws SAXException, XmlException, IOException {
    xhtml.characters(run.getContent().getText());
  }

  private void extractTable(XWPFTable table, OsXWPFListManager listManager,
                            XHTMLContentHandler xhtml)
      throws SAXException, XmlException, IOException {
    xhtml.startElement("table");
    xhtml.startElement("tbody");
    for (XWPFTableRow row : table.getRows()) {
      xhtml.startElement("tr");
      for (ICell cell : row.getTableICells()) {
        xhtml.startElement("td");
        if (cell instanceof XWPFTableCell) {
          extractIBodyText((XWPFTableCell) cell, listManager, xhtml);
        } else if (cell instanceof XWPFSDTCell) {
          xhtml.characters(((XWPFSDTCell) cell).getContent().getText());
        }
        xhtml.endElement("td");
      }
      xhtml.endElement("tr");
    }
    xhtml.endElement("tbody");
    xhtml.endElement("table");
  }

  private void extractFooters(
      XHTMLContentHandler xhtml, XWPFHeaderFooterPolicy hfPolicy,
      OsXWPFListManager listManager)
      throws SAXException, XmlException, IOException {
    // footers
    if (hfPolicy.getFirstPageFooter() != null) {
      extractHeaderText(xhtml, hfPolicy.getFirstPageFooter(), listManager);
    }
    if (hfPolicy.getEvenPageFooter() != null) {
      extractHeaderText(xhtml, hfPolicy.getEvenPageFooter(), listManager);
    }
    if (hfPolicy.getDefaultFooter() != null) {
      extractHeaderText(xhtml, hfPolicy.getDefaultFooter(), listManager);
    }
  }

  private void extractHeaders(
      XHTMLContentHandler xhtml, XWPFHeaderFooterPolicy hfPolicy, OsXWPFListManager listManager)
      throws SAXException, XmlException, IOException {
    if (hfPolicy == null) {
      return;
    }

    if (hfPolicy.getFirstPageHeader() != null) {
      extractHeaderText(xhtml, hfPolicy.getFirstPageHeader(), listManager);
    }

    if (hfPolicy.getEvenPageHeader() != null) {
      extractHeaderText(xhtml, hfPolicy.getEvenPageHeader(), listManager);
    }

    if (hfPolicy.getDefaultHeader() != null) {
      extractHeaderText(xhtml, hfPolicy.getDefaultHeader(), listManager);
    }
  }

  private void extractHeaderText(XHTMLContentHandler xhtml, XWPFHeaderFooter header, OsXWPFListManager listManager) throws SAXException, XmlException, IOException {

    for (IBodyElement e : header.getBodyElements()) {
      if (e instanceof XWPFParagraph) {
        extractParagraph((XWPFParagraph) e, listManager, xhtml);
      } else if (e instanceof XWPFTable) {
        extractTable((XWPFTable) e, listManager, xhtml);
      } else if (e instanceof XWPFSDT) {
        extractSDT((XWPFSDT) e, xhtml);
      }
    }
  }

  /**
   * Word documents are simple, they only have the one
   * main part
   */
  @Override
  protected List<PackagePart> getMainDocumentParts() {
    List<PackagePart> parts = new ArrayList<PackagePart>();
    parts.add(document.getPackagePart());
    return parts;
  }

  private class TmpFormatting {
    private boolean bold = false;
    private boolean italic = false;

    private TmpFormatting(boolean bold, boolean italic) {
      this.bold = bold;
      this.italic = italic;
    }

    public boolean isBold() {
      return bold;
    }

    public void setBold(boolean bold) {
      this.bold = bold;
    }

    public boolean isItalic() {
      return italic;
    }

    public void setItalic(boolean italic) {
      this.italic = italic;
    }

  }

}
