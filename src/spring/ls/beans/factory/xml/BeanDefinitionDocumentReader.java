package spring.ls.beans.factory.xml;

import org.w3c.dom.Document;

/**
 * document的beanDefinition的注册
 * @author warhorse
 *
 */
public interface BeanDefinitionDocumentReader {

	void registerBeanDefinitions(Document document, XmlReaderContext readerContext) throws Exception;
}
