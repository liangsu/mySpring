package spring.ls.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
public class MutablePropertyValues implements PropertyValues,Serializable{

	List<PropertyValue> propertyValues;
	
	Set<String> processedProperties;
	
	private volatile boolean converted = false;
	
	public MutablePropertyValues() {
		this.propertyValues = new ArrayList<PropertyValue>(0);
	}
	
	public MutablePropertyValues(PropertyValues original) {
		// We can optimize this because it's all new:
		// There is no replacement of existing property values.
		if (original != null) {
			PropertyValue[] pvs = original.getPropertyValues();
			this.propertyValues = new ArrayList<PropertyValue>(pvs.length);
			for (PropertyValue pv : pvs) {
				this.propertyValues.add(new PropertyValue(pv));
			}
		}
		else {
			this.propertyValues = new ArrayList<PropertyValue>(0);
		}
	}

	@Override
	public PropertyValue[] getPropertyValues() {
		return this.propertyValues.toArray(new PropertyValue[this.propertyValues.size()]);
	}

	@Override
	public PropertyValue getPropertyValue(String propertyName) {
		for(PropertyValue pv : this.propertyValues){
			if(pv.getName().equals(propertyName)){
				return pv;
			}
		}
		return null;
	}

	@Override
	public boolean contains(String propertyName) {
		return (getPropertyValue(propertyName) != null ||
				( processedProperties != null && processedProperties.contains(propertyName)));
	}

	@Override
	public boolean isEmpty() {
		return this.processedProperties.isEmpty();
	}

	public void addPropertyValue(PropertyValue pv) {
		this.propertyValues.add(pv);
	}
}
