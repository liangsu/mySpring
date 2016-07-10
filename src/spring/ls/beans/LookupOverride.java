package spring.ls.beans;

import java.lang.reflect.Method;

public class LookupOverride extends MethodOverride{

	private final String beanName;
	
	private Method method;
	
	public LookupOverride(String methodName, String beanName) {
		super(methodName);
		this.beanName = beanName;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public String getBeanName() {
		return beanName;
	}
	
}
