package spring.ls.beans.factory.config;

import spring.ls.beans.BeanDefinition;

public class BeanDefinitionHolder {
	private BeanDefinition beanDefinition;
	private String beanName;
	private String[] asliases;
	
	public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName, String[] asliases) {
		super();
		this.beanDefinition = beanDefinition;
		this.beanName = beanName;
		this.asliases = asliases;
	}
	public BeanDefinitionHolder(String beanName, String[] asliases) {
		super();
		this.beanName = beanName;
		this.asliases = asliases;
	}
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	public String[] getAsliases() {
		return asliases;
	}
	public void setAsliases(String[] asliases) {
		this.asliases = asliases;
	}
	public BeanDefinition getBeanDefinition() {
		return beanDefinition;
	}
	public void setBeanDefinition(BeanDefinition beanDefinition) {
		this.beanDefinition = beanDefinition;
	}
}
