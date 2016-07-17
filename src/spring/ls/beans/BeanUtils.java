package spring.ls.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class BeanUtils {

	public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws BeansException{
		try {
			return ctor.newInstance(args);
		} catch (InstantiationException e) {
			throw new BeansException(ctor.getDeclaringClass()+"是一个抽象的class", e);
			
		} catch (IllegalAccessException e) {
			throw new BeansException(ctor.getDeclaringClass()+"的构造方法是可访问的吗？", e);
			
		} catch (IllegalArgumentException e) {
			throw new BeansException(ctor.getDeclaringClass()+"的构造方法的参数不合法", e);
			
		} catch (InvocationTargetException e) {
			throw new BeansException(ctor.getDeclaringClass()+"构造方法抛出异常", e);
			
		}
	}
}
