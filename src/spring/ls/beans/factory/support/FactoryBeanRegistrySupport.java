package spring.ls.beans.factory.support;

import java.util.HashMap;
import java.util.Map;

import spring.ls.beans.BeansException;
import spring.ls.beans.factory.FactoryBean;

public class FactoryBeanRegistrySupport extends DefaultSingletonBeanRegistry{

	/** 用来存储factoryBean的getObject()返回的对象 */
	private final Map<String, Object> factoryBeanObjectCache = new HashMap<String, Object>();
	
	protected Object getCachedObjectForFactoryBean(String name){
		return this.factoryBeanObjectCache.get(name);
	}
	
	protected Object getObjectFromFactoryBean(FactoryBean<?> factoryBean, String beanName) throws BeansException {
		
		//如果是单例
		if( factoryBean.isSingleton()){
			
			Object object = this.factoryBeanObjectCache.get(beanName);
			if(object == null){
				
				object = doGetObjectFromFactoryBean(factoryBean, beanName);
				
				this.factoryBeanObjectCache.put(beanName, object);
			}
			return object;
		}
		else {
			
			Object object = doGetObjectFromFactoryBean(factoryBean, beanName);
			
			return object;
		}
	}
	
	private Object doGetObjectFromFactoryBean(FactoryBean<?> factoryBean, String beanName) throws BeansException{
		Object object;
		
		try {
			object = factoryBean.getObject();
		} catch (Exception e) {
			throw new BeansException("创建beanName："+beanName+"失败!");
		}
		
		//TODO 调用objectFactory的后处理器
		
		return object;
	}
}
