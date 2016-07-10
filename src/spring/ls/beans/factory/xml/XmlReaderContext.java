package spring.ls.beans.factory.xml;

import spring.ls.beans.factory.parsing.ReaderContext;
import spring.ls.core.io.Resource;
import spring.ls.io.XmlBeanDefinitionReader;

public class XmlReaderContext extends ReaderContext{

	private final XmlBeanDefinitionReader reader;
	
	private final NamespaceHandlerResolver namespaceHandlerResolver;

	public XmlReaderContext(Resource resource, XmlBeanDefinitionReader reader,
			NamespaceHandlerResolver namespaceHandlerResolver) {
		super(resource);
		this.reader = reader;
		this.namespaceHandlerResolver = namespaceHandlerResolver;
	}

	public XmlBeanDefinitionReader getReader() {
		return reader;
	}

	public NamespaceHandlerResolver getNamespaceHandlerResolver() {
		return namespaceHandlerResolver;
	}

}
