package spring.ls.beans.factory.support;

import spring.ls.beans.BeanDefinition;

/**
 * 登记注册bean
 * @author Administrator
 *
 */
public interface BeanDefinitionRegistry {

	void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)  throws Exception;
	
	void registerAlias(String beanName,String alias) throws Exception;
}
