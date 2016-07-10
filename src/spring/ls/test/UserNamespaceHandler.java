package spring.ls.test;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import spring.ls.beans.BeanDefinition;
import spring.ls.beans.factory.config.BeanDefinitionHolder;
import spring.ls.beans.factory.xml.NamespaceHandler;
import spring.ls.beans.factory.xml.ParserContext;

public class UserNamespaceHandler implements NamespaceHandler{

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BeanDefinition parse(Element ele, ParserContext parserContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BeanDefinitionHolder decorate(Node source, BeanDefinitionHolder bdHolder, ParserContext parserContext) {
		return bdHolder;
	}

}
