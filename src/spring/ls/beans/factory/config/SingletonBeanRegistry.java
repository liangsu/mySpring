package spring.ls.beans.factory.config;

public interface SingletonBeanRegistry {

	String SCOPE_PROTOTYPE = "prototype";
	
	String SCOPE_SINGLETON = "singleton";
	
	void registerSingleton(String beanName, Object singletonObject);
	
	Object getSingleton(String beanName);
}
