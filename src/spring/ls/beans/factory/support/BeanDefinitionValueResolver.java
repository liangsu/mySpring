package spring.ls.beans.factory.support;

import spring.ls.beans.BeanDefinition;
import spring.ls.beans.BeansException;
import spring.ls.beans.TypeConverter;
import spring.ls.beans.factory.config.RuntimeBeanReference;
import spring.ls.beans.factory.config.TypedStringValue;

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

	public Object resolveValueIfNecessary(String argName, Object value){
		
		if( value instanceof RuntimeBeanReference){
			RuntimeBeanReference ref = (RuntimeBeanReference) value;
			return resolveReference(argName, ref);
			
		}else if(value instanceof TypedStringValue){
			TypedStringValue typedStringValue = (TypedStringValue) value;
			Object valueObject = evaluate(typedStringValue);
			try {
				Class<?> targetType = resolvedTargetType(typedStringValue);
				
				if(targetType != null){
					return typeConverter.convertIfNecessary(valueObject, targetType);
				}else{
					return valueObject;
				}
				
			} catch (Exception e) {
				throw new BeansException("解析参数失败"+value);
			}
			
		}else{
			
		}
		
		return value;
	}
	
	private Class<?> resolvedTargetType(TypedStringValue value) throws ClassNotFoundException {
		if(value.hasTargetType()){
			return value.getTargetType();
		}
		return value.resolveTargetType(beanFactory.getClass().getClassLoader());
	}

	private Object resolveReference(String argName, RuntimeBeanReference ref){
		String refName = ref.getBeanName();
		//TODO 其它操作...
		
		return this.beanFactory.getBean(refName);
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
