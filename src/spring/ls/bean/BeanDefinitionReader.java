package spring.ls.bean;

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
	BeanDefinition[] getBeanDefinitions();
}
