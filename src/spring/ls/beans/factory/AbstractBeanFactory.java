package spring.ls.beans.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import spring.ls.beans.BeanDefinition;
import spring.ls.beans.BeanUtils;
import spring.ls.beans.BeansException;
import spring.ls.beans.factory.support.BeanDefinitionRegistry;
import spring.ls.beans.factory.support.FactoryBeanRegistrySupport;
import spring.ls.beans.factory.support.RootBeanDefinition;
import spring.ls.util.ObjectUtils;
import spring.ls.util.StringUtils;

public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements BeanFactory<Object>,BeanDefinitionRegistry{
	private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>(64);
	
	/** 将parent合并后的beanDefinition */
	private final Map<String, RootBeanDefinition> mergedBeanDefinitions = new HashMap<String, RootBeanDefinition>();
	
	/** 用来存储单例对象，包含：单例bean、单例factoryBean */
	private final Map<String, Object> singletonObjects = new HashMap<String, Object>();
	private final Map<String, Object> earlySingletonObjects = new HashMap<String, Object>();
	private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<String, ObjectFactory<?>>();

	/** 保存了注册了的单例bean顺序 */
	private final Set<String> registeredSingletons = new LinkedHashSet<String>(64);
	
	/** 用来存储factoryBean的getObject()返回的对象 */
	private final Map<String, Object> factoryBeanObjectCache = new HashMap<String, Object>();
	
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
	
	/**
	 * 注册bean
	 * @param name
	 * @param instance
	 * @param scope
	 * @throws Exception
	 */
	private void registerBean(String name,Object instance,String scope) throws Exception{
		if(scope.equals(SCOPE_PROTOTYPE)){
			beans.put(name, instance);
		}else if(scope.equals(SCOPE_SINGLETON)){
			singletonObjects.put(name, instance);
		}
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
				
//				Class<?> clazz = mbd.getBeanClass();
//				try {
//					bean = clazz.newInstance();
//				} catch (Exception e) {
//					throw new BeansException("bean加载失败!");
//				}
				
			}else if( mbd.isPrototype()){
				
//				Class<?> clazz = mbd.getBeanClass();
//				try {
//					bean = clazz.newInstance();
//				} catch (Exception e) {
//					throw new BeansException("bean加载失败!");
//				}
				
			}else{
				
				
			}
			
			
			
		}
		
		return bean;
	}

	/**
	 * 创建bean，检测循环依赖
	 * @param beanName
	 * @param objectFactory
	 * @return
	 * @throws BeansException
	 */
	private Object getSingleton(String beanName, ObjectFactory<Object> objectFactory) throws BeansException {
		
		//首先检查对应的bean是否已经加载过了，因为singleton模式就是复用已创建的bean，所以这一步是必须的
		Object singletonObject = this.singletonObjects.get(beanName);
		if(singletonObject == null){
			
			singletonObject = objectFactory.getObject();
			if(singletonObject == null){
				throw new BeansException("创建失败"+beanName);
			}
			
			addSingleton(beanName, singletonObject);
		}
		
		return singletonObject;
	}

	public void addSingleton(String beanName, Object singletonObject) {
		this.singletonObjects.put(beanName, singletonObject);
		this.earlySingletonObjects.remove(beanName);
		this.singletonFactories.remove(beanName);
		this.registeredSingletons.add(beanName);
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

	public Object getSingleton(String beanName) throws BeansException {
		Object singletonObject = this.singletonObjects.get(beanName);
		
		if(singletonObject == null){
			singletonObject = this.earlySingletonObjects.get(beanName);
			
			if(singletonObject == null){
				ObjectFactory<?> objectFactory = this.singletonFactories.get(beanName);
				
				if(objectFactory != null){
					singletonObject = objectFactory.getObject();
					this.earlySingletonObjects.put(beanName, singletonObject);
					this.singletonFactories.remove(beanName);
				}
			}
			
		}
		return singletonObject;
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
		
		object = factoryBeanObjectCache.get(name);
		
		if(object == null){
			
			FactoryBean<?> factoryBean = (FactoryBean<?>) beanInstance;
			
			//从factoryBean中获取bean
			object = getObjectFromFactoryBean(factoryBean, beanName);
			
		}
		
		
		return object;
	}

	public Object getObjectFromFactoryBean(FactoryBean<?> factoryBean, String beanName) throws BeansException {
		
		//如果是单例
		if( factoryBean.isSingleton()){
			
			Object object = this.factoryBeanObjectCache.get(beanName);
			if(object == null){
				
				object = doGetObjectFromFactoryBean(factoryBean, beanName);
				
				this.factoryBeanObjectCache.put(beanName, object);
			}
			return object;
		}
		else {
			
			Object object = doGetObjectFromFactoryBean(factoryBean, beanName);
			
			return object;
		}
	}
	
	public Object doGetObjectFromFactoryBean(FactoryBean<?> factoryBean, String beanName) throws BeansException{
		Object object;
		
		try {
			object = factoryBean.getObject();
		} catch (Exception e) {
			throw new BeansException("创建beanName："+beanName+"失败!");
		}
		
		//TODO 调用objectFactory的后处理器
		
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
				return null;
			}else{
				return instantiateBean(beanName, mbd);
			}
		}
		
//		Constructor[] ctors = beanClass.getDeclaredConstructors();
//		if(ctors != null || !ObjectUtils.isEmpty(args)){
//			
//		}
		
		return instantiateBean(beanName, mbd);
	}

	public Object instantiateBean(String beanName, RootBeanDefinition bd) throws BeansException {
		
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
			return instantiateWithMethodInjection(beanName, bd);
		}
	}

	public Object instantiateWithMethodInjection(String beanName, RootBeanDefinition bd) {
		throw new UnsupportedOperationException("Method Injection not supported in SimpleInstantiationStrategy");
	}
}
