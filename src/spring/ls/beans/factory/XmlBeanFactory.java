package spring.ls.beans.factory;

import spring.ls.core.io.Resource;
import spring.ls.io.XmlBeanDefinitionReader;

public class XmlBeanFactory extends AbstractBeanFactory{

	private XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this);
	
	public XmlBeanFactory(Resource resource) {
		try {
			reader.loadBeanDefinitions(resource);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
