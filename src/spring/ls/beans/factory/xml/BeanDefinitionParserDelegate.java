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
import spring.ls.beans.factory.config.ConstructorArgumentValues;
import spring.ls.beans.factory.config.RuntimeBeanReference;
import spring.ls.beans.factory.config.TypedStringValue;
import spring.ls.beans.factory.support.AbstractBeanDefinition;
import spring.ls.beans.factory.support.GenericBeanDefinition;
import spring.ls.util.ClassUtils;
import spring.ls.util.StringUtils;

public class BeanDefinitionParserDelegate {
	
	/** 默认bean的命名空间 */
	public static final String BEANS_NAMESPACE_URI = "http://www.springframework.org/schema/beans";
	public static final String ATTRIBUTE_ID = "id";
	public static final String ATTRIBUTE_NAME = "name";
	public static final String ATTRIBUTE_FACTORY_BEAN_NAME = "factory-bean";
	public static final String ATTRIBUTE_FACTORY_METHOD_NAME = "factory-method";
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
		
		//解析replaced-method元素
		
		//解析构造方法属性
		parseConstructorArgElements(ele, bd);
		
		//解析property子元素
		
		//解析qualifier子元素
		
		
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
		
		String factoryBeanName = ele.getAttribute(ATTRIBUTE_FACTORY_BEAN_NAME);
		if(StringUtils.hasLength(factoryBeanName)){
			bd.setFactoryBeanName(factoryBeanName);
		}
		String factoryMethodName = ele.getAttribute(ATTRIBUTE_FACTORY_METHOD_NAME);
		if(StringUtils.hasLength(factoryMethodName)){
			bd.setFactoryMethodName(factoryMethodName);
		}
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
	 * 解析构造方法参数
	 * 构造方法的注入方式有4种：
	 * 方式一：根据索引赋值，索引都是以0开头的：
	 *   <constructor-arg index="0" value="刘晓刚" />
	 *   <constructor-arg index="2" ref="dept"/>
	 * 方式二：根据所属类型传值
	 *   <constructor-arg type="java.lang.String" value="刘晓刚" />
	 *   <constructor-arg type="www.csdn.spring01.constructor.Dept" ref="dept"/>
	 * 方式三：根据参数的名字传值：（推荐用法）
	 *   <constructor-arg name="name" value="刘晓刚" />
	 *   <constructor-arg name="dept" ref="dept"/>
	 * 方式四：直接传值：
	 *   <constructor-arg  value="刘晓刚" />
	 *   <constructor-arg  ref="dept"/>
	 * @param ele
	 * @param bd
	 */
	private void parseConstructorArgElements(Element beanEle, AbstractBeanDefinition bd) {
		NodeList nodes = beanEle.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++){
			Node node = nodes.item(i);
			if(isCandidateElement(node) && nodeNameEqual(node, "constructor-arg")){
				parseConstructorArgElement((Element) node, bd);
			}
		}
	}
	
	private void parseConstructorArgElement(Element ele, AbstractBeanDefinition bd) {
		String indexAttr = ele.getAttribute("index");
		String nameAttr = ele.getAttribute("name");
		String typeAttr = ele.getAttribute("type");
		
		if(StringUtils.hasLength(indexAttr)){
			int index = Integer.parseInt(indexAttr);
			if(index < 0){
				//抛出异常
				error("构造方法的index必须大于等于0", ele);
			}else{
				//从构造方法元素中解析出参数的值
				Object value = parsePropertyValue(ele, bd, null);
				ConstructorArgumentValues.ValueHolder valueHolder = new ConstructorArgumentValues.ValueHolder(value);
				if(StringUtils.hasLength(typeAttr)){
					valueHolder.setType(typeAttr);
				}
				if(StringUtils.hasLength(nameAttr)){
					valueHolder.setType(nameAttr);
				}
				valueHolder.setSource(ele);
				if(bd.getConstructorArgumentValues().hasIndexedArgumentValue(index)){
					error("构造方法中出现了重复的index", ele);
				}else{
					bd.getConstructorArgumentValues().addIndexedArgumentValue(index, valueHolder);
				}
				
			}
			
		}else{
			
			//从构造方法元素中解析出参数的值
			Object value = parsePropertyValue(ele, bd, null);
			ConstructorArgumentValues.ValueHolder valueHolder = new ConstructorArgumentValues.ValueHolder(value);
			if(StringUtils.hasLength(typeAttr)){
				valueHolder.setType(typeAttr);
			}
			if(StringUtils.hasLength(nameAttr)){
				valueHolder.setType(nameAttr);
			}
			valueHolder.setSource(ele);
			bd.getConstructorArgumentValues().addGenericArgumentValue(valueHolder);
			
		}
	}

	private Object parsePropertyValue(Element ele, BeanDefinition bd, String propertyName) {
		String elementName = (propertyName != null) ?
				"<property> element for property '" + propertyName + "'" :
				"<constructor-arg> element";
		
		Element subElement = null;
		NodeList nodes = ele.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++){
			Node node = nodes.item(i);
			if( node instanceof Element){
				if(subElement != null){
					error(elementName+"构造方法的一个参数只能有一个值", ele);
				}else{
					subElement = (Element) node;
				}
			}
		}
		
		boolean hasValueAttribute = ele.hasAttribute("value");
		boolean hasRefAttribute = ele.hasAttribute("ref");
		if( (hasValueAttribute && hasRefAttribute) ||
				((hasRefAttribute || hasRefAttribute) && subElement != null)){
			error(elementName+"只允许拥有一个vlaue或者ref或者subElement", ele);
		}
		
		if(hasValueAttribute){
			//value属性的处理，使用TypeStringValue封装对应的value属性
			TypedStringValue value = new TypedStringValue(ele.getAttribute("value"));
			value.setSource(ele);
			return value;
			
		}else if(hasRefAttribute){
			//ref属性的处理，使用RuntimeBeanReference封装对应的ref名称
			String refName = ele.getAttribute("value");
			if( !StringUtils.hasText(refName)){
				error(elementName+"value的值不能为空", ele);
			}
			RuntimeBeanReference ref = new RuntimeBeanReference(refName);
			ref.setSource(ele);
			return ref;
			
		}else if(subElement != null){
			//TODO 解析子元素
			return parsePropertySubElement(subElement, bd);
			
		}else{
			//既没有 子元素 也没有 ref 或者 value
			error(elementName+"必须指定一个ref或者value", ele);
			return null;
		}
	}

	private Object parsePropertySubElement(Element ele, BeanDefinition bd) {
		
		return parsePropertySubElement(ele, bd, null);
	}

	/**
	 * 解析属性元素
	 * @param ele
	 * @param bd
	 * @param defaultValueType
	 * @return
	 */
	private Object parsePropertySubElement(Element ele, BeanDefinition bd, String defaultValueType) {
		if( !isDefaultNameSpace(ele)){
			return null;
			
		}else if( nodeNameEqual(ele, "bean")){
			return null;
			
		}else if( nodeNameEqual(ele, "ref")){
			return null;
			
		}else if( nodeNameEqual(ele, "idref")){
			return null;
			
		}else if( nodeNameEqual(ele, "value")){
			return null;
			
		}else if( nodeNameEqual(ele, "null")){
			TypedStringValue nullValue = new TypedStringValue(null);
			nullValue.setSource(ele);
			return nullValue;
			
		}else if( nodeNameEqual(ele, "array")){
			return null;
			
		}else if( nodeNameEqual(ele, "list")){
			return null;
			
		}else if( nodeNameEqual(ele, "set")){
			return null;
			
		}else if( nodeNameEqual(ele, "map")){
			return null;
			
		}else{
			error("不能够解析", ele);
			return null;
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

	/**
	 * 错误提示
	 */
	protected void error(String message, Node source) {
		this.readerContext.error(message, source);
	}

	protected void error(String message, Element source) {
		this.readerContext.error(message, source);
	}

	protected void error(String message, Element source, Throwable cause) {
		this.readerContext.error(message, source, cause);
	}
	
	public String getLocalName(Node node) {
		return node.getLocalName();
	}
	
	public String getNamespaceURI(Node node){
		return node.getNamespaceURI();
	}
	
	public boolean nodeNameEqual(Node node, String desiredName) {
		return desiredName.equals( node.getNodeName()) || desiredName.equals( node.getLocalName());
	}
	
	public boolean isDefaultNameSpace(String nameSpaceUri){
		return ( !StringUtils.hasLength(nameSpaceUri) || BEANS_NAMESPACE_URI.equals(nameSpaceUri));
	}
	
	public boolean isDefaultNameSpace(Node node){
		return isDefaultNameSpace( node.getNamespaceURI());
	}
	
	public boolean isCandidateElement(Node node){
		return ( (node instanceof Element) && ( isDefaultNameSpace(node) || !isDefaultNameSpace(node.getParentNode())));
	}

}
