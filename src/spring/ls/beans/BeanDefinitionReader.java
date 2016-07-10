package spring.ls.beans;

import spring.ls.core.io.Resource;

/**
 * 用于获取bean的定义
 * @author warhorse
 *
 */
public interface BeanDefinitionReader {
	/**
	 * 获取定义的bean
	 * @return
	 */
	void loadBeanDefinitions(Resource resource) throws Exception;
}
