package spring.ls.beans;

import java.lang.reflect.Field;

import spring.ls.core.MethodParameter;

public abstract class TypeConverterSupport extends PropertyEditorRegistrySupport implements TypeConverter{

	TypeConverterDelegate typeConverterDelegate;
	
	@Override
	public <T> T convertIfNecessary(Object value, Class<T> requiredType) throws BeansException {
		return doConvert(value, requiredType, null, null);
	}

	@Override
	public <T> T convertIfNecessary(Object value, Class<T> requiredType, MethodParameter methodParam)
			throws BeansException {
		return doConvert(value, requiredType, methodParam, null);
	}

	@Override
	public <T> T convertIfNecessary(Object value, Class<T> requiredType, Field field) throws BeansException {
		return doConvert(value, requiredType, null, field);
	}

	private <T> T doConvert(Object value, Class<T> requiredType, MethodParameter methodParam, Field field)
			throws BeansException {
		try {
			
			if( field != null){
				return typeConverterDelegate.convertIfNecessary(value, requiredType, field);
				
			}else{
				return typeConverterDelegate.convertIfNecessary(value, requiredType, methodParam);
			}
			
		} catch (Exception e) {
			throw new BeansException(value+"值转换失败!",e);
		}
		
	}
}
