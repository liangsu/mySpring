package spring.ls.beans.factory.support;

import spring.ls.beans.BeanDefinition;

public class RootBeanDefinition extends AbstractBeanDefinition{

	public final Object constructorArgumentLock = new Object();
	
	/** 缓存bean实例化的构造方法 或 工厂方法*/
	public Object resolvedConstructorOrFactoryMethod;
	
	/** 标记是否有构造方法注入 */
	public boolean constructorArgumentsResolved = false;
	
	/** 解析出来的构造方法的参数 */
	public Object[] resolvedConstructorArguments;
	
	/** 解析出来的构造方法的参数，但是参数类型不一定对应 */
	public Object[] preparedConstructorArguments;
	
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
