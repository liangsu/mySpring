package spring.ls.beans;

import spring.ls.beans.factory.config.ConstructorArgumentValues;

/**
 * 用于bean信息的描述
 * @author Administrator
 *
 */
public interface BeanDefinition extends AttributeAccessor{
	
	String SCOPE_PROTOTYPE = "prototype";
	String SCOPE_SINGLETON = "singleton";
	
	String getParentName();
	
	void setParentName(String parentName);
	
	/**
	 * 设置bean的class，如：com.ls.User
	 * @param beanClassName
	 */
	void setBeanClassName(String beanClassName);
	
	String getBeanClassName();
	
	void setBeanClass(Class<?> beanClass);
	
	Class<?> getBeanClass();
	
	void setScope(String scope);
	
	String getScope();
	
	boolean isPrototype();
	
	boolean isSingleton();
	
	String getFactoryBeanName();
	
	void setFactoryBeanName(String factoryBeanName);
	
	String getFactoryMethodName();
	
	void setFactoryMethodName(String factoryMethodName);
	
	ConstructorArgumentValues getConstructorArgumentValues();
	
	MutablePropertyValues getPropertyValues();
	
}
