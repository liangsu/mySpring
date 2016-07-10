package spring.ls.beans;

public class MethodOverride implements BeanMetadataElement{

	private final String methodName;
	
	private boolean overLoaded = true;
	
	private Object source;
	
	public MethodOverride(String methodName){
		this.methodName = methodName;
	}
	
	@Override
	public Object getSource() {
		return source;
	}


	public String getMethodName() {
		return methodName;
	}

	public boolean isOverLoaded() {
		return overLoaded;
	}


	public void setOverLoaded(boolean overLoaded) {
		this.overLoaded = overLoaded;
	}


	public void setSource(Object source) {
		this.source = source;
	}

}
