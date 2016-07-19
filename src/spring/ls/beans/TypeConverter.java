package spring.ls.beans;

import java.lang.reflect.Field;

import spring.ls.core.MethodParameter;

public interface TypeConverter {

	<T> T convertIfNecessary(Object value, Class<T> requiredType) throws BeansException;

	<T> T convertIfNecessary(Object value, Class<T> requiredType, MethodParameter methodParam) throws BeansException;
	
	<T> T convertIfNecessary(Object value, Class<T> requiredType, Field field) throws BeansException;
}
