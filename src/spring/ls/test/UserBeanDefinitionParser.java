package spring.ls.test;

import org.w3c.dom.Element;

import spring.ls.beans.BeanDefinition;
import spring.ls.beans.factory.xml.BeanDefinitionParser;
import spring.ls.beans.factory.xml.ParserContext;

public class UserBeanDefinitionParser implements BeanDefinitionParser{

	@Override
	public BeanDefinition parser(Element element, ParserContext parserContext) {
		System.out.println("-------------UserBeanDefinitionParser begin---------------");
		System.out.println("namespaceUri:"+element.getNamespaceURI());
		System.out.println("localName:"+element.getLocalName());
		System.out.println("-------------UserBeanDefinitionParser end---------------");

		
		return null;
	}

}
