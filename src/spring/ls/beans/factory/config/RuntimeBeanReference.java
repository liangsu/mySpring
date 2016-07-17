package spring.ls.beans.factory.config;

public class RuntimeBeanReference implements BeanReference{

	private final String beanName;
	
	private final boolean toParent;
	
	private Object source;
	
	public RuntimeBeanReference(String beanName){
		this(beanName, false);
	}
	
	public RuntimeBeanReference(String beanName, boolean toParent) {
		this.beanName = beanName;
		this.toParent = toParent;
	}

	@Override
	public Object getSource() {
		return source;
	}

	@Override
	public String getBeanName() {
		return beanName;
	}

	public boolean isToParent() {
		return toParent;
	}

	public void setSource(Object source) {
		this.source = source;
	}

}
