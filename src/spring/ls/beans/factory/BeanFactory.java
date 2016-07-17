package spring.ls.beans.factory;

import spring.ls.beans.BeansException;

public interface BeanFactory<T> {
	
	String FACTORY_BEAN_PREFIX = "&";
	
//	T getObject();
//	
//	Class<?> getObjectType();
//	
//	boolean isSingleton();
	
	T getBean(String alias) throws BeansException;
}
