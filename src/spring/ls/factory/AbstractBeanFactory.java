package spring.ls.factory;

import java.util.HashMap;
import java.util.Map;

import spring.ls.bean.BeanDefinition;
import spring.ls.core.ParseBeanDefinitionsHolder;
import spring.ls.core.env.Environment;

public abstract class AbstractBeanFactory extends DefaultConfiguration implements BeanFactory<Object>,Environment{

	private static Map<String, Object> beans = new HashMap<String, Object>();
	private static Map<String, Object> singletonBeans = new HashMap<String, Object>();
	private static Map<String, Object> cacheBeans = new HashMap<String, Object>(4);
	
	public AbstractBeanFactory(){
		
	}
	
	@Override
	public void registerBeanDefinition(BeanDefinition beanDefinition) throws Exception {
		Object instance = ParseBeanDefinitionsHolder.parse(beanDefinition);
		registerBean(beanDefinition.getName(), instance, beanDefinition.getScope());
	}
	
	/**
	 * 注册bean
	 * @param name
	 * @param instance
	 * @param scope
	 * @throws Exception
	 */
	private void registerBean(String name,Object instance,String scope) throws Exception{
		if(scope.equals(SCOPE_PROTOTYPE)){
			beans.put(name, instance);
		}else if(scope.equals(SCOPE_SINGLETON)){
			singletonBeans.put(name, instance);
		}
	}
	
	@Override
	public Object getBean(String alias){
		Object obj = null;
		
		boolean isSingle = true;
		obj = cacheBeans.get(alias);
		if(obj == null){
			obj = singletonBeans.get(alias);
			if(obj == null){
				obj = beans.get(alias);
				isSingle = obj == null ? true : false;//如果是从beans取出，则不是单例
			}
		}
		
		//如果bean不为空，并且是单例，则放入缓存
		if(obj != null && isSingle && !cacheBeans.containsKey(alias)){
			cacheBeans.put(alias, obj);
		}
		
		return obj;
	}

}
