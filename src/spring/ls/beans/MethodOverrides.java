package spring.ls.beans;

import java.util.HashSet;
import java.util.Set;

public class MethodOverrides {

	Set<MethodOverride> methodOverrides = new HashSet<MethodOverride>();

	public Set<MethodOverride> getMethodOverrides() {
		return methodOverrides;
	}

	public void setMethodOverrides(Set<MethodOverride> methodOverrides) {
		this.methodOverrides = methodOverrides;
	}
	
	public void addMethodOverride(MethodOverride methodOverride){
		this.methodOverrides.add(methodOverride);
	}
}
