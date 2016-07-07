package spring.ls.core;

import spring.ls.beans.BeanDefinition;

/**
 * 用于将{@code BeanDefinition}解析实例的类
 * @author Administrator
 */
public class ParseBeanDefinitionsHolder {

	public static Object parse(BeanDefinition beanDefinition){
		Object obj = null;
		
		Class<?> clazz = beanDefinition.getBeanClass();
		try {
			obj = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		if(obj == null){
			throw new RuntimeException(beanDefinition.getBeanClassName() + "初始化失败!");
		}
		
		return obj;
	}
}
