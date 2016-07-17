package spring.ls.beans.factory.config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import spring.ls.beans.BeanMetadataElement;

/**
 * 描述了构造方法的参数
 * @author warhorse
 *
 */
public class ConstructorArgumentValues {

	private Map<Integer, ValueHolder> indexedArgumentValues = new HashMap<Integer, ValueHolder>();
	
	private List<ValueHolder> genericArgumentValues = new LinkedList<ValueHolder>();
	
	public ConstructorArgumentValues() {
		super();
	}
	
	public boolean hasIndexedArgumentValue(int index){
		return this.indexedArgumentValues.containsKey(index);
	}
	
	public void addIndexedArgumentValue(Integer index, ValueHolder valueHolder){
		this.indexedArgumentValues.put(index, valueHolder);
	}
	
	public void addGenericArgumentValue(ValueHolder valueHolder){
		this.genericArgumentValues.add(valueHolder);
	}

	/**
	 * 构造方法参数的值
	 * @author warhorse
	 *
	 */
	public static class ValueHolder implements BeanMetadataElement{

		private String name;
		
		private String type;
		
		private Object value;
		
		private Object source;
		
		private boolean converted = false;
		
		private Object convertedValue;
		
		public ValueHolder() {
			super();
		}

		public ValueHolder(Object value) {
			super();
			this.value = value;
		}

		public ValueHolder(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}

		public ValueHolder(String name, String type, String value) {
			super();
			this.name = name;
			this.type = type;
			this.value = value;
		}

		@Override
		public Object getSource() {
			return source;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
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

		public void setSource(Object source) {
			this.source = source;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}
		
	}
}
