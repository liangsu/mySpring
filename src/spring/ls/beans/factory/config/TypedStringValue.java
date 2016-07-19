package spring.ls.beans.factory.config;

import spring.ls.beans.BeanMetadataElement;
import spring.ls.util.ClassUtils;

public class TypedStringValue implements BeanMetadataElement{

	private String value;
	
	/** 表明value值得最终类型，存的值可能是Class，也可能是Class的路径*/
	private volatile Object targetType;
	
	private String specifiedTypeName;
	
	/** 表面value的值是否是动态的，需要计算 */
	private volatile boolean dynamic;
	
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

	public Class<?> resolveTargetType(ClassLoader classLoader) throws ClassNotFoundException{
		if(targetType == null){
			return null;
		}
		Class<?> resolvedClass = ClassUtils.forName(getTargetTypeName(), classLoader);
		this.targetType = resolvedClass;
		return resolvedClass;
	}

	public void setTargetType(Class<?> targetType) {
		this.targetType = targetType;
	}
	
	public Class<?> getTargetType(){
		Object targetTypeValue = this.targetType;
		if( !(targetTypeValue instanceof Class)){
			throw new IllegalStateException("Typed String value does not carry a resolved target type");
		}
		return (Class<?>) this.targetType;
	}
	
	public void setTargetTypeName(String targetTypeName){
		this.targetType = targetTypeName;
	}
	
	public String getTargetTypeName() {
		Object targetTypeValue = this.targetType;
		if(targetTypeValue instanceof Class){
			((Class<?>) targetTypeValue).getName();
		}
		return (String) targetTypeValue;
	}

	public boolean hasTargetType(){
		return (this.targetType instanceof Class);
	}

	public String getSpecifiedTypeName() {
		return specifiedTypeName;
	}

	public void setSpecifiedTypeName(String specifiedTypeName) {
		this.specifiedTypeName = specifiedTypeName;
	}

	public boolean isDynamic() {
		return dynamic;
	}
	
	public void setDynamic(){
		this.dynamic = true;
	}
	
}
