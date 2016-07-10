package spring.ls.beans.factory.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import spring.ls.beans.BeanDefinition;
import spring.ls.beans.factory.config.BeanDefinitionHolder;

public interface NamespaceHandler {

	void init();
	
	/**
	 * 解析自定义标签
	 * @param ele
	 * @param parserContext
	 * @return
	 */
	BeanDefinition parse(Element ele, ParserContext parserContext);
	
	/**
	 * 装饰自定义标签
	 * 用于默认标签下含有自定义标签时使用
	 * @param node
	 * @param bdHolder
	 * @param parserContext
	 * @return
	 */
	BeanDefinitionHolder decorate(Node source, BeanDefinitionHolder bdHolder, ParserContext parserContext);
}
