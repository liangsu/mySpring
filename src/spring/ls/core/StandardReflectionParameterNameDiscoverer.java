package spring.ls.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class StandardReflectionParameterNameDiscoverer implements ParameterNameDiscoverer{

	@Override
	public String[] getParameterNames(Method method) {
		Parameter[] parameters = method.getParameters();
		String[] paramNames = new String[parameters.length];
		for(int i = 0; i < parameters.length; i++){
			Parameter parameter = parameters[i];
			if( !parameter.isNamePresent()){
				return null;
			}
			paramNames[i] = parameter.getName();
		}
		return null;
	}

	@Override
	public String[] getParameterNames(Constructor<?> ctor) {
		Parameter[] parameters = ctor.getParameters();
		String[] paramNames = new String[parameters.length];
		for(int i = 0; i < parameters.length; i++){
			Parameter parameter = parameters[i];
			if( !parameter.isNamePresent()){
				return null;
			}
			paramNames[i] = parameter.getName();
		}
		return null;
	}

}
