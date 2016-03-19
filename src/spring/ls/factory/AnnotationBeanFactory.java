package spring.ls.factory;

import spring.ls.annotation.ScanfAnnotation;
import spring.ls.bean.BeanDefinition;
import spring.ls.core.ParseBeanDefinitionsHolder;

public class AnnotationBeanFactory extends AbstractBeanFactory {

	@Override
	public void loadBeans() throws Exception {
		//2.通过注解获取要管理的clazz
		ScanfAnnotation scanfAnnotation = new ScanfAnnotation("spring.ls");
		BeanDefinition[] beanDefinitions = scanfAnnotation.getBeanDefinitions();
		for (BeanDefinition beanDefinition : beanDefinitions) {
			Object instance = ParseBeanDefinitionsHolder.parse(beanDefinition);
			super.registerBean(beanDefinition.getName(), instance, beanDefinition.getScope());
		}
	}

}
