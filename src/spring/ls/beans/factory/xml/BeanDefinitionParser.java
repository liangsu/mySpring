package spring.ls.beans.factory.xml;

import org.w3c.dom.Element;

import spring.ls.beans.BeanDefinition;

public interface BeanDefinitionParser {

	BeanDefinition parser(Element element, ParserContext parserContext);
}
