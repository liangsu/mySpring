package spring.ls.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;

import spring.ls.beans.factory.config.BeanDefinition;
import spring.ls.beans.factory.config.BeanDefinitionHolder;
import spring.ls.beans.factory.config.GenericBeanDefinition;
import spring.ls.util.ClassUtils;
import spring.ls.util.StringUtils;

public class BeanDefinitionParserDelegate {
	public static final String MULTI_VALUE_ATTRIBUTE_DELIMITERS = ",";
	public static final String ATTRIBUTE_ID = "id";
	public static final String ATTRIBUTE_NAME = "name";
	public static final String ATTRIBUTE_CLASS = "class";
	public static final String ATTRIBUTE_SCOPE = "scope";
	
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
		BeanDefinition bd = new GenericBeanDefinition();
		
		parseBeanDefinitionAttributes(ele,bd);
		
		return bd;
	}

	
	/**
	 * 解析标签属性
	 * @param ele
	 * @param beanDefinition
	 * @throws ClassNotFoundException
	 */
	private void parseBeanDefinitionAttributes(Element ele,BeanDefinition beanDefinition) throws ClassNotFoundException{
		String beanClassName = ele.getAttribute(ATTRIBUTE_CLASS);
		String scope = ele.getAttribute(ATTRIBUTE_SCOPE);
		beanDefinition.setBeanClass(ClassUtils.forName(beanClassName, ClassUtils.getDefaultClassLoader()));
		beanDefinition.setScope(scope);
	}
}
