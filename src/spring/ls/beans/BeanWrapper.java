package spring.ls.beans;

import java.beans.PropertyDescriptor;

public interface BeanWrapper extends ConfigurablePropertyAccessor{

	/**
	 * 获取包装类的实例
	 * @return
	 */
	Object getWrappedInstance();
	
	/**
	 * 获取包装类的Class
	 * @return
	 */
	Class<?> getWrappedClass();

	/**
	 * 获取包装类的PropertyDescriptor
	 * @return
	 */
	PropertyDescriptor[] getPropertyDescriptors();
}
