package spring.ls.beans.factory.support;

import spring.ls.beans.BeanDefinition;
import spring.ls.beans.BeansException;
import spring.ls.beans.TypeConverter;
import spring.ls.beans.factory.config.RuntimeBeanReference;
import spring.ls.beans.factory.config.TypedStringValue;

/**
 * 用于解决bean实例化的依赖问题
 * @author warhorse
 *
 */
public class BeanDefinitionValueResolver {

	 private final AbstractBeanFactory beanFactory;
	 
	 private final String beanName;
	 
	 private final BeanDefinition beanDefinition;
	 
	 private final TypeConverter typeConverter;

	public BeanDefinitionValueResolver(AbstractBeanFactory beanFactory, String beanName, BeanDefinition beanDefinition,
			TypeConverter typeConverter) {
		super();
		this.beanFactory = beanFactory;
		this.beanName = beanName;
		this.beanDefinition = beanDefinition;
		this.typeConverter = typeConverter;
	}

	public Object resolveValueIfNecessary(Object argName, Object value){
		
		if( value instanceof RuntimeBeanReference){
			RuntimeBeanReference ref = (RuntimeBeanReference) value;
			return resolveReference(argName, ref);
			
		}else if(value instanceof TypedStringValue){
			TypedStringValue typedStringValue = (TypedStringValue) value;
			Object valueObject = evaluate(typedStringValue);
			try {
				Class<?> targetType = resolvedTargetType(typedStringValue);
				
				if(targetType != null){
					return this.typeConverter.convertIfNecessary(valueObject, targetType);
				}else{
					return valueObject;
				}
				
			} catch (Exception e) {
				throw new BeansException(this.beanName+"的参数解析失败"+value);
			}
			
		}else{
			
		}
		
		return value;
	}
	
	private Class<?> resolvedTargetType(TypedStringValue value) throws ClassNotFoundException {
		if(value.hasTargetType()){
			return value.getTargetType();
		}
		return value.resolveTargetType(this.beanFactory.getBeanClassLoader());
	}

	private Object resolveReference(Object argName, RuntimeBeanReference ref){
		String refName = ref.getBeanName();
		if(ref.isToParent()){
			if (this.beanFactory.getParentBeanFactory() == null) {
				throw new BeansException("依赖创建的bean:"+refName+"的在parentFactory，没有可用的parentFactory");
			}
			return this.beanFactory.getParentBeanFactory().getBean(refName);
			
		}else{
			Object refBean = this.beanFactory.getBean(refName);
			this.beanFactory.registerDependentBean(refName, this.beanName);
			return refBean;
		}
	}
	
	private Object evaluate(TypedStringValue value) {
		Object result = doEvaluate(value.getValue());
		if( result == value){
			value.setDynamic();
		}
		return result;
	}
	
	private Object doEvaluate(String value){
		//TODO 处理表达式
		
		return value;
	}
}
