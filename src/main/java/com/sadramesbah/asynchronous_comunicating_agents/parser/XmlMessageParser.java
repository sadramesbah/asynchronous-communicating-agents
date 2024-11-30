package com.sadramesbah.asynchronous_comunicating_agents.parser;

import com.sadramesbah.asynchronous_comunicating_agents.config.XmlMessageConfig;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.xml.sax.SAXException;

@Component
public class XmlMessageParser {

  private final XmlMessageConfig xmlConfig;
  private final DocumentBuilder documentBuilder;

  public XmlMessageParser(XmlMessageConfig xmlConfig) throws ParserConfigurationException {
    this.xmlConfig = xmlConfig;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    this.documentBuilder = factory.newDocumentBuilder();
  }

  // parses the XML message in string and returns the Document object
  public Document parse(String xmlMessage) throws IOException, SAXException {
    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(
        xmlMessage.getBytes(StandardCharsets.UTF_8))) {
      Document document = documentBuilder.parse(inputStream);
      if (!isValid(document)) {
        throw new IOException("Invalid XML message");
      }
      return document;
    }
  }

  // checks if xml message contains all required fields
  private boolean isValid(Document document) {
    Element root = document.getDocumentElement();
    return xmlConfig.getFields().stream()
        .allMatch(field -> root.getElementsByTagName(field.getName()).getLength() > 0);
  }
}
