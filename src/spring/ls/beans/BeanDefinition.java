package spring.ls.beans;

/**
 * 用于bean信息的描述
 * @author Administrator
 *
 */
public interface BeanDefinition extends AttributeAccessor{
	
	String SCOPE_PROTOTYPE = "prototype";
	String SCOPE_SINGLETION = "singleton";
	
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
}
