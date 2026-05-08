package com.commander4j.xml;

import java.io.File;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class JXMLDocument
{
	private DocumentBuilder builder;
	private Document document;
	private XPath xpath = XPathFactory.newInstance().newXPath();

	public Document getDocument()
	{
		return document;
	}

	public JXMLDocument()
	{
		try
		{
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch (Exception ex)
		{
			System.err.println("JXMLDocument constructor " + ex.getMessage());
		}
	}

	public JXMLDocument(String filename)
	{
		this();
		setDocument(filename);
	}

	public String findXPath(String path)
	{
		String result = "";

		try
		{
			Node widgetNode = (Node) xpath.evaluate(path, document, XPathConstants.NODE);
			result = widgetNode.getFirstChild().getNodeValue().toString();
		}
		catch (Exception ex)
		{
			result = "";
		}

		return result;
	}

	public Boolean setDocument(String filename)
	{
		Boolean result = false;
		try
		{
			document = builder.parse(new File(filename));
			result = true;
		}
		catch (Exception ex)
		{
			System.err.println("JXMLDocument.setDocument " + ex.getMessage());
		}
		return result;
	}

	public void setDocument(Document doc)
	{
		document = doc;
	}

	public void setDocumentText(String text)
	{
		try
		{
			document = builder.parse(new InputSource(new StringReader(text)));
		}
		catch (Exception ex)
		{
			System.err.println("JXMLDocument.setDocumentText " + ex.getMessage());
		}
	}
}
