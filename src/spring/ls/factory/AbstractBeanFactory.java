package spring.ls.factory;

import java.util.HashMap;
import java.util.Map;

import spring.ls.annotation.ScanfAnnotation;
import spring.ls.bean.BeanDefinition;
import spring.ls.core.ParseBeanDefinitionsHolder;

public abstract class AbstractBeanFactory extends DefaultConfiguration implements BeanFactory<Object>{

	private static Map<String, Object> beans = null;
	private static Map<String, Object> singletonBeans = null;
	private static Map<String, Object> cacheBeans = new HashMap<String, Object>(4);
	
	public AbstractBeanFactory(){
		
	}
	
	protected void initBeans() throws Exception{
		if(beans == null){
			beans = new HashMap<String, Object>();
		}
		if(singletonBeans == null){
			singletonBeans = new HashMap<String, Object>();
		}
		loadBeans();
	}
	
	public abstract void loadBeans() throws Exception;
	
	/**
	 * 注册bean
	 * @param name
	 * @param instance
	 * @param scope
	 * @throws Exception
	 */
	protected void registerBean(String name,Object instance,String scope) throws Exception{
		if(scope.equals(SCOPE_PROTOTYPE)){
			beans.put(name, instance);
		}else if(scope.equals(SCOPE_SINGLETON)){
			singletonBeans.put(name, instance);
		}
	}
	
	protected void addBean(Class clazz,String scope) throws Exception{
		String name = clazz.getSimpleName();
		name = name.toLowerCase().charAt(0) + name.substring(1);
		Object obj = null;
		
		try {
			obj = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		if(obj == null){
			throw new Exception(name + "初始化失败!");
		}
		
		registerBean(name, obj, scope);
	}
	
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
