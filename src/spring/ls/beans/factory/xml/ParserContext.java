package spring.ls.beans.factory.xml;

import spring.ls.io.BeanDefinitionParserDelegate;

public class ParserContext {

	private final XmlReaderContext readerContext;
	
	private final BeanDefinitionParserDelegate delegate;

	public ParserContext(XmlReaderContext readerContext, BeanDefinitionParserDelegate delegate) {
		super();
		this.readerContext = readerContext;
		this.delegate = delegate;
	}

	public XmlReaderContext getReaderContext() {
		return readerContext;
	}

	public BeanDefinitionParserDelegate getDelegate() {
		return delegate;
	}

}
