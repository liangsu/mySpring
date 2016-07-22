package spring.ls.beans.factory.support;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import spring.ls.beans.BeanDefinition;
import spring.ls.beans.BeanWrapper;
import spring.ls.beans.BeanWrapperImpl;
import spring.ls.beans.BeansException;
import spring.ls.beans.MutablePropertyValues;
import spring.ls.beans.PropertyValue;
import spring.ls.beans.PropertyValues;
import spring.ls.beans.TypeConverter;
import spring.ls.beans.factory.ObjectFactory;
import spring.ls.beans.factory.config.AutowireCapableBeanFactory;
import spring.ls.util.ObjectUtils;
import spring.ls.util.StringUtils;

/**
 * 具有装配能力的工厂
 * @author warhorse
 *
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory{

	/** 实例化策略 */
	private InstantiationStrategy instantiationStrategy = new SimpleInstantiationStrategy();
	
	/** Cache of unfinished FactoryBean instances: FactoryBean name --> BeanWrapper */
	private final Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, BeanWrapper>(16);
	
	/** 是否允许循环依赖 */
	private boolean allowCircularReferences = true;
	
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

	
	public Object doCreateBean(final String beanName, final RootBeanDefinition mbd, Object[] args) throws BeansException {
		BeanWrapper beanWrapper = null;
		
		if(mbd.isSingleton()){
			beanWrapper = this.factoryBeanInstanceCache.remove(beanName);
		}
		if(beanWrapper == null){
			beanWrapper = createBeanInstance(beanName, mbd, args);
		}

		final Object bean = ( beanWrapper != null ? beanWrapper.getWrappedInstance() : null);
		
		/**
		 * 是否需要提前曝光，单例&允许循环依赖&当前bean正在创建中，检测循环依赖
		 */
		boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
				isSingletonCurrentlyInCreation(beanName));
		
		if(earlySingletonExposure){
			//为避免循环依赖，可以在bean初始化完成前创建实例的ObjectFactory加入工厂
			addSingletonFactory(beanName, new ObjectFactory(){
				@Override
				public Object getObject() throws BeansException {
					
					//其中我们熟悉的aop就是在这里将advice动态织入bean中的，若没有则直接返回bean，不做任何处理
					
					return getEarlyBeanReference(beanName, mbd, bean);
				}
			});
		}
		
		Object exposedObject = bean;
		//装配属性
		populateBean(beanName, mbd, beanWrapper);
		
		return beanWrapper.getWrappedInstance();
	}
	
	/**
	 * 填充bean的属性
	 * @param beanName
	 * @param mbd
	 * @param bw
	 */
	protected void populateBean(String beanName, RootBeanDefinition mbd, BeanWrapper bw) {
		PropertyValues pvs = mbd.getPropertyValues();
		
		if(bw == null){
			if(!pvs.isEmpty()){
				throw new BeansException("不用运用属性到null对象上"+beanName);
			}else{
				return ;
			}
		}
		
		boolean continueWithPropertyPopulation = true;
		//TODO 修正bean
		
		if(!continueWithPropertyPopulation){
			return ;
		}
		
		if(mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_BY_NAME ||
				mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_BY_TYPE){
			
			MutablePropertyValues newPvs = new MutablePropertyValues(pvs);
			
			//通过名称注入
			if(mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_BY_NAME){
				autowireByName(beanName, mbd, bw, newPvs);
			}
			//通过类型注入
			if(mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_BY_TYPE){
				autowireByType(beanName, mbd, bw, newPvs);
			}
			
			pvs = newPvs;
		}
		
		applyPropertyValues(beanName, mbd, bw, pvs);
		
	}

	/**
	 * 运用属性的值到实例中
	 * @param beanName
	 * @param mbd
	 * @param bw
	 * @param pvs
	 */
	protected void applyPropertyValues(String beanName, BeanDefinition mbd, BeanWrapper bw, PropertyValues pvs) {
		if(pvs == null || pvs.isEmpty()){
			return ;
		}
		
		MutablePropertyValues mpvs = null;
		List<PropertyValue> original = null;
		
		if(pvs instanceof MutablePropertyValues){
			mpvs = (MutablePropertyValues) pvs;
			if(mpvs.isConverted()){
				bw.setPropertyValues(mpvs);
				return ;
			}
			original = mpvs.getPropertyValueList();
		}else{
			original = Arrays.asList(pvs.getPropertyValues());
		}
		
		TypeConverter typeConverter = getCustomTypeConverter();
		if(typeConverter == null){
			typeConverter = bw;
		}
		
		BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this, beanName, mbd, typeConverter);
		
		List<PropertyValue> deepCopy = new ArrayList<PropertyValue>(original.size());
		boolean resolveNecessary = false;
		for(PropertyValue pv : original){
			if(pv.isConverted()){
				deepCopy.add(pv);
			}else{
				String propertyName = pv.getName();
				Object originalValue = pv.getValue();
				Object resolvedValue = valueResolver.resolveValueIfNecessary(pv, originalValue);
				Object convertedValue = resolvedValue;
				
				pv.setConvertedValue(convertedValue);
				deepCopy.add(pv);
			}
		}
		
		if(mpvs != null && !resolveNecessary){
			mpvs.setConverted();
		}
		
		bw.setPropertyValues( new MutablePropertyValues(deepCopy));
	}

	protected void autowireByType(String beanName, RootBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues newPvs) {
		
	}

	protected void autowireByName(String beanName, RootBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) {
		String[] propertyNames = unsatisfiedNonSimpleProperties(mbd, bw);
		
		for (String propertyName : propertyNames) {
			if(containsBean(propertyName)){
				Object bean = getBean(propertyName);//这里获取的bean是已经有属性指针依赖的
				pvs.add(propertyName, bean);
				registerDependentBean(propertyName, beanName);
				System.out.println(beanName+"的属性"+propertyName+"的注入值"+bean);
			}else{
				System.out.println(beanName+"的属性"+propertyName+"没有找到可用的实例bean注入");
			}
		}
	}

	/**
	 * 提取需要注入的属性
	 * @param mbd
	 * @param bw
	 * @return
	 */
	protected String[] unsatisfiedNonSimpleProperties(AbstractBeanDefinition mbd, BeanWrapper bw) {
		Set<String> result = new TreeSet<String>();
		MutablePropertyValues pvs = mbd.getPropertyValues();
		PropertyDescriptor[] pds = bw.getPropertyDescriptors();
		
		for (PropertyDescriptor pd : pds) {
			if(pd.getWriteMethod() != null && !pd.getPropertyType().isPrimitive() && 
					!pvs.contains(pd.getName())){
				
				result.add(pd.getName());
			}
		}
		return StringUtils.toStringArray(result);
	}


	protected Object getEarlyBeanReference(String beanName, RootBeanDefinition mbd, Object bean) {
		
		return bean;
	}
	
	public BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, Object[] args) throws BeansException {
		
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
	
	public BeanWrapper instantiateBean(String beanName, RootBeanDefinition bd) throws BeansException {
		Object beanInstance = getInstantiationStrategy().instantiate(bd, beanName, this);
		BeanWrapperImpl bw = new BeanWrapperImpl(beanInstance);
		initBeanWrapper(bw);
		return bw;
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
	public BeanWrapper autowireConstructor(String beanName, RootBeanDefinition mbd, Constructor[] chosenCtors, Object[] explicitArgs) throws BeansException{
		
		return new ConstructorResolver(this).autowireConstructor(beanName, mbd, chosenCtors, explicitArgs);
	}

}
