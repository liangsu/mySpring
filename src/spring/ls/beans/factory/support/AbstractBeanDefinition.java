package spring.ls.beans.factory.support;

import spring.ls.beans.BeanDefinition;
import spring.ls.beans.BeanMetadataAttributeAccessor;
import spring.ls.beans.MethodOverrides;

public abstract class AbstractBeanDefinition extends BeanMetadataAttributeAccessor implements BeanDefinition{

	private volatile Object beanClass;
	
	private String scope;
	
	private MethodOverrides methodOverrides;
	
	public AbstractBeanDefinition() {
		
	}
	
	public AbstractBeanDefinition(BeanDefinition original){
		setBeanClassName(original.getBeanClassName());
		setScope(original.getScope());
		
		if(original instanceof AbstractBeanDefinition){
			AbstractBeanDefinition bd = (AbstractBeanDefinition) original;
			if(bd.hasBeanClass()){
				setBeanClass(bd.getBeanClass());
			}
			setMethodOverrides(bd.getMethodOverrides());
		}
	}
	
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
		return SCOPE_SINGLETON.equals(scope);
	}
	
	public MethodOverrides getMethodOverrides() {
		return methodOverrides;
	}
	
	public void setMethodOverrides(MethodOverrides methodOverrides) {
		this.methodOverrides = methodOverrides;
	}
	
	public boolean hasBeanClass(){
		return (beanClass instanceof Class);
	}
}
