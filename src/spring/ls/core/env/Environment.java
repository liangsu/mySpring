package spring.ls.core.env;

import spring.ls.bean.BeanDefinition;

public interface Environment {

	void registerBeanDefinition(BeanDefinition beanDefinition) throws Exception;
}
