package spring.ls.beans.factory.support;

import spring.ls.beans.BeanDefinition;

public class RootBeanDefinition extends AbstractBeanDefinition{

	public RootBeanDefinition(BeanDefinition original){
		super(original);
	}

	@Override
	public String getParentName() {
		return null;
	}

	@Override
	public void setParentName(String parentName) {
		
	}

	/**
	 * 重写rootBean的各个属性
	 * @param bd
	 */
	public void overrideFrom(BeanDefinition bd) {
		setBeanClassName(bd.getBeanClassName());
		setBeanClassName(bd.getBeanClassName());
		setScope(bd.getScope());
		
		if(bd instanceof AbstractBeanDefinition){
			AbstractBeanDefinition abd = (AbstractBeanDefinition) bd;
			setMethodOverrides(abd.getMethodOverrides());
		}
	}
	
}
