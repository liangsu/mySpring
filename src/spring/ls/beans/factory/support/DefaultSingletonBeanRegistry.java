package spring.ls.beans.factory.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import spring.ls.beans.BeansException;
import spring.ls.beans.factory.ObjectFactory;
import spring.ls.beans.factory.config.SingletonBeanRegistry;
import spring.ls.core.SimpleAliasRegistry;
import spring.ls.util.StringUtils;

public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry{

	protected static final Object NULL_OBJECT = new Object();
	
	/** 用来存储单例对象，包含：单例bean、单例factoryBean */
	private final Map<String, Object> singletonObjects = new HashMap<String, Object>();
	private final Map<String, Object> earlySingletonObjects = new HashMap<String, Object>();
	private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<String, ObjectFactory<?>>();
	
	/** refName -> beans */
	private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap<String, Set<String>>(64);
	
	/** beanName -> beans */
	private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<String, Set<String>>(64);
	
	/** 保存了注册了的单例bean顺序 */
	private final Set<String> registeredSingletons = new LinkedHashSet<String>(64);
	
	/** 正在创建的bean */
	private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(16));

	/** 好像是当前bean依赖的bean名称，用于循环检测 */
	private final Set<String> inCreationCheckExclusions = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(16));
	
	@Override
	public void registerSingleton(String beanName, Object singletonObject) {
		Object oldSingletonObject = this.singletonObjects.get(beanName);
		if(oldSingletonObject != null){
			throw new IllegalStateException(beanName+"已经被注册了");
		}
	}

	protected void addSingleton(String beanName, Object singletonObject) {
		synchronized (this.singletonObjects) {
			this.singletonObjects.put(beanName, singletonObject != null ? singletonObject : NULL_OBJECT);
			this.earlySingletonObjects.remove(beanName);
			this.singletonFactories.remove(beanName);
			this.registeredSingletons.add(beanName);
		}
	}

	@Override
	public Object getSingleton(String beanName) throws BeansException {
		return getSingleton(beanName, true);
	}
	
	protected Object getSingleton(String beanName, boolean allowEarlyReference) throws BeansException {
		Object singletonObject = this.singletonObjects.get(beanName);
		if( singletonObject == null && isSingletonCurrentlyInCreation(beanName)){
			synchronized (this.singletonObjects) {
				singletonObject = this.singletonObjects.get(beanName);
				if(singletonObject == null){
					singletonObject = this.earlySingletonObjects.get(beanName);
					if( singletonObject == null && allowEarlyReference){
						ObjectFactory<?> objectFactory = this.singletonFactories.get(beanName);
						if(objectFactory != null ){
							singletonObject = objectFactory.getObject();
							this.earlySingletonObjects.put(beanName, singletonObject);
							this.singletonFactories.remove(beanName);
						}
					}
				}
			}
		}
		
		return singletonObject;
	}
	
	/**
	 * 创建bean，检测循环依赖
	 * @param beanName
	 * @param objectFactory
	 * @return
	 * @throws BeansException
	 */
	protected Object getSingleton(String beanName, ObjectFactory<Object> objectFactory) throws BeansException {
		synchronized (this.singletonObjects) {
			//首先检查对应的bean是否已经加载过了，因为singleton模式就是复用已创建的bean，所以这一步是必须的
			Object singletonObject = this.singletonObjects.get(beanName);
			if(singletonObject == null){
				//
				beforeSingletonCreation(beanName);
				try {
					singletonObject = objectFactory.getObject();
					
				} catch (Exception e) {
					throw new BeansException("创建失败"+beanName,e);
				}finally {
					afterSingletonCreation(beanName);
				}
				
				addSingleton(beanName, singletonObject);
			}
			return singletonObject;
		}
	}
	
	protected void beforeSingletonCreation(String beanName) {
		if( !this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.add(beanName)){
			throw new IllegalStateException("你要获取的bean："+beanName+"正在创建中");
		}
	}
	
	protected void afterSingletonCreation(String beanName) {
		if( !this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.remove(beanName)){
			throw new IllegalStateException("你要获取的bean："+beanName+"没有创建");
		}
	}
	
	protected boolean isSingletonCurrentlyInCreation(String beanName) {
		return this.singletonsCurrentlyInCreation.contains(beanName);
	}

	@Override
	public boolean containsSingleton(String beanName) {
		return this.singletonObjects.containsKey(beanName);
	}

	@Override
	public String[] getSingletonNames() {
		synchronized (this.singletonObjects) {
			return StringUtils.toStringArray(this.registeredSingletons);
		}
	}

	@Override
	public int getSingletonCount() {
		synchronized (this.singletonObjects) {
			return this.registeredSingletons.size();
		}
	}
	
	/**
	 * 注册依赖
	 * @param beanName
	 * @param dependentBeanName
	 */
	public void registerDependentBean(String beanName, String dependentBeanName) {
		String canonicalName = canonicalName(beanName);
		Set<String> dependentBeans = this.dependentBeanMap.get(canonicalName);
		if( dependentBeans != null && dependentBeans.contains(dependentBeanName)){
			return ;
		}
		
		synchronized (this.dependentBeanMap) {
			dependentBeans = this.dependentBeanMap.get(canonicalName);
			if(dependentBeans == null){
				dependentBeans = new HashSet<String>(8);
				this.dependentBeanMap.put(beanName, dependentBeans);
			}
			dependentBeans.add(dependentBeanName);
		}
		synchronized (this.dependenciesForBeanMap) {
			Set<String> dependenciesForBean = this.dependenciesForBeanMap.get(dependentBeanName);
			if (dependenciesForBean == null) {
				dependenciesForBean = new LinkedHashSet<String>(8);
				this.dependenciesForBeanMap.put(dependentBeanName, dependenciesForBean);
			}
			dependenciesForBean.add(canonicalName);
		}
	}
	
	/**
	 * 提前曝光bean工厂
	 * @param beanName
	 * @param objectFactory
	 */
	protected void addSingletonFactory(String beanName, ObjectFactory<?> objectFactory) {
		synchronized (this.singletonObjects) {
			if(! this.singletonObjects.containsKey(beanName)){
				this.singletonFactories.put(beanName, objectFactory);
				this.earlySingletonObjects.remove(beanName);
				this.registeredSingletons.add(beanName);
			}
		}
	}
	
}
