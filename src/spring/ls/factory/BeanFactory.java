package spring.ls.factory;

public interface BeanFactory<T> {
//	T getObject();
//	
//	Class<?> getObjectType();
//	
//	boolean isSingleton();
	
	T getBean(String alias);
}
