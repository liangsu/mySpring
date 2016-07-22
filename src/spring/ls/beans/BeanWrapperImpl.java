package spring.ls.beans;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Method;

public class BeanWrapperImpl extends AbstractPropertyAccessor implements BeanWrapper{

	/** 包装类 */
	private Object object;
	
	public BeanWrapperImpl() {
		this(true);
	}
	
	public BeanWrapperImpl(boolean registerDefaultEditor){
		if(registerDefaultEditor){
			registerDefaultEditors();
		}
		typeConverterDelegate = new TypeConverterDelegate(this);
	}
	
	public BeanWrapperImpl(Object object) {
		registerDefaultEditors();
		setWrappedInstance(object);
	}

	@Override
	public Object getWrappedInstance() {
		return this.object;
	}

	@Override
	public Class<?> getWrappedClass() {
		return (this.object != null ? this.object.getClass() : null);
	}

	public void setWrappedInstance(Object object) {
		this.object = object;
	}

	@Override
	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(this.object.getClass());
			return beanInfo.getPropertyDescriptors();
		} catch (Exception e) {
			throw new BeansException("获取"+this.object+"的PropertyDescriptor出错");
		}
	}

	@Override
	public void setPropertyValue(String propertyName, Object value) throws BeansException {
		
	}
	
	@Override
	public void setPropertyValue(PropertyValue pv) throws BeansException {
		String propertyName = pv.getName();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(this.object.getClass());
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor pd : pds) {
				if(pd.getName().equals(propertyName)){
					Method m = pd.getWriteMethod();
					if(m != null){
						PropertyEditor editor = super.getDefaultEditor(pd.getPropertyType());
						if(editor != null){
							editor.setAsText((String) pv.getConvertedValue());
							Object resolvedValue = editor.getValue();
							m.invoke(this.object, resolvedValue);
						}else{
							m.invoke(this.object, pv.getConvertedValue());
						}
					}
					
					break;
				}
			}
		} catch (Exception e) {
			new BeansException("设置属性值失败"+propertyName,e);
		}
	}
}
