package spring.ls.beans;

public interface AttributeAccessor {

	Object getAttribute(String name);
	
	void setAttribute(String name, String value);
	
	Object removeAttribute(String name);
	
	boolean hasAttribute(String name);
	
	String[] attributeNames();
}
