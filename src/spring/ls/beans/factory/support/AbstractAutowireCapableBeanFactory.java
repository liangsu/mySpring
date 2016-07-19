package spring.ls.beans.factory.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import spring.ls.beans.BeansException;
import spring.ls.beans.factory.config.AutowireCapableBeanFactory;
import spring.ls.util.ObjectUtils;

/**
 * 具有装配能力的工厂
 * @author warhorse
 *
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory{

	/** 实例化策略 */
	private InstantiationStrategy instantiationStrategy = new SimpleInstantiationStrategy();
	
	
	//---------------------------------------------------------------------
	// 实现 AbstractBeanFactory相关的抽象方法
	//---------------------------------------------------------------------
	@Override
	public Object createBean(final String beanName,final RootBeanDefinition mbd, Object[] args) throws BeansException {
		//TODO 验证及准备覆盖的方法
		
		//TODO 给BeanPostProcessor一个机会返回代理来代替真正的实例
		
		//创建bean
		Object beanInstance = doCreateBean(beanName, mbd, args);
		
		
		return beanInstance;
	}

	
	public Object doCreateBean(String beanName, RootBeanDefinition mbd, Object[] args) throws BeansException {
		
		Object beanInstance = createBeanInstance(beanName, mbd, args);

		//TODO 提起曝光earlyBean
		
		//TODO 装配属性
		
		return beanInstance;
	} 
	
	public Object createBeanInstance(String beanName, RootBeanDefinition mbd, Object[] args) throws BeansException {
		
		Class<?> beanClass = mbd.getBeanClass();
		
		if(beanClass != null && !Modifier.isPublic(beanClass.getModifiers())){
			throw new BeansException("bean class is not public");
		}
		
		//如果有factoryMethodName，则调用factoryBean的方法创建bean
		if(mbd.getFactoryMethodName() != null){
			//TODO 创建
			return null;
		}
		
		boolean resolved = false;
		boolean autowireNecessary = false;
		
		if(args == null){
			synchronized (mbd.constructorArgumentLock) {
				if( mbd.resolvedConstructorOrFactoryMethod != null){
					resolved = true;
					autowireNecessary = mbd.constructorArgumentsResolved;
				}
			}
		}
		
		if(resolved){
			if(autowireNecessary){
				return autowireConstructor(beanName, mbd, null, null);
			}else{
				return instantiateBean(beanName, mbd);
			}
		}
		
		Constructor<?>[] ctors = beanClass.getDeclaredConstructors();
		if((ctors != null && ctors.length > 1)
				|| !ObjectUtils.isEmpty(args) ){
			
			return autowireConstructor(beanName, mbd, ctors, args);
		}
		
		return instantiateBean(beanName, mbd);
	}
	
	public Object instantiateBean(String beanName, RootBeanDefinition bd) throws BeansException {
		
		return getInstantiationStrategy().instantiate(bd, beanName, this);
	}
	
	public InstantiationStrategy getInstantiationStrategy() {
		return this.instantiationStrategy;
	}
	
	/**
	 * 装配构造方法
	 * @param beanName
	 * @param mbd
	 * @param chosenCtors
	 * @param explicitArgs
	 * @return
	 * @throws BeansException
	 */
	public Object autowireConstructor(String beanName, RootBeanDefinition mbd, Constructor[] chosenCtors, Object[] explicitArgs) throws BeansException{
		
		return new ConstructorResolver(this).autowireConstructor(beanName, mbd, chosenCtors, explicitArgs);
	}
}
