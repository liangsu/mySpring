package spring.ls.factory;

public class ApplicationContext extends AbstractBeanFactory implements BeanFactory<Object>{

	private AnnotationBeanFactory annotationBeanFactory;
	private XmlBeanFactory xmlBeanFactory;
	
	public ApplicationContext() {
		annotationBeanFactory = new AnnotationBeanFactory();
		xmlBeanFactory =new XmlBeanFactory();
		try {
			super.initBeans();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void loadBeans() {
		try {
			xmlBeanFactory.loadBeans();
			annotationBeanFactory.loadBeans();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
