package spring.ls.io;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import spring.ls.beans.BeanDefinitionReader;
import spring.ls.beans.factory.config.BeanDefinitionHolder;
import spring.ls.beans.factory.support.BeanDefinitionRegistry;
import spring.ls.beans.factory.support.BeanDefinitionRegistryUtils;
import spring.ls.core.io.EncodedResource;
import spring.ls.core.io.Resource;
import spring.ls.util.StringUtils;

public class XmlBeanDefinitionReader implements BeanDefinitionReader{

	private BeanDefinitionRegistry beanDefinitionRegistry;
	private BeanDefinitionParserDelegate delegate = new BeanDefinitionParserDelegate();
	
	public XmlBeanDefinitionReader(BeanDefinitionRegistry beanDefinitionRegistry) {
		this.beanDefinitionRegistry = beanDefinitionRegistry;
	}
	
	@Override
	public void LoadBeanDefinitions(Resource resource) throws Exception {
		if(!resource.exists()){
			throw new FileNotFoundException(resource.getDescription());
		}
		
		EncodedResource encodedResource = new EncodedResource(resource,"utf-8");
		InputStream is = encodedResource.getResource().getInputStream();
		InputSource inputSource = new InputSource(is);
		inputSource.setEncoding(encodedResource.getEncoding());
		LoadBeanDefinitions(inputSource);
	}
	
	private void LoadBeanDefinitions(InputSource inputSource) throws Exception {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(inputSource);
		
		Element root = document.getDocumentElement();
		NodeList nodeList = root.getChildNodes();
		for(int i  = 0; i < nodeList.getLength(); i++){
			Node node = nodeList.item(i);
			if(node instanceof Element){
				Element ele = (Element) node;
				parseBeanDefinition(ele);
			}
		}
	}

	private void parseBeanDefinition(Element ele) throws ClassNotFoundException {
		try {
			BeanDefinitionHolder beanDefinitionHolder = delegate.parseBeanDefinitionElement(ele);
			
			BeanDefinitionRegistryUtils.registerBeanDefinition(beanDefinitionHolder, beanDefinitionRegistry);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new ClassNotFoundException(ele.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
