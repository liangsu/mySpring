package spring.ls.beans;

import java.lang.reflect.Field;

import spring.ls.beans.factory.config.TypedStringValue;
import spring.ls.core.MethodParameter;

public class TypeConverterDelegate {

	private final PropertyEditorRegistrySupport propertyEditorRegistry;

	private final Object targetObject;
	
	public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry){
		this(propertyEditorRegistry, null);
	}
	
	public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry,Object targetObject) {
		this.propertyEditorRegistry = propertyEditorRegistry;
		this.targetObject = targetObject;
	}

	public <T> T convertIfNecessary(Object value, Class<T> requiredType, Field field) {

		return null;
	}

	public <T> T convertIfNecessary(Object value, Class<T> requiredType, MethodParameter methodParam) {
		
		if(value instanceof TypedStringValue){
			TypedStringValue typedStringValue = (TypedStringValue) value;
			if( "int".equals(requiredType.getCanonicalName())){
				Integer arg = Integer.parseInt(typedStringValue.getValue());
				return (T)arg;
			}else if("java.lang.Integer".equals(requiredType.getCanonicalName())){
				Integer arg = Integer.parseInt(typedStringValue.getValue());
				return (T) arg;
				
			}else if("java.lang.String".equals(requiredType.getCanonicalName())){
				return (T) typedStringValue.getValue();
			}
		}else if(value instanceof String){
			String strValue = (String) value;
			if( "int".equals(requiredType.getCanonicalName())){
				Integer arg = Integer.parseInt(strValue);
				return (T)arg;
			}else if("java.lang.Integer".equals(requiredType.getCanonicalName())){
				Integer arg = Integer.parseInt(strValue);
				return (T) arg;
				
			}else if("java.lang.String".equals(requiredType.getCanonicalName())){
				return (T) strValue;
			}
		}
		
		return (T) value;
	}

}
