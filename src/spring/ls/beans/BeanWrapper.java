package spring.ls.beans;

public interface BeanWrapper extends TypeConverter{

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
}
