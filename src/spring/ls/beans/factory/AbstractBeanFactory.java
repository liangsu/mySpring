package spring.ls.beans.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import spring.ls.beans.BeanDefinition;
import spring.ls.beans.BeansException;
import spring.ls.beans.factory.config.ConstructorArgumentValues;
import spring.ls.beans.factory.config.TypedStringValue;
import spring.ls.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import spring.ls.beans.factory.support.BeanDefinitionRegistry;
import spring.ls.beans.factory.support.FactoryBeanRegistrySupport;
import spring.ls.beans.factory.support.InstantiationStrategy;
import spring.ls.beans.factory.support.RootBeanDefinition;
import spring.ls.beans.factory.support.SimpleInstantiationStrategy;
import spring.ls.util.ObjectUtils;
import spring.ls.util.StringUtils;

public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements BeanFactory<Object>,BeanDefinitionRegistry{
	private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>(64);
	
	/** 将parent合并后的beanDefinition */
	private final Map<String, RootBeanDefinition> mergedBeanDefinitions = new HashMap<String, RootBeanDefinition>();
	
	/** 实例化策略 */
	private InstantiationStrategy instantiationStrategy;
	
	// ---
	private static Map<String, Object> cacheBeans = new HashMap<String, Object>(4);
	private static Map<String, Object> beans = new HashMap<String, Object>();
	public AbstractBeanFactory(){
		
	}
	
	@Override
	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws Exception {
		beanDefinitionMap.put(beanName, beanDefinition);
		System.out.println("注册了bean："+beanName+"["+beanDefinition+"]");
	}
	
	@Override
	public Object getBean(String alias) throws BeansException{
		return doGetBean(alias);
	}
	
	public Object doGetBean(String name) throws BeansException{
		
		final String beanName = cononicalName(name);
		
		Object bean;
		
		bean = getSingleton(beanName);
		
		if(bean != null){
			
			bean = getObjectForBeanInstance(bean, name, beanName, null);
			
		}else{
			
			final RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
			
			if(mbd.isSingleton()){
				
				bean = getSingleton(beanName, new ObjectFactory<Object>() {

					@Override
					public Object getObject() throws BeansException {
						
						return createBean(beanName, mbd);
					}

				});
				
			}else if( mbd.isPrototype()){
				
				
			}else{
				
				
			}
			
			
			
		}
		
		return bean;
	}

	public RootBeanDefinition getMergedLocalBeanDefinition(String beanName) throws BeansException {
		RootBeanDefinition mbd = this.mergedBeanDefinitions.get(beanName);
		if(mbd != null){
			return mbd;
		}
		
		return getMergedBeanDefinition(beanName, getBeanDefinition(beanName));
	}
	
	/**
	 * 获取将parent合并后的RootBeanDefinition
	 * @param beanName
	 * @param bd
	 * @return
	 * @throws BeansException
	 */
	public RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition bd) throws BeansException{
		RootBeanDefinition mbd = null;
		
		if(bd.getParentName() == null){
			
			mbd = new RootBeanDefinition(bd);
			
		}else{
			
			BeanDefinition pbd = getBeanDefinition(bd.getParentName());
			if(pbd == null){
				throw new BeansException("你获取的bean不存在"+beanName);
			}
			mbd = new RootBeanDefinition(pbd);
			mbd.overrideFrom(bd);
		}
		
		//设置scope默认为singleton
		if( !StringUtils.hasLength(mbd.getScope())){
			mbd.setScope( RootBeanDefinition.SCOPE_SINGLETON);
		}
		
		return mbd;
	}
	
	public BeanDefinition getBeanDefinition(String beanName) throws BeansException{
		BeanDefinition bd = this.beanDefinitionMap.get(beanName);
		if(bd == null){
			throw new BeansException("你要获取的"+beanName+"不存在!");
		}
		return bd;
	}

	public Object getObjectForBeanInstance(Object beanInstance, String name, String beanName, Object mbd) throws BeansException {
		
		//&name -> factory || &name -> bean || name -> bean || name -> factory || 
		//判断获取的名称和bean实例是否对应
		if( BeanFactoryUtil.isFactoryDeference(name) && !(beanInstance instanceof FactoryBean) ){
			throw new BeansException("你要获取的bean不是一个factory");
		}
		
		//&name -> factory || name -> bean || name -> factory ||
//		if( BeanFactoryUtil.isFactoryDeference(name) && (bean instanceof FactoryBean)){
//			return bean;
//		}
//		if( !BeanFactoryUtil.isFactoryDeference(name) && !(bean instanceof FactoryBean)){
//			return bean;
//		}
		//以上两句换为下面这句
		if( !(beanInstance instanceof FactoryBean) || BeanFactoryUtil.isFactoryDeference(name)){
			return beanInstance;
		}
		
		//到这里，可以明确是获取name -> factory
		Object object = null;
		
		object = getCachedObjectForFactoryBean(name);
		
		if(object == null){
			
			FactoryBean<?> factoryBean = (FactoryBean<?>) beanInstance;
			
			//从factoryBean中获取bean
			object = getObjectFromFactoryBean(factoryBean, beanName);
			
		}
		
		
		return object;
	}

	
	public Object createBean(final String beanName,final RootBeanDefinition mbd) throws BeansException {
		//TODO 验证及准备覆盖的方法
		
		//TODO 给BeanPostProcessor一个机会返回代理来代替真正的实例
		
		//创建bean
		Object beanInstance = doCreateBean(beanName, mbd);
		
		
		return beanInstance;
	}

	
	public Object doCreateBean(String beanName, RootBeanDefinition mbd) throws BeansException {
		
		return createBeanInstance(beanName, mbd, null);
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
				//TODO
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
	
	@SuppressWarnings("rawtypes")
	public Object autowireConstructor(String beanName, RootBeanDefinition mbd, Constructor[] chosenCtors, Object[] explicitArgs) throws BeansException{
		
		Constructor<?> constructorToUse = null;
		Object[] argsToUse = null;
		
		if(explicitArgs != null){
			argsToUse = explicitArgs;
		}else{
			
			Object[] argsToResolve = null;
			synchronized (mbd.constructorArgumentLock) {
				constructorToUse = (Constructor<?>) mbd.resolvedConstructorOrFactoryMethod;
				if( constructorToUse != null && mbd.constructorArgumentsResolved){
					argsToUse = mbd.resolvedConstructorArguments;
					if(argsToUse == null){
						argsToResolve = mbd.preparedConstructorArguments;
					}
				}
			}
			
			if(argsToResolve != null){
				argsToUse = resolvePreparedArguments(beanName, mbd, constructorToUse, argsToResolve);
			}
			
		}
		
		if(constructorToUse == null){
			
			Class<?> beanClass = mbd.getBeanClass();
			Constructor[] ctors = beanClass.getConstructors();
			ConstructorArgumentValues constructorArgumentValues = mbd.getConstructorArgumentValues();
			int argsLength = constructorArgumentValues.getIndexedArgumentValues().keySet().size();
			
			for (Constructor ctor : ctors) {
				if(ctor.getParameterTypes().length == argsLength){
					constructorToUse = ctor;
					break;
				}
			}
			
			
			if(constructorToUse == null){
				throw new IllegalStateException(beanName+"构造方法没找到");
			}
			
			Map<Integer, ValueHolder> valueMap = mbd.getConstructorArgumentValues().getIndexedArgumentValues();
			Object[] argsToResolve = new Object[argsLength];
			for(int i = 0; i < argsLength; i++){
				argsToResolve[i] = valueMap.get(new Integer(i)).getValue();
			}
			argsToUse = resolvePreparedArguments(beanName, mbd, constructorToUse, argsToResolve);
		}
		
		return getInstantiationStrategy().instantiate(mbd, beanName, this, constructorToUse, argsToUse);
	}

	private Object[] resolvePreparedArguments(String beanName, RootBeanDefinition mbd, 
			Constructor<?> constructorToUse,Object[] argsToResolve) {
		
		Object[] argsToUse = new Object[argsToResolve.length];
		Class<?>[] paramTypes = constructorToUse.getParameterTypes();
		for(int i = 0; i < argsToResolve.length; i++){
			Class<?> paramType = paramTypes[i];
			argsToUse[i] = convertIfNecessary(paramType, argsToResolve[i]);
		}
		
		return argsToUse;
	}

	private Object convertIfNecessary(Class<?> paramType, Object object) {
		
		if(object instanceof TypedStringValue){
			TypedStringValue value = (TypedStringValue) object;
			if( "int".equals(paramType.getCanonicalName())){
				int arg = Integer.parseInt(value.getValue());
				return arg;
			}else if("java.lang.Integer".equals(paramType.getCanonicalName())){
				return Integer.parseInt(value.getValue());
			}else if("java.lang.String".equals(paramType.getCanonicalName())){
				return value.getValue();
			}
		}
		
		return null;
	}

	public Object instantiateBean(String beanName, RootBeanDefinition bd) throws BeansException {
		
		return getInstantiationStrategy().instantiate(bd, beanName, this);
	}

	public InstantiationStrategy getInstantiationStrategy() {
		if(this.instantiationStrategy == null){
			this.instantiationStrategy = new SimpleInstantiationStrategy();
		}
		return this.instantiationStrategy;
	}
}
