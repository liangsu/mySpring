package spring.ls.beans.factory.support;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import spring.ls.beans.BeansException;
import spring.ls.beans.factory.ObjectFactory;
import spring.ls.beans.factory.config.SingletonBeanRegistry;
import spring.ls.core.SimpleAliasRegistry;

public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry{

	/** 用来存储单例对象，包含：单例bean、单例factoryBean */
	private final Map<String, Object> singletonObjects = new HashMap<String, Object>();
	private final Map<String, Object> earlySingletonObjects = new HashMap<String, Object>();
	private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<String, ObjectFactory<?>>();
	
	/** 保存了注册了的单例bean顺序 */
	private final Set<String> registeredSingletons = new LinkedHashSet<String>(64);
	
	@Override
	public void registerSingleton(String beanName, Object singletonObject) {
		Object oldSingletonObject = this.singletonObjects.get(beanName);
		if(oldSingletonObject != null){
			throw new IllegalStateException(beanName+"已经被注册了");
		}
	}

	protected void addSingleton(String beanName, Object singletonObject) {
		this.singletonObjects.put(beanName, singletonObject);
		this.earlySingletonObjects.remove(beanName);
		this.singletonFactories.remove(beanName);
		this.registeredSingletons.add(beanName);
	}

	@Override
	public Object getSingleton(String beanName) throws BeansException {
		Object singletonObject = this.singletonObjects.get(beanName);
		
		if(singletonObject == null){
			singletonObject = this.earlySingletonObjects.get(beanName);
			
			if(singletonObject == null){
				ObjectFactory<?> objectFactory = this.singletonFactories.get(beanName);
				
				if(objectFactory != null){
					singletonObject = objectFactory.getObject();
					this.earlySingletonObjects.put(beanName, singletonObject);
					this.singletonFactories.remove(beanName);
				}
			}
			
		}
		return singletonObject;
	}
	
	/**
	 * 创建bean，检测循环依赖
	 * @param beanName
	 * @param objectFactory
	 * @return
	 * @throws BeansException
	 */
	protected Object getSingleton(String beanName, ObjectFactory<Object> objectFactory) throws BeansException {
		
		//首先检查对应的bean是否已经加载过了，因为singleton模式就是复用已创建的bean，所以这一步是必须的
		Object singletonObject = this.singletonObjects.get(beanName);
		if(singletonObject == null){
			
			singletonObject = objectFactory.getObject();
			if(singletonObject == null){
				throw new BeansException("创建失败"+beanName);
			}
			
			addSingleton(beanName, singletonObject);
		}
		
		return singletonObject;
	}
}
