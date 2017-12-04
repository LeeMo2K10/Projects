package com.bizruntime.DOMParsing;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMService {
	static Logger logger = Logger.getLogger(DOMService.class);

	public static void parseXMLFile() {
		ResourceBundle rb = ResourceBundle.getBundle("path");
		try {

			File fXmlFile = new File(rb.getString("filepath"));
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			logger.debug("Root element :" + doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("staff");

			logger.info("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				logger.debug("\nCurrent Element :" + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					logger.debug("Staff id : " + eElement.getAttribute("id"));
					logger.debug("First Name : " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
					logger.debug("Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
					logger.debug("Nick Name : " + eElement.getElementsByTagName("nickname").item(0).getTextContent());
					logger.debug("Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());

				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	public static void main(String[] args) {
		logger.info("Hello World!");

		DOMService.parseXMLFile();
	}
}
