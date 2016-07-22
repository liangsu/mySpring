package spring.ls.beans.factory.support;

import java.util.HashMap;
import java.util.Map;

import spring.ls.beans.BeanDefinition;
import spring.ls.beans.BeanWrapperImpl;
import spring.ls.beans.BeansException;
import spring.ls.beans.TypeConverter;
import spring.ls.beans.factory.BeanFactory;
import spring.ls.beans.factory.BeanFactoryUtils;
import spring.ls.beans.factory.FactoryBean;
import spring.ls.beans.factory.ObjectFactory;
import spring.ls.core.DefaultParameterNameDiscoverer;
import spring.ls.core.ParameterNameDiscoverer;
import spring.ls.util.ClassUtils;
import spring.ls.util.StringUtils;

public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements BeanFactory,BeanDefinitionRegistry{

	/** Parent bean factory, for bean inheritance support */
	private BeanFactory parentBeanFactory;
	
	/** 用于加载bean的类加载器 */
	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
	
	/** 将parent合并后的beanDefinition */
	private final Map<String, RootBeanDefinition> mergedBeanDefinitions = new HashMap<String, RootBeanDefinition>();
	
	/** 用于获取参数名称 */
	private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
	
	/** 自定义的类型转换器 */
	private TypeConverter typeConverter;
	
	public AbstractBeanFactory(){
		
	}
	
	@Override
	public Object getBean(String alias) throws BeansException{
		return doGetBean(alias, null);
	}
	
	protected Object doGetBean(String name, final Object[] args) throws BeansException{
		
		final String beanName = canonicalName(name);
		
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
						
						return createBean(beanName, mbd, args);
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
	
	public Object getObjectForBeanInstance(Object beanInstance, String name, String beanName, Object mbd) throws BeansException {
		
		//&name -> factory || &name -> bean || name -> bean || name -> factory || 
		//判断获取的名称和bean实例是否对应
		if( BeanFactoryUtils.isFactoryDereference(name) && !(beanInstance instanceof FactoryBean) ){
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
		if( !(beanInstance instanceof FactoryBean) || BeanFactoryUtils.isFactoryDereference(name)){
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
	
	protected void clearMergedBeanDefinition(String beanName) {
		this.mergedBeanDefinitions.remove(beanName);
	}
	
	/**
	 * 销毁单例bean
	 * @param beanName
	 */
	public void destroySingleton(String beanName) {
		
	}
	
	public ParameterNameDiscoverer getParameterNameDiscoverer() {
		return parameterNameDiscoverer;
	}

	public TypeConverter getCustomTypeConverter() {
		return this.typeConverter;
	}
	
	public ClassLoader getBeanClassLoader() {
		return beanClassLoader;
	}

	public void setBeanClassLoader(ClassLoader beanClassLoader) {
		this.beanClassLoader = (beanClassLoader != null ? beanClassLoader : ClassUtils.getDefaultClassLoader());
	}
	
	public void initBeanWrapper(BeanWrapperImpl bw) {
		//TODO 
	}

	public BeanFactory getParentBeanFactory() {
		return parentBeanFactory;
	}
	
	@Override
	public boolean containsBean(String name) {
		String beanName = transformedBeanName(name);
		if (containsSingleton(beanName) || containsBeanDefinition(beanName)) {
			//return (!BeanFactoryUtils.isFactoryDereference(name) || isFactoryBean(name));
			return !BeanFactoryUtils.isFactoryDereference(name);
		}
		// Not found -> check parent.
		BeanFactory parentBeanFactory = getParentBeanFactory();
		return (parentBeanFactory != null && parentBeanFactory.containsBean(originalBeanName(name)));
	}
	
	/**
	 * Determine the original bean name, resolving locally defined aliases to canonical names.
	 * @param name the user-specified name
	 * @return the original bean name
	 */
	protected String originalBeanName(String name) {
		String beanName = transformedBeanName(name);
		if (name.startsWith(FACTORY_BEAN_PREFIX)) {
			beanName = FACTORY_BEAN_PREFIX + beanName;
		}
		return beanName;
	}
	
	protected String transformedBeanName(String name) {
		return canonicalName(BeanFactoryUtils.transformedBeanName(name));
	}
	
	protected abstract boolean containsBeanDefinition(String beanName);
	
	protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;
	
	protected abstract Object createBean(String beanName, RootBeanDefinition mbd, Object[] args)
			throws BeansException;

}
