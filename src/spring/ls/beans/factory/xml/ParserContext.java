package spring.ls.beans.factory.xml;

import spring.ls.beans.factory.support.BeanDefinitionRegistry;

public class ParserContext {

	private final XmlReaderContext readerContext;
	
	private final BeanDefinitionParserDelegate delegate;

	public ParserContext(XmlReaderContext readerContext, BeanDefinitionParserDelegate delegate) {
		super();
		this.readerContext = readerContext;
		this.delegate = delegate;
	}

	public final XmlReaderContext getReaderContext() {
		return readerContext;
	}

	public final BeanDefinitionParserDelegate getDelegate() {
		return delegate;
	}

	public final BeanDefinitionRegistry getRegistry(){
		return this.readerContext.getRegistry();
	}
}
