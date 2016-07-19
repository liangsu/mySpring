package spring.ls.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * 实现了尝试几种ParameterNameDiscoverer策略获取参数名称，
 * 排在前面的最先尝试，当排在前面的没有获取到参数名称，后面的会陆续尝试获取参数名称
 * @author warhorse
 *
 */
public class PrioritizedParameterNameDiscoverer implements ParameterNameDiscoverer{

	private List<ParameterNameDiscoverer> discovers = new LinkedList<ParameterNameDiscoverer>();
	
	public void addDiscover(ParameterNameDiscoverer discoverer){
		this.discovers.add(discoverer);
	}
	
	@Override
	public String[] getParameterNames(Method method) {
		for (ParameterNameDiscoverer pnd : discovers) {
			String[] result = pnd.getParameterNames(method);
			if(result != null){
				return result;
			}
		}
		return null;
	}

	@Override
	public String[] getParameterNames(Constructor<?> ctor) {
		for (ParameterNameDiscoverer pnd : discovers) {
			String[] result = pnd.getParameterNames(ctor);
			if(result != null){
				return result;
			}
		}
		return null;
	}

}
