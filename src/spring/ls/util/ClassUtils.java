package spring.ls.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassUtils {

	/** Suffix for array class names: "[]" */
	public static final String ARRAY_SUFFIX = "[]";
	
	/**
	 * Map with primitive wrapper type as key and corresponding primitive
	 * type as value, for example: Integer.class -> int.class.
	 */
	private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new HashMap<Class<?>, Class<?>>(8);
	
	/**
	 * Map with primitive type as key and corresponding wrapper
	 * type as value, for example: int.class -> Integer.class.
	 */
	private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new HashMap<Class<?>, Class<?>>(8);

	static {
		primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
		primitiveWrapperTypeMap.put(Byte.class, byte.class);
		primitiveWrapperTypeMap.put(Character.class, char.class);
		primitiveWrapperTypeMap.put(Double.class, double.class);
		primitiveWrapperTypeMap.put(Float.class, float.class);
		primitiveWrapperTypeMap.put(Integer.class, int.class);
		primitiveWrapperTypeMap.put(Long.class, long.class);
		primitiveWrapperTypeMap.put(Short.class, short.class);

		for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
			primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
			//registerCommonClasses(entry.getKey());
		}

		Set<Class<?>> primitiveTypes = new HashSet<Class<?>>(32);
		primitiveTypes.addAll(primitiveWrapperTypeMap.values());
		primitiveTypes.addAll(Arrays.asList(new Class<?>[] {
				boolean[].class, byte[].class, char[].class, double[].class,
				float[].class, int[].class, long[].class, short[].class}));
		primitiveTypes.add(void.class);
		for (Class<?> primitiveType : primitiveTypes) {
			//primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
		}

//		registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class,
//				Float[].class, Integer[].class, Long[].class, Short[].class);
//		registerCommonClasses(Number.class, Number[].class, String.class, String[].class,
//				Object.class, Object[].class, Class.class, Class[].class);
//		registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class,
//				Error.class, StackTraceElement.class, StackTraceElement[].class);
	}
	
	
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

	/**
	 * 给定的类的限定名是否匹配给定的名称
	 * @param clazz
	 * @param typeName
	 * @return
	 */
	public static boolean matchesTypeName(Class<?> clazz, String typeName) {
		
		return (typeName != null &&
						(typeName.equals(clazz.getName()) || typeName.equals(clazz.getSimpleName()) ||
								(clazz.isArray() && typeName.equals(getQualifiedNameForArray(clazz)))));
	}
	
	/**
	 * Return the qualified name of the given class: usually simply
	 * the class name, but component type class name + "[]" for arrays.
	 * @param clazz the class
	 * @return the qualified name of the class
	 */
	public static String getQualifiedName(Class<?> clazz) {
		//Assert.notNull(clazz, "Class must not be null");
		if (clazz.isArray()) {
			return getQualifiedNameForArray(clazz);
		}
		else {
			return clazz.getName();
		}
	}
	
	/**
	 * 获取类的限定名
	 * @param clazz
	 * @return
	 */
	private static String getQualifiedNameForArray(Class<?> clazz) {
		StringBuilder result = new StringBuilder();
		while (clazz.isArray()) {
			clazz = clazz.getComponentType();
			result.append(ClassUtils.ARRAY_SUFFIX);
		}
		result.insert(0, clazz.getName());
		return result.toString();
	}
	
	/**
	 * Determine if the given type is assignable from the given value,
	 * assuming setting by reflection. Considers primitive wrapper classes
	 * as assignable to the corresponding primitive types.
	 * @param type the target type
	 * @param value the value that should be assigned to the type
	 * @return if the type is assignable from the value
	 */
	public static boolean isAssignableValue(Class<?> type, Object value) {
		return (value != null ? isAssignable(type, value.getClass()) : !type.isPrimitive());
	}
	
	/**
	 * Check if the right-hand side type may be assigned to the left-hand side
	 * type, assuming setting by reflection. Considers primitive wrapper
	 * classes as assignable to the corresponding primitive types.
	 * @param lhsType the target type
	 * @param rhsType the value type that should be assigned to the target type
	 * @return if the target type is assignable from the value type
	 * @see TypeUtils#isAssignable
	 */
	public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
//		Assert.notNull(lhsType, "Left-hand side type must not be null");
//		Assert.notNull(rhsType, "Right-hand side type must not be null");
		if (lhsType.isAssignableFrom(rhsType)) {
			return true;
		}
		if (lhsType.isPrimitive()) {
			Class<?> resolvedPrimitive = primitiveWrapperTypeMap.get(rhsType);
			if (resolvedPrimitive != null && lhsType.equals(resolvedPrimitive)) {
				return true;
			}
		}
		else {
			Class<?> resolvedWrapper = primitiveTypeToWrapperMap.get(rhsType);
			if (resolvedWrapper != null && lhsType.isAssignableFrom(resolvedWrapper)) {
				return true;
			}
		}
		return false;
	}
}
