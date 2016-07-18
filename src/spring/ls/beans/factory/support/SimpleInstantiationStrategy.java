package spring.ls.beans.factory.support;

import java.lang.reflect.Constructor;

import spring.ls.beans.BeanUtils;
import spring.ls.beans.BeansException;
import spring.ls.beans.factory.BeanFactory;

public class SimpleInstantiationStrategy implements InstantiationStrategy{

	@Override
	public Object instantiate(RootBeanDefinition bd, String beanName, BeanFactory owner) throws BeansException {
		if(bd.getMethodOverrides().isEmpty()){
			Constructor<?> constructorToUse;
			//解析默认的构造方法
			synchronized (bd.constructorArgumentLock) {
				constructorToUse = (Constructor<?>) bd.resolvedConstructorOrFactoryMethod;
				if(constructorToUse == null){
					Class<?> beanClass = bd.getBeanClass();
					if(beanClass.isInterface()){
						throw new BeansException("你初始化的bean："+beanName+"是一个接口");
					}
					
					try {
						constructorToUse = beanClass.getDeclaredConstructor();
						bd.resolvedConstructorOrFactoryMethod = constructorToUse;
					} catch (Exception e) {
						throw new BeansException(beanClass+"没有找到默认的构造方法");
					}
				}
			}
			return BeanUtils.instantiateClass(constructorToUse);
			
		}else{
			return instantiateWithMethodInjection(beanName, bd, owner);
		}
	}

	protected Object instantiateWithMethodInjection(String beanName, RootBeanDefinition bd, BeanFactory owner) {
		throw new UnsupportedOperationException("Method Injection not supported in SimpleInstantiationStrategy");
	}

	@Override
	public Object instantiate(RootBeanDefinition bd, String beanName, BeanFactory owner, Constructor<?> constructorToUse,
			Object... args) throws BeansException {
		
		if(bd.getMethodOverrides().isEmpty()){
			
			return BeanUtils.instantiateClass(constructorToUse, args);
			
		}else{
			return instantiateWithMethodInjection(beanName, bd, owner, constructorToUse, args);
		}
	}

	protected Object instantiateWithMethodInjection(String beanName, RootBeanDefinition bd, BeanFactory owner,
			Constructor<?> constructorToUse, Object[] args) {
		throw new UnsupportedOperationException("Method Injection not supported in SimpleInstantiationStrategy");
	}
}
