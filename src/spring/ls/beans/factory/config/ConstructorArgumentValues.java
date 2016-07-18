package spring.ls.beans.factory.config;

import java.util.HashMap;
import java.util.Iterator;
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
	
	public ConstructorArgumentValues(ConstructorArgumentValues original) {

	}
	
	public void addArgumentValues(ConstructorArgumentValues other) {
		if (other != null) {
			for (Map.Entry<Integer, ValueHolder> entry : other.indexedArgumentValues.entrySet()) {
				addOrMergeIndexedArgumentValue(entry.getKey(), entry.getValue().copy());
			}
			for (ValueHolder valueHolder : other.genericArgumentValues) {
				if (!this.genericArgumentValues.contains(valueHolder)) {
					addOrMergeGenericArgumentValue(valueHolder.copy());
				}
			}
		}
	}
	
	private void addOrMergeIndexedArgumentValue(Integer key, ValueHolder newValue) {
		ValueHolder currentValue = this.indexedArgumentValues.get(key);
//		if (currentValue != null && newValue.getValue() instanceof Mergeable) {
//			Mergeable mergeable = (Mergeable) newValue.getValue();
//			if (mergeable.isMergeEnabled()) {
//				newValue.setValue(mergeable.merge(currentValue.getValue()));
//			}
//		}
		this.indexedArgumentValues.put(key, newValue);
	}
	
	private void addOrMergeGenericArgumentValue(ValueHolder newValue) {
		if (newValue.getName() != null) {
			for (Iterator<ValueHolder> it = this.genericArgumentValues.iterator(); it.hasNext();) {
				ValueHolder currentValue = it.next();
				if (newValue.getName().equals(currentValue.getName())) {
//					if (newValue.getValue() instanceof Mergeable) {
//						Mergeable mergeable = (Mergeable) newValue.getValue();
//						if (mergeable.isMergeEnabled()) {
//							newValue.setValue(mergeable.merge(currentValue.getValue()));
//						}
//					}
					it.remove();
				}
			}
		}
		this.genericArgumentValues.add(newValue);
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

		public ValueHolder(String name, Object value) {
			super();
			this.name = name;
			this.value = value;
		}

		public ValueHolder(String name, String type, Object value) {
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
		
		public ValueHolder copy() {
			ValueHolder copy = new ValueHolder(this.name, this.type, this.value);
			copy.setSource(this.source);
			return copy;
		}
	}
}
