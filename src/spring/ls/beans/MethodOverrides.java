package spring.ls.beans;

import java.util.HashSet;
import java.util.Set;

public class MethodOverrides {

	Set<MethodOverride> overrides = new HashSet<MethodOverride>();

	public MethodOverrides(){
		
	}
	
	public MethodOverrides(MethodOverrides other){
		addOverrides(other);
	}
	
	public Set<MethodOverride> getOverrides() {
		return overrides;
	}

	public void setOverrides(Set<MethodOverride> overrides) {
		this.overrides = overrides;
	}
	
	public void addOverride(MethodOverride overrides){
		this.overrides.add(overrides);
	}
	
	public void addOverrides(MethodOverrides other){
		if(other != null){
			this.overrides.addAll(other.getOverrides());
		}
	}
	
	public boolean isEmpty(){
		return this.overrides.isEmpty();
	}
}
