package com.app.document;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomParser {
	public static void main(String[] args) throws Exception {
		InputStream inputStream = DomParser.class.getResourceAsStream("/book.xml");
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(inputStream);

		Element root = document.getDocumentElement();
		traverse(root, 0);
	}

	private static void traverse(Node node, int depth) {

		String indent = StringUtils.repeat("  ", depth);
		System.out.print(indent);

		switch (node.getNodeType()) {
		case Node.DOCUMENT_NODE:
			System.out.println("Document: " + node.getNodeName());
			break;
			
		case Node.ELEMENT_NODE:
			Element elem = (Element) node;
			System.out.println("Element: " + elem.getNodeName());
			break;
			
		case Node.TEXT_NODE:
			System.out.println("Text: " + node.getNodeName() + " = " + node.getNodeValue());
			break;
			
		case Node.ATTRIBUTE_NODE:
			System.out.println("Attribute: " + node.getNodeName() + " = " + node.getNodeValue());
			break;
			
		default:
			System.out.println("NodeType: " + node.getNodeType() + ", NodeName: " + node.getNodeName());
		}
		
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i ++) {
			traverse(children.item(i), depth + 1);
		}
	}
}
