package spring.ls.io;

import spring.ls.bean.BeanDefinition;
import spring.ls.bean.BeanDefinitionReader;
import spring.ls.core.io.Resource;

public class XmlResourceBeanDefinitionReader implements BeanDefinitionReader{

	Resource resource;
	
	public XmlResourceBeanDefinitionReader(Resource resource) {
		
	}
	
	@Override
	public BeanDefinition[] getBeanDefinitions() {
		
		return null;
	}

}
