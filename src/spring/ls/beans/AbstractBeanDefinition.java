package spring.ls.beans;

public abstract class AbstractBeanDefinition extends BeanMetadataAttributeAccessor implements BeanDefinition{

	private volatile Object beanClass;
	private String scope;
	
	@Override
	public void setBeanClassName(String beanClassName) {
		this.beanClass = beanClassName;
	}

	@Override
	public String getBeanClassName() {
		Object beanClassObject = this.beanClass;
		if(beanClassObject instanceof Class){
			return ((Class<?>) beanClassObject).getName();
		}
		return (String) beanClassObject;
	}

	@Override
	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	@Override
	public Class<?> getBeanClass() {
		Object beanClassObject = this.beanClass;
		if(beanClassObject == null){
			throw new IllegalStateException("no bean can get");
		}
		if(!(beanClassObject instanceof Class)){
			throw new IllegalStateException("bean class name["+beanClassObject+"] does not init");
		}
		return (Class<?>) beanClassObject;
	}

	@Override
	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
	public String getScope() {
		return scope;
	}

	@Override
	public boolean isPrototype() {
		return SCOPE_PROTOTYPE.equals(scope);
	}

	@Override
	public boolean isSingleton() {
		return SCOPE_SINGLETION.equals(scope);
	}

}
