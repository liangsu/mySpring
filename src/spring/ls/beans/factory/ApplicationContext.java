package spring.ls.beans.factory;

import spring.ls.beans.factory.support.AbstractAutowireCapableBeanFactory;
import spring.ls.core.io.ClassPathResource;

public class ApplicationContext extends AbstractAutowireCapableBeanFactory implements BeanFactory{

	private AnnotationBeanFactory annotationBeanFactory;
	private XmlBeanFactory xmlBeanFactory;
	
	public ApplicationContext() {
		annotationBeanFactory = new AnnotationBeanFactory();
		xmlBeanFactory =new XmlBeanFactory(new ClassPathResource("beans.properties"));
	}
	
	public ApplicationContext(String fileLocation) {
		//annotationBeanFactory = new AnnotationBeanFactory();
		xmlBeanFactory =new XmlBeanFactory(new ClassPathResource(fileLocation));
	}

}
