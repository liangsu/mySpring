package spring.ls.beans.factory.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import spring.ls.core.MethodParameter;

public class DependencyDescriptor {

	private transient MethodParameter methodParameter;

	private transient Field field;

	private Class<?> declaringClass;

	private Class<?> containingClass;

	private String methodName;

	private Class<?>[] parameterTypes;

	private int parameterIndex;

	private String fieldName;

	private final boolean required;

	private final boolean eager;

	private int nestingLevel = 1;

	private transient Annotation[] fieldAnnotations;
	
	/**
	 * Create a new descriptor for a method or constructor parameter.
	 * Considers the dependency as 'eager'.
	 * @param methodParameter the MethodParameter to wrap
	 * @param required whether the dependency is required
	 */
	public DependencyDescriptor(MethodParameter methodParameter, boolean required) {
		this(methodParameter, required, true);
	}

	/**
	 * Create a new descriptor for a method or constructor parameter.
	 * @param methodParameter the MethodParameter to wrap
	 * @param required whether the dependency is required
	 * @param eager whether this dependency is 'eager' in the sense of
	 * eagerly resolving potential target beans for type matching
	 */
	public DependencyDescriptor(MethodParameter methodParameter, boolean required, boolean eager) {
		//Assert.notNull(methodParameter, "MethodParameter must not be null");
		this.methodParameter = methodParameter;
//		this.declaringClass = methodParameter.getDeclaringClass();
//		this.containingClass = methodParameter.getContainingClass();
//		if (this.methodParameter.getMethod() != null) {
//			this.methodName = methodParameter.getMethod().getName();
//			this.parameterTypes = methodParameter.getMethod().getParameterTypes();
//		}
//		else {
//			this.parameterTypes = methodParameter.getConstructor().getParameterTypes();
//		}
//		this.parameterIndex = methodParameter.getParameterIndex();
		this.required = required;
		this.eager = eager;
	}

	/**
	 * Create a new descriptor for a field.
	 * Considers the dependency as 'eager'.
	 * @param field the field to wrap
	 * @param required whether the dependency is required
	 */
	public DependencyDescriptor(Field field, boolean required) {
		this(field, required, true);
	}

	/**
	 * Create a new descriptor for a field.
	 * @param field the field to wrap
	 * @param required whether the dependency is required
	 * @param eager whether this dependency is 'eager' in the sense of
	 * eagerly resolving potential target beans for type matching
	 */
	public DependencyDescriptor(Field field, boolean required, boolean eager) {
		//Assert.notNull(field, "Field must not be null");
		this.field = field;
		this.declaringClass = field.getDeclaringClass();
		this.fieldName = field.getName();
		this.required = required;
		this.eager = eager;
	}

	/**
	 * Copy constructor.
	 * @param original the original descriptor to create a copy from
	 */
	public DependencyDescriptor(DependencyDescriptor original) {
		this.methodParameter = (original.methodParameter != null ? new MethodParameter(original.methodParameter) : null);
		this.field = original.field;
		this.declaringClass = original.declaringClass;
		this.containingClass = original.containingClass;
		this.methodName = original.methodName;
		this.parameterTypes = original.parameterTypes;
		this.parameterIndex = original.parameterIndex;
		this.fieldName = original.fieldName;
		this.required = original.required;
		this.eager = original.eager;
		this.nestingLevel = original.nestingLevel;
		this.fieldAnnotations = original.fieldAnnotations;
	}
	
}
