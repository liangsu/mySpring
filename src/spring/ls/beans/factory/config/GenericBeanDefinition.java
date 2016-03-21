package spring.ls.beans.factory.config;

public class GenericBeanDefinition extends AbstractBeanDefinition{
	
	private String parentName;

	public String getParentName() {
		return parentName;
	}
	
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
}
