package com.app.document;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class DomParser {
	public static void main(String[] args) throws Exception {
		// new Dom();
		// new Sax();
		new Jackson();
	}
}

class Dom {
	public Dom() {
		executeParse();
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
	
	public void executeParse() {
		try {
			InputStream inputStream = DomParser.class.getResourceAsStream("/book.xml");
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(inputStream);
			
			Element root = document.getDocumentElement();
			traverse(root, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

// 基于流的解析方式，边读取XML边解析，并以事件回调方式让调用者获取数据
// 因可一边读一边解析，所以不管XML多大，占用的内存都很小
class Sax {
	public Sax() {
		executeParse();
	}

	public void executeParse() {
		InputStream inputStream = DomParser.class.getResourceAsStream("/book.xml");
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = saxParserFactory.newSAXParser();
			saxParser.parse(inputStream, new Handler());
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}
	
	class Handler extends DefaultHandler {
		void print(Object... objs) {
			for (Object obj : objs) {
				System.out.print(obj + " ");
			}
			System.out.println();
		}
		public void startDocument() throws SAXException {
			print("start document");
		}
		
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			print("start element:", localName, qName);
		}
		
		public void endElement(String uri, String localName, String qName) throws SAXException {
			print("end element:", localName, qName);
		}
		
		public void characters(char[] ch, int start, int length) throws SAXException {
			print("characters:", new String(ch, start, length));
		}
		
		public void error(SAXParseException e) throws SAXException {
			print("error:", e);
		}
		
		public void endDocument() throws SAXException {
			print("end document");
		}
		
	}
}

class Book {
	public long id;
	public String name;
	public String author;
	public String isbn;
	public List<String> tags;
	public String pubDate;
}

class Jackson {
	public Jackson() {
		executeParse();
	}
	
	public void executeParse() {
		InputStream inputStream = DomParser.class.getResourceAsStream("/book.xml");
		JacksonXmlModule jacksonXmlModule = new JacksonXmlModule();
		XmlMapper xmlMapper = new XmlMapper(jacksonXmlModule);
		
		try {
			Book book = xmlMapper.readValue(inputStream, Book.class);
			System.out.println("id: " + book.id);
			System.out.println("name: " + book.name);
			System.out.println("author: " + book.author);
			System.out.println("isbn: " + book.isbn);
			System.out.println("tags: " + book.tags);
			System.out.println("pubDate: " + book.pubDate);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
