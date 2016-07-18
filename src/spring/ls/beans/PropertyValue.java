package spring.ls.beans;

import java.io.Serializable;

/**
 * 用于承载property元素
 * @author warhorse
 *
 */
@SuppressWarnings("serial")
public class PropertyValue extends BeanMetadataAttributeAccessor implements Serializable{

	private final String name;

	private final Object value;
	
	private Object source;
	
	private boolean optional = false;

	private boolean converted = false;

	private Object convertedValue;

	public PropertyValue(String name, Object value) {
		super();
		this.name = name;
		this.value = value;
	}
	
	public PropertyValue(PropertyValue original) {
//		Assert.notNull(original, "Original must not be null");
		this.name = original.getName();
		this.value = original.getValue();
		this.source = original.getSource();
		this.optional = original.isOptional();
		this.converted = original.converted;
		this.convertedValue = original.convertedValue;
//		this.conversionNecessary = original.conversionNecessary;
//		this.resolvedTokens = original.resolvedTokens;
//		this.resolvedDescriptor = original.resolvedDescriptor;
//		copyAttributesFrom(original);
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public boolean isConverted() {
		return converted;
	}

	public void setConverted(boolean converted) {
		this.converted = converted;
	}

	public Object getConvertedValue() {
		return convertedValue;
	}

	public void setConvertedValue(Object convertedValue) {
		this.convertedValue = convertedValue;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}
}
