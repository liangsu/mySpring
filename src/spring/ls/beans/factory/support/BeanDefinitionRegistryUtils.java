package spring.ls.beans.factory.support;

import spring.ls.beans.factory.config.BeanDefinitionHolder;

public class BeanDefinitionRegistryUtils {

	public static void registerBeanDefinition(BeanDefinitionHolder beanDefinitionHolder,BeanDefinitionRegistry registry) throws Exception{
		String beanName = beanDefinitionHolder.getBeanName();
		registry.registerBeanDefinition(beanName, beanDefinitionHolder.getBeanDefinition());
	}
}
