package spring.ls.beans;

public class BeanWrapperImpl extends AbstractPropertyAccessor implements BeanWrapper{

	/** 包装类 */
	private Object object;
	
	public BeanWrapperImpl() {
		typeConverterDelegate = new TypeConverterDelegate();
	}
	
	@Override
	public Object getWrappedInstance() {
		return this.object;
	}

	@Override
	public Class<?> getWrappedClass() {
		return (this.object != null ? this.object.getClass() : null);
	}

}
