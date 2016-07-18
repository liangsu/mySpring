package spring.ls.beans.factory.xml;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import spring.ls.beans.BeanDefinition;
import spring.ls.beans.factory.config.BeanDefinitionHolder;

/**
 * 自定义标签解析、装饰的支持
 * @author warhorse
 *
 */
public abstract class NamespaceHandlerSupport implements NamespaceHandler{

	private final Map<String, BeanDefinitionParser> parsers = new HashMap<String, BeanDefinitionParser>();
	
	@Override
	public BeanDefinition parse(Element ele, ParserContext parserContext) {
		return findParserForElement(ele, parserContext).parser(ele, parserContext);
	}
	
	private BeanDefinitionParser findParserForElement(Element ele, ParserContext parserContext){
		String localName = parserContext.getDelegate().getLocalName(ele);
		BeanDefinitionParser parser = parsers.get(localName);
		if(parser == null){
			//TODO 错误处理
			
		}
		return parser;
	}
	
	@Override
	public BeanDefinitionHolder decorate(Node source, BeanDefinitionHolder bdHolder, ParserContext parserContext) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected void registerBeanDefinitionParser(String elementName, BeanDefinitionParser parser){
		this.parsers.put(elementName, parser);
	}
}
