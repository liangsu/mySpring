package spring.ls.beans;

import java.beans.PropertyEditor;
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
		
		if(value instanceof String){
			PropertyEditor editor = this.propertyEditorRegistry.getDefaultEditor(requiredType);
			if(editor != null){
				editor.setAsText((String)value);
				return (T) editor.getValue();
			}
		}
		
		return (T) value;
	}

}
