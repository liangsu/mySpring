package spring.ls.beans.factory.config;

import spring.ls.beans.AbstractBeanDefinition;

public class GenericBeanDefinition extends AbstractBeanDefinition{
	
	private String parentName;

	public String getParentName() {
		return parentName;
	}
	
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
}
