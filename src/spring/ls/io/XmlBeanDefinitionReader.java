package spring.ls.io;

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import spring.ls.beans.BeanDefinitionReader;
import spring.ls.beans.factory.config.BeanDefinition;
import spring.ls.beans.factory.config.BeanDefinitionHolder;
import spring.ls.beans.factory.config.GenericBeanDefinition;
import spring.ls.beans.factory.support.BeanDefinitionRegistry;
import spring.ls.beans.factory.support.BeanDefinitionRegistryUtils;
import spring.ls.core.io.EncodedResource;
import spring.ls.core.io.Resource;
import spring.ls.util.ClassUtils;

public class XmlBeanDefinitionReader implements BeanDefinitionReader{

	private BeanDefinitionRegistry beanDefinitionRegistry;
	
	public XmlBeanDefinitionReader(BeanDefinitionRegistry beanDefinitionRegistry) {
		this.beanDefinitionRegistry = beanDefinitionRegistry;
	}
	
	private String getBeanName(Class<?> clazz){
		String beanName = clazz.getSimpleName();
		beanName = beanName.toLowerCase().charAt(0) + beanName.substring(1);
		return beanName;
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
	
	public void LoadBeanDefinitions(InputSource inputSource) throws Exception {
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
		String id = ele.getAttribute("id");
		String name = ele.getAttribute("name");
		String[] aliases = null;
		
		String beanName = name;
		try {
			BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanName,aliases);
			BeanDefinition beanDefinition = createBeanDefinition(ele);
			beanDefinitionHolder.setBeanDefinition(beanDefinition);
			BeanDefinitionRegistryUtils.registerBeanDefinition(beanDefinitionHolder, beanDefinitionRegistry);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new ClassNotFoundException(ele.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private BeanDefinition createBeanDefinition(Element ele) throws ClassNotFoundException{
		BeanDefinition beanDefinition = new GenericBeanDefinition();
		String beanClassName = ele.getAttribute("class");
		String scope = ele.getAttribute("scope");
		beanDefinition.setBeanClass(ClassUtils.forName(beanClassName, ClassUtils.getDefaultClassLoader()));
		beanDefinition.setScope(scope);
		return beanDefinition;
	}

}
