package spring.ls.beans.factory.support;

import java.lang.reflect.Constructor;

import spring.ls.beans.BeanDefinition;
import spring.ls.beans.BeanMetadataAttributeAccessor;
import spring.ls.beans.MethodOverrides;
import spring.ls.beans.MutablePropertyValues;
import spring.ls.beans.factory.config.AutowireCapableBeanFactory;
import spring.ls.beans.factory.config.ConstructorArgumentValues;

public abstract class AbstractBeanDefinition extends BeanMetadataAttributeAccessor implements BeanDefinition{

	public static final int AUTOWIRE_NO = AutowireCapableBeanFactory.AUTOWIRE_NO;
	public static final int AUTOWIRE_BY_NAME = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;
	public static final int AUTOWIRE_BY_TYPE = AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;
	public static final int AUTOWIRE_CONSTRUCTOR = AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;
	@Deprecated
	public static final int AUTOWIRE_AUTODETECT = AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT;
	
	private volatile Object beanClass;
	
	private String scope;
	
	private String factoryBeanName;
	
	private String factoryMethodName;
	
	/** 返回是否允许访问非公有的构造函数和方法*/
	private boolean nonPublicAccessAllowed = true;
	
	/** 是在宽松模式还是严格模式解决构造方法 */
	private boolean lenientConstructorResolution = true;
	
	/** 构造方法 */
	private ConstructorArgumentValues constructorArgumentValues;
	
	/** 属性 */
	private MutablePropertyValues propertyValues;
	
	/** 重写的方法 */
	private MethodOverrides methodOverrides = new MethodOverrides();
	
	private int autowireMode = AUTOWIRE_NO;
	
	
	protected AbstractBeanDefinition() {
		this(null, null);
	}
	
	protected AbstractBeanDefinition(ConstructorArgumentValues cav, MutablePropertyValues mpv){
		setConstructorArgumentValues(cav);
		setPropertyValues(mpv);
	}
	
	public AbstractBeanDefinition(BeanDefinition original){
		setBeanClassName(original.getBeanClassName());
		setScope(original.getScope());
		setFactoryBeanName(original.getFactoryBeanName());
		setFactoryMethodName(original.getFactoryMethodName());
		setPropertyValues(new MutablePropertyValues(original.getPropertyValues()));
		setConstructorArgumentValues(new ConstructorArgumentValues(original.getConstructorArgumentValues()));
		
		if(original instanceof AbstractBeanDefinition){
			AbstractBeanDefinition originalAbd = (AbstractBeanDefinition) original;
			if(originalAbd.hasBeanClass()){
				setBeanClass(originalAbd.getBeanClass());
			}
			setMethodOverrides(new MethodOverrides(originalAbd.getMethodOverrides()));
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

	public String getFactoryBeanName() {
		return factoryBeanName;
	}

	public void setFactoryBeanName(String factoryBeanName) {
		this.factoryBeanName = factoryBeanName;
	}

	public String getFactoryMethodName() {
		return factoryMethodName;
	}

	public void setFactoryMethodName(String factoryMethodName) {
		this.factoryMethodName = factoryMethodName;
	}

	public ConstructorArgumentValues getConstructorArgumentValues() {
		return constructorArgumentValues;
	}

	public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
		this.constructorArgumentValues = (constructorArgumentValues != null ? constructorArgumentValues : new ConstructorArgumentValues());
	}
	
	@Override
	public MutablePropertyValues getPropertyValues() {
		return this.propertyValues;
	}

	public void setPropertyValues(MutablePropertyValues propertyValues) {
		this.propertyValues = (propertyValues != null ? propertyValues : new MutablePropertyValues());
	}

	/**
	 * 返回是否允许访问非公有的构造函数和方法
	 * @return
	 */
	public boolean isNonPublicAccessAllowed() {
		return nonPublicAccessAllowed;
	}

	/**
	 * 是在宽松模式还是严格模式解决构造方法
	 * @return
	 */
	public boolean isLenientConstructorResolution() {
		return lenientConstructorResolution;
	}

	public int getAutowireMode() {
		return autowireMode;
	}
	
	public int getResolvedAutowireMode() {
		if (this.autowireMode == AUTOWIRE_AUTODETECT) {
			// Work out whether to apply setter autowiring or constructor autowiring.
			// If it has a no-arg constructor it's deemed to be setter autowiring,
			// otherwise we'll try constructor autowiring.
			Constructor<?>[] constructors = getBeanClass().getConstructors();
			for (Constructor<?> constructor : constructors) {
				if (constructor.getParameterTypes().length == 0) {
					return AUTOWIRE_BY_TYPE;
				}
			}
			return AUTOWIRE_CONSTRUCTOR;
		}
		else {
			return this.autowireMode;
		}
	}

	public void setAutowireMode(int autowireMode) {
		this.autowireMode = autowireMode;
	}
}
