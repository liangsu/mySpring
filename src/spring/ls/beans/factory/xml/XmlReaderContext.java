package spring.ls.beans.factory.xml;

import spring.ls.beans.factory.parsing.ReaderContext;
import spring.ls.beans.factory.support.BeanDefinitionRegistry;
import spring.ls.core.io.Resource;

public class XmlReaderContext extends ReaderContext{

	private final XmlBeanDefinitionReader reader;
	
	private final NamespaceHandlerResolver namespaceHandlerResolver;

	public XmlReaderContext(Resource resource, XmlBeanDefinitionReader reader,
			NamespaceHandlerResolver namespaceHandlerResolver) {
		super(resource);
		this.reader = reader;
		this.namespaceHandlerResolver = namespaceHandlerResolver;
	}

	public final XmlBeanDefinitionReader getReader() {
		return reader;
	}

	public final NamespaceHandlerResolver getNamespaceHandlerResolver() {
		return namespaceHandlerResolver;
	}

	public final BeanDefinitionRegistry getRegistry(){
		return this.reader.getRegistry();
	}
}
