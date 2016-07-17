package spring.ls.beans.factory.config;

import spring.ls.beans.BeanMetadataElement;

public class TypedStringValue implements BeanMetadataElement{

	private String value;
	
	private Object source;
	
	public TypedStringValue(String value) {
		this.value = value;
	}
	
	@Override
	public Object getSource() {
		return source;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setSource(Object source) {
		this.source = source;
	}

}
