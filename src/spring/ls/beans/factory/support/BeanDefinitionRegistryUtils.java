package spring.ls.beans.factory.support;

import spring.ls.beans.factory.config.BeanDefinitionHolder;

public class BeanDefinitionRegistryUtils {

	public static void registerBeanDefinition(BeanDefinitionHolder beanDefinitionHolder,BeanDefinitionRegistry registry) throws Exception{
		//1.注册beanDefinition
		String beanName = beanDefinitionHolder.getBeanName();
		registry.registerBeanDefinition(beanName, beanDefinitionHolder.getBeanDefinition());
		
		//2.注册别名
		String[] aliases = beanDefinitionHolder.getAsliases();
		if(aliases != null && aliases.length > 0){
			for (String alias : aliases) {
				registry.registerAlias(beanName, alias);
			}
		}
	}
}
