package spring.ls.beans.factory.support;

import spring.ls.beans.BeanDefinition;
import spring.ls.core.AliasRegistry;

/**
 * 登记注册bean
 * @author Administrator
 *
 */
public interface BeanDefinitionRegistry extends AliasRegistry{

	void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)  throws Exception;
	
}
