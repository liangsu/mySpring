package spring.ls.beans.factory.support;

import spring.ls.beans.factory.config.BeanDefinition;

/**
 * 登记注册bean
 * @author Administrator
 *
 */
public interface BeanDefinitionRegistry {

	void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)  throws Exception;
}
