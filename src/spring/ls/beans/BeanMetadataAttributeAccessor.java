package spring.ls.beans;

public class BeanMetadataAttributeAccessor extends AttributeAccessorSupport implements BeanMetadataElement{

	private Object source;
	
	@Override
	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

}
                                                                                                                                                                                            