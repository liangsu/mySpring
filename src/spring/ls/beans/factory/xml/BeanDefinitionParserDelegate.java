package spring.ls.beans.factory.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import spring.ls.beans.BeanDefinition;
import spring.ls.beans.BeanMetadataAttributeAccessor;
import spring.ls.beans.LookupOverride;
import spring.ls.beans.MethodOverrides;
import spring.ls.beans.factory.config.BeanDefinitionHolder;
import spring.ls.beans.factory.support.AbstractBeanDefinition;
import spring.ls.beans.factory.support.GenericBeanDefinition;
import spring.ls.util.ClassUtils;
import spring.ls.util.StringUtils;

public class BeanDefinitionParserDelegate {
	
	/** 默认bean的命名空间 */
	public static final String BEANS_NAMESPACE_URI = "http://www.springframework.org/schema/beans";
	public static final String ATTRIBUTE_ID = "id";
	public static final String ATTRIBUTE_NAME = "name";
	public static final String ATTRIBUTE_CLASS = "class";
	public static final String ATTRIBUTE_SCOPE = "scope";
	public static final String ATTRIBUTE_INIT_METHOD = "init-method";
	
	private XmlReaderContext readerContext;
	
	public BeanDefinitionParserDelegate(XmlReaderContext readerContext) {
		this.readerContext = readerContext;
	}

	public BeanDefinitionHolder parseBeanDefinitionElement(Element ele) throws ClassNotFoundException{
		String id = ele.getAttribute(ATTRIBUTE_ID);
		String nameAttr = ele.getAttribute(ATTRIBUTE_NAME);
		
		List<String> aliases = new ArrayList<String>();
		if(StringUtils.hasLength(nameAttr)){
			aliases.addAll( Arrays.asList(nameAttr.split(",")));
		}
		
		String beanName = id;
		if(!StringUtils.hasLength(beanName) && aliases.size() > 0 ){
			beanName = aliases.remove(0);
		}
		
		BeanDefinition beanDefinition = parseBeanDefinitionElement(ele,beanName);
		String[] aliasArray = aliases.toArray(new String[aliases.size()]);
		BeanDefinitionHolder bd = new BeanDefinitionHolder(beanDefinition, beanName, aliasArray);
		return bd;
	}

	private BeanDefinition parseBeanDefinitionElement(Element ele, String beanName) throws ClassNotFoundException {
		AbstractBeanDefinition bd = new GenericBeanDefinition();
		
		//解析bean的属性
		parseBeanDefinitionAttributes(ele,bd);
		
		//解析meta元素
		parseMetaElement(ele, bd);
		
		//解析lookup-method元素
		parseLookupSubElement(ele, bd.getMethodOverrides());
		
		//解析构造方法属性
		
		return bd;
	}

	/**
	 * 解析标签属性
	 * @param ele
	 * @param beanDefinition
	 * @throws ClassNotFoundException
	 */
	private void parseBeanDefinitionAttributes(Element ele,AbstractBeanDefinition bd) throws ClassNotFoundException{
		String beanClassName = ele.getAttribute(ATTRIBUTE_CLASS);
		String scope = ele.getAttribute(ATTRIBUTE_SCOPE);
		bd.setBeanClass(ClassUtils.forName(beanClassName, ClassUtils.getDefaultClassLoader()));
		bd.setScope(scope);
	}
	
	/**
	 * 解析meta元素
	 * @param ele
	 * @param bd
	 */
	private void parseMetaElement(Element ele, BeanMetadataAttributeAccessor attributeAccessor) {
		NodeList nodes = ele.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++){
			Node node = nodes.item(i);
			if(nodeNameEqual(node, "meta")){
				Element element = (Element) node;
				attributeAccessor.setAttribute(element.getAttribute("key"), element.getAttribute("value"));
			}
		}
	}
	
	/**
	 * 解析lookup-method元素
	 * @param beanEle
	 * @param methodOverrides
	 */
	private void parseLookupSubElement(Element beanEle, MethodOverrides methodOverrides) {
		NodeList nodes = beanEle.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++){
			Node node = nodes.item(i);
			if(nodeNameEqual(node, "lookup-method")){
				Element element = (Element) node;
				String methodName = element.getAttribute("name");
				String beanName = element.getAttribute("bean");
				LookupOverride lookupOverride = new LookupOverride(methodName, beanName);
				lookupOverride.setSource(element);
				methodOverrides.addMethodOverride(lookupOverride);
			}
		}
	}
	
	/**
	 * 解析自定义标签
	 * @param ele
	 * @return
	 */
	public BeanDefinition parseCustomElement(Element ele) {
		String namespaceUri = getNamespaceURI(ele);
		NamespaceHandler handler = this.readerContext.getNamespaceHandlerResolver().resolve(namespaceUri);
		BeanDefinition bd = handler.parse(ele,  new ParserContext(readerContext, this));
		return bd;
	}
	
	public XmlReaderContext getReaderContext() {
		return readerContext;
	}

	/**
	 * 解析bean标签下的自定义标签
	 * @param ele
	 * @param bdHoler
	 * @return
	 */
	public BeanDefinitionHolder decorateBeanDefinitionIfRequired(Element beanEle, BeanDefinitionHolder bdHolder) {
		
		BeanDefinitionHolder finalDefinition = bdHolder;
		
		NodeList nodes = beanEle.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++){
			Node node = nodes.item(i);
			String namespaceUri = node.getNamespaceURI();
			if( !isDefaultNameSpace(namespaceUri)){
				NamespaceHandler handler = getReaderContext().getNamespaceHandlerResolver().resolve(namespaceUri);
				finalDefinition = handler.decorate(node, finalDefinition, new ParserContext(readerContext, this));
			}
		}
		
		return finalDefinition;
	}

	public String getLocalName(Node node) {
		return node.getLocalName();
	}
	
	public String getNamespaceURI(Node node){
		return node.getNamespaceURI();
	}
	
	public boolean isDefaultNameSpace(String nameSpaceUri){
		return ( !StringUtils.hasLength(nameSpaceUri) || BEANS_NAMESPACE_URI.equals(nameSpaceUri));
	}
	
	public boolean isDefaultNameSpace(Node node){
		return isDefaultNameSpace( node.getNamespaceURI());
	}

	public boolean nodeNameEqual(Node node, String desiredName) {
		return desiredName.equals( node.getNodeName()) || desiredName.equals( node.getLocalName());
	}
}
