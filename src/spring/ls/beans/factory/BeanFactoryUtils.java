package spring.ls.beans.factory;

public abstract class BeanFactoryUtils {
	
	/**
	 * 判断该名称是否是factoryBean的名称
	 * factoryBean的名称都是以&符号开始
	 * @param name
	 * @return
	 * @see BeanFactory#FACTORY_BEAN_PREFIX
	 */
	public static boolean isFactoryDereference(String name) {
		return ( name != null && name.startsWith(BeanFactory.FACTORY_BEAN_PREFIX));
	}

	public static String transformedBeanName(String name) {
		
		return null;
	}
}
