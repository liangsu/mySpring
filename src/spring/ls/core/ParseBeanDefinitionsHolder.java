package spring.ls.core;

import spring.ls.bean.BeanDefinition;

public class ParseBeanDefinitionsHolder {

	public static Object parse(BeanDefinition beanDefinition){
		Object obj = null;
		
		Class clazz = beanDefinition.getBeanClass();
		try {
			obj = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		if(obj == null){
			throw new RuntimeException(beanDefinition.getName() + "初始化失败!");
		}
		
		return obj;
	}
}
