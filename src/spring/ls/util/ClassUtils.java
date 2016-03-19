package spring.ls.util;

public class ClassUtils {

	/**
	 * 获取默认类加载器
	 * @return
	 */
	public static ClassLoader getDefaultClassLoader(){
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable e) {
			// Cannot access thread context ClassLoader - falling back...
		}
		if(cl == null){
			//没有线程运行环境的类加载器，使用加载本类的类加载器
			cl = ClassUtils.class.getClassLoader();
			if(cl == null){
				// getClassLoader() returning null indicates the bootstrap ClassLoader
				try {
					cl = ClassLoader.getSystemClassLoader();
				} catch (Throwable e) {
					// Cannot access system ClassLoader - oh well, maybe the caller can live with null...
				}
			}
		}
		return cl;
	}
	
	public static Class<?> forName(String className,ClassLoader classLoader) throws ClassNotFoundException{
		Class<?> clazz = null;
		clazz = classLoader.loadClass(className);
		return clazz;
	}
}
