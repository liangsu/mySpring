package spring.ls.beans.factory;

public interface AttributeAccessor {

	void setAttribute(String name, Object value);

	Object getAttribute(String name);
	
	boolean hasAttribute(String name);
	
	void removeAttribute(String name);
	
	String[] attributeNames();
	
}
