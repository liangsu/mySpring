package spring.ls.beans.factory.xml;

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import spring.ls.beans.BeanDefinitionReader;
import spring.ls.beans.factory.parsing.FailFastProblemReporter;
import spring.ls.beans.factory.parsing.ProblemReporter;
import spring.ls.beans.factory.support.BeanDefinitionRegistry;
import spring.ls.core.io.EncodedResource;
import spring.ls.core.io.Resource;

public class XmlBeanDefinitionReader implements BeanDefinitionReader{

	private BeanDefinitionRegistry beanDefinitionRegistry;
	private NamespaceHandlerResolver namespaceHandlerResolver;
	private ProblemReporter problemReporter;
	
	public XmlBeanDefinitionReader(BeanDefinitionRegistry beanDefinitionRegistry){
		this.beanDefinitionRegistry = beanDefinitionRegistry;
	}
	
	@Override
	public void loadBeanDefinitions(Resource resource) throws Exception {
		if(!resource.exists()){
			throw new FileNotFoundException(resource.getDescription());
		}
		EncodedResource encodedResource = new EncodedResource(resource,"utf-8");
		InputStream is = encodedResource.getResource().getInputStream();
		InputSource inputSource = new InputSource(is);
		inputSource.setEncoding(encodedResource.getEncoding());
		loadBeanDefinitions(inputSource, resource);
	}
	
	private void loadBeanDefinitions(InputSource inputSource,Resource resource) throws Exception{
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(inputSource);
		
		BeanDefinitionDocumentReader beanDefinitionDocumentReader = new DefaultBeanDefinitionDocumentReader();
		beanDefinitionDocumentReader.registerBeanDefinitions(document, createReaderContext(resource));
	}
	
	private XmlReaderContext createReaderContext(Resource resource){
		return new XmlReaderContext(resource, getProblemReporter(), this, getNamespaceHandlerResolver());
	}
	
	private NamespaceHandlerResolver getNamespaceHandlerResolver(){
		if( this.namespaceHandlerResolver == null){
			namespaceHandlerResolver = new DefaultNamespaceHandlerResolver();
		}
		return this.namespaceHandlerResolver;
	}
	
	private ProblemReporter getProblemReporter(){
		if(this.problemReporter == null){
			this.problemReporter = new FailFastProblemReporter();
		}
		return this.problemReporter;
	}
	
	public BeanDefinitionRegistry getRegistry() {
		return beanDefinitionRegistry;
	}
}
