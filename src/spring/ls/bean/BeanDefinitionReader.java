package spring.ls.bean;

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
	void LoadBeanDefinitions(Resource resource) throws Exception;
}
