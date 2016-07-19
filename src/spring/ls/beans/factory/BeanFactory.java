package spring.ls.beans.factory;

import spring.ls.beans.BeansException;

public interface BeanFactory {
	
	String FACTORY_BEAN_PREFIX = "&";
	
//	T getObject();
//	
//	Class<?> getObjectType();
//	
//	boolean isSingleton();
	
	Object getBean(String alias) throws BeansException;
}
