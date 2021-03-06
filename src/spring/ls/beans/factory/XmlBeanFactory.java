package spring.ls.beans.factory;

import spring.ls.beans.factory.support.DefaultListableBeanFactory;
import spring.ls.beans.factory.xml.XmlBeanDefinitionReader;
import spring.ls.core.io.Resource;

public class XmlBeanFactory extends DefaultListableBeanFactory{

	private XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this);
	
	public XmlBeanFactory(Resource resource) {
		try {
			reader.loadBeanDefinitions(resource);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
