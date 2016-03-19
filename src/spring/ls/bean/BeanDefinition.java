package spring.ls.bean;

/**
 * 实例化对象定义的持有者
 * @author lenovo
 *
 */
public class BeanDefinition {

	private Class<?> beanClass;
	private String name;
	private String scope;
	
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public Class<?> getBeanClass() {
		return beanClass;
	}
	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
