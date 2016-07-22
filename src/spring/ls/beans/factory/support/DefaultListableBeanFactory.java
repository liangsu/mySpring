package spring.ls.beans.factory.support;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import spring.ls.beans.BeanDefinition;
import spring.ls.beans.BeansException;

public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry{
	
	/** 是否允许beanDefinition重写 */
	private boolean allowBeanDefinitionOverriding = true;
	
	/** beanDefinition存放的地方 */
	private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>(64);
	
	/** 存放beanName的注册名称 */
	private final List<String> beanDefinitionNames = new ArrayList<String>(64);
	
	/** 存放beanName的注册名称 */
	private final Set<String> manualSingletonNames = new LinkedHashSet<String>(16);
	
	@Override
	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws Exception {
		
		BeanDefinition oldBeanDefinition = this.beanDefinitionMap.get(beanName);
		if(oldBeanDefinition != null){
			
			if(!isAllowBeanDefinitionOverriding()){
				throw new BeansException(beanName+"重复注册了");
			}
		}else{
			beanDefinitionNames.add(beanName);
			manualSingletonNames.remove(beanName);
		}
		beanDefinitionMap.put(beanName, beanDefinition);
		
		//如果覆盖已经实例化了的bean
		if( oldBeanDefinition != null && containsBeanDefinition(beanName)){
			resetBeanDefinition(beanName);
		}
		
		System.out.println("注册了bean："+beanName+"["+beanDefinition+"]");
	}
	
	/**
	 * 清空bean
	 * @param beanName
	 */
	private void resetBeanDefinition(String beanName) {
		
		clearMergedBeanDefinition(beanName);
		
		destroySingleton(beanName);
		
		for(String name : this.beanDefinitionNames){
			if( !name.equals(beanName)){
				BeanDefinition bd = this.beanDefinitionMap.get(name);
				if(bd.getParentName().equals(beanName)){
					resetBeanDefinition(name);
				}
			}
		}
	}
	
	@Override
	public void destroySingleton(String beanName) {
		super.destroySingleton(beanName);
		this.manualSingletonNames.remove(beanName);
	}

	@Override
	protected BeanDefinition getBeanDefinition(String beanName) throws BeansException{
		BeanDefinition bd = this.beanDefinitionMap.get(beanName);
		if(bd == null){
			throw new BeansException("你要获取的"+beanName+"不存在!");
		}
		return bd;
	}

	@Override
	protected boolean containsBeanDefinition(String beanName) {
		return this.beanDefinitionMap.containsKey(beanName);
	}

	public boolean isAllowBeanDefinitionOverriding() {
		return allowBeanDefinitionOverriding;
	}

	public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
		this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
	}
}
