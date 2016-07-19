package spring.ls.beans.factory.support;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import spring.ls.beans.BeanDefinition;
import spring.ls.beans.BeansException;
import spring.ls.beans.TypeConverter;
import spring.ls.beans.factory.BeanFactory;
import spring.ls.beans.factory.BeanFactoryUtil;
import spring.ls.beans.factory.FactoryBean;
import spring.ls.beans.factory.ObjectFactory;
import spring.ls.core.DefaultParameterNameDiscoverer;
import spring.ls.core.ParameterNameDiscoverer;
import spring.ls.util.StringUtils;

public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements BeanFactory,BeanDefinitionRegistry{
	private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>(64);
	
	/** 将parent合并后的beanDefinition */
	private final Map<String, RootBeanDefinition> mergedBeanDefinitions = new HashMap<String, RootBeanDefinition>();
	
	/** 用于获取参数名称 */
	private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
	
	/** 自定义的类型转换器 */
	private TypeConverter typeConverter;
	
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
		return doGetBean(alias, null);
	}
	
	public Object doGetBean(String name, Object[] args) throws BeansException{
		
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
						
						return createBean(beanName, mbd, null);
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
	
	public ParameterNameDiscoverer getParameterNameDiscoverer() {
		return parameterNameDiscoverer;
	}

	public TypeConverter getCustomTypeConverter() {
		return this.typeConverter;
	}
	
	protected abstract Object createBean(String beanName, RootBeanDefinition mbd, Object[] args)
			throws BeansException;

}
