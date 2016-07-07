package spring.ls.io;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import spring.ls.beans.BeanDefinition;
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
				if( delegate.isDefaultNameSpace(node)){
					Element ele = (Element) node;
					parseDefaultElement(ele, delegate);
				}else{
					
				}
			}
		}
	}

	/**
	 * 默认标签的解析
	 * @param ele
	 * @param del
	 * @throws Exception
	 */
	private void parseDefaultElement(Element ele, BeanDefinitionParserDelegate delegate) throws Exception {
		if( delegate.nodeNameEqual(ele, "bean")){
			processBeanDefinition(ele, delegate);
			
		}else if(delegate.nodeNameEqual(ele, "alias")){
			processAliasRegistration(ele);
			
		}else if(delegate.nodeNameEqual(ele, "import")){
			
		}else if(delegate.nodeNameEqual(ele, "beans")){
			
		}
	}

	/**
	 * 注册别名
	 * @param ele
	 * @throws Exception
	 */
	private void processAliasRegistration(Element ele) throws Exception {
		String name = ele.getAttribute("name");
		String alias = ele.getAttribute("alias");
		boolean valid = true;
		
		if( !StringUtils.hasLength(name)){
			valid = false;
			//throw new Exception("名称不能为空");
		}
		
		if( !StringUtils.hasLength(alias)){
			//throw new Exception("别名不能为空");
			valid = false;
		}
		
		if(valid){
			beanDefinitionRegistry.registerAlias(name, alias);
		}
	}

	/**
	 * 解析bean标签
	 * @param ele
	 * @throws ClassNotFoundException
	 */
	private void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) throws ClassNotFoundException {
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
