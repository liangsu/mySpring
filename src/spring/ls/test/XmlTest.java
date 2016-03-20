package spring.ls.test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import spring.ls.core.io.ClassPathResource;
import spring.ls.core.io.EncodedResource;
import spring.ls.core.io.Resource;

public class XmlTest {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		Resource resource = new ClassPathResource("beans.xml");
		EncodedResource encodedResource = new EncodedResource(resource,"utf-8");
		InputStream is = encodedResource.getResource().getInputStream();
		InputSource inputSource = new InputSource(is);
		inputSource.setEncoding(encodedResource.getEncoding());
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(inputSource);
		System.out.println(document);
		Element root = document.getDocumentElement();
		System.out.println(root);
		NodeList nodeList = root.getChildNodes();
		for(int i  = 0; i < nodeList.getLength(); i++){
			Node node = nodeList.item(i);
			System.out.println(node);
			//System.out.println(node.getNodeName());
		}
		
	}
}
