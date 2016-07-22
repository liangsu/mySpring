package spring.ls.beans;

public interface PropertyAccessor {

	void setPropertyValue(PropertyValue pv) throws BeansException;
	
	void setPropertyValue(String propertyName, Object value) throws BeansException;
	
	void setPropertyValues(PropertyValues pvs) throws BeansException;
	
	void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown) throws BeansException;
	
	void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, boolean ignoreInvalid) throws BeansException;
}
