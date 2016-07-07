package spring.ls.beans;

import java.util.HashMap;
import java.util.Map;

public class AttributeAccessorSupport implements AttributeAccessor{

	private Map<String, Object> attributes = new HashMap<String, Object>();
	
	@Override
	public Object getAttribute(String name) {
		return this.attributes.get(name);
	}

	@Override
	public void setAttribute(String name, String value) {
		this.attributes.put(name, value);
	}

	@Override
	public Object removeAttribute(String name) {
		return this.attributes.remove(name);
	}

	@Override
	public boolean hasAttribute(String name) {
		return this.attributes.containsKey(name);
	}

	@Override
	public String[] attributeNames() {
//		Set<String> nameSet = this.attributes.keySet();
//		String names[] = new String[nameSet.size()];
//		int i = 0;
//		for(String name : nameSet){
//			names[i] = name;
//			i++;
//		}
		
		return this.attributes.keySet().toArray(new String[ this.attributes.size()]);
	}

}
