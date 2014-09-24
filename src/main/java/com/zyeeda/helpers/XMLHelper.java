package com.zyeeda.helpers;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLHelper {

	private static Document loadXML(String path) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File(path));
		document.normalize();

		return document;
	}
	
	private static boolean updateXML(Document document, String path) throws TransformerException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();

		// out put encoding.
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		DocumentType type = document.getDoctype();
		if (type != null) {
			System.out.println("doctype -->" + type.getPublicId());
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, type.getPublicId());
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, type.getSystemId());
		}
		
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new File(path));
		transformer.transform(source, result);

		transformer.reset();
		return true;
	}
	
	public static boolean updateDBConfig(String userName, String password, String name, String driver, String url, String dbConfigPath) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException, TransformerException {
		Document document = loadXML(dbConfigPath);
		
		XPath path = XPathFactory.newInstance().newXPath();
		XPathExpression express = path.compile("//Configure/New[@class='org.eclipse.jetty.plus.jndi.Resource']/Arg/New[@class='bitronix.tm.resource.jdbc.PoolingDataSource']/Set[@name='className']");
		
		NodeList nodes = (NodeList) express.evaluate(document,
				XPathConstants.NODESET);
		Node node = nodes.item(0);
		node.setTextContent(driver);
		path.reset();
		
		express = path.compile("//Configure/New[@class='org.eclipse.jetty.plus.jndi.Resource']/Arg/New[@class='bitronix.tm.resource.jdbc.PoolingDataSource']/Get['@name=driverProperties']/Put");
		nodes = (NodeList) express.evaluate(document,
				XPathConstants.NODESET);
		
		for (int i = 0; i < nodes.getLength(); i++) {
			node = nodes.item(i);
			if ("user".equals(node.getAttributes().item(0).getTextContent())) {
				node.setTextContent(userName);
			} else if ("password".equals(node.getAttributes().item(0).getTextContent())) {
				node.setTextContent(password);
			} else if ("URL".equals(node.getAttributes().item(0).getTextContent())) {
//				String url = node.getTextContent();
//				System.out.println("basic url ---> " + url + "; name -->" + name);
//				url = replaceDBName("examples", name, url);
//				System.out.println("dist url ---> " + url);
				node.setTextContent(url);
			}
		}
		path.reset();

		return updateXML(document, dbConfigPath);
	}
	
	public static boolean updatePomDBConfig(String userName, String password, String name, String host, String port, String version, String pomPath) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException {
		Document document = loadXML(pomPath);
		XPath path = XPathFactory.newInstance().newXPath();
		XPathExpression express = path.compile("//project/properties/mysql.server.version");
		
		NodeList nodes = (NodeList) express.evaluate(document,
				XPathConstants.NODESET);
		Node node = nodes.item(0);
		node.setTextContent(version);
		path.reset();
		
		express = path.compile("//project/properties/mysql.server.host");
		nodes = (NodeList) express.evaluate(document,
				XPathConstants.NODESET);
		node = nodes.item(0);
		node.setTextContent(host);
		path.reset();
		
		express = path.compile("//project/properties/mysql.server.port");
		nodes = (NodeList) express.evaluate(document,
				XPathConstants.NODESET);
		node = nodes.item(0);
		node.setTextContent(port);
		path.reset();

		express = path.compile("//project/properties/mysql.server.database");
		nodes = (NodeList) express.evaluate(document,
				XPathConstants.NODESET);
		node = nodes.item(0);
		node.setTextContent(name);
		path.reset();
		
		express = path.compile("//project/properties/mysql.server.user");
		nodes = (NodeList) express.evaluate(document,
				XPathConstants.NODESET);
		node = nodes.item(0);
		node.setTextContent(userName);
		path.reset();
		
		express = path.compile("//project/properties/mysql.server.password");
		nodes = (NodeList) express.evaluate(document,
				XPathConstants.NODESET);
		node = nodes.item(0);
		node.setTextContent(password);
		path.reset();
		
		return updateXML(document, pomPath);
	}
	
	public static boolean updateServerConfig(String port, String serverConfigPath) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException {
		return updateServerConfig(port, "//Configure/Call/Arg/New/Set[2]/Property", serverConfigPath);
	}
	
	public static boolean updateServerConfig(String port, String xpath, String serverConfigPath) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException {
		System.out.println("server config file --->" + serverConfigPath);
		Document document = loadXML(serverConfigPath);
		XPath path = XPathFactory.newInstance().newXPath();
		XPathExpression express = path
				.compile(xpath);

		// TODO:修改xml 节点。
		NodeList nodes = (NodeList) express.evaluate(document,
				XPathConstants.NODESET);
		
		Node attribute = nodes.item(0).getAttributes().item(0);
		System.out
				.println("attribute ---- > " + attribute.getTextContent());
		attribute.setTextContent(port);
		path.reset();

		return updateXML(document, serverConfigPath);
	}
	
//	private static String replaceDBName(String source, String dist, String url) {
//		return url.replaceFirst(source, dist);
//	}
	
	public static void main(String[] args) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException, TransformerException {
//		String driver = "com.mysql.jdbc.Driver";
//		String name = "test1";
//		String userName = "username1";
//		String password = "password1";
//		String dbConfigPath = "/Users/qizhao/Workspace/Zyeeda/jetty-env.xml";
//		updateDBConfig(userName, password, name, driver, dbConfigPath);
		File f = new File("/Users/qizhao/Desktop/build/docs");
		boolean flag = f.mkdir();
		System.out.println("Directory created ok ?" + flag);
	}
}
