package spring.ls.beans.factory.support;

import java.lang.reflect.Constructor;

import spring.ls.beans.BeansException;
import spring.ls.beans.factory.BeanFactory;

public interface InstantiationStrategy {

	Object instantiate(RootBeanDefinition bd, String beanName, BeanFactory owner) throws BeansException;
	
	Object instantiate(RootBeanDefinition bd, String beanName, BeanFactory owner,
			Constructor<?> ctor, Object... args) throws BeansException;
	
}
