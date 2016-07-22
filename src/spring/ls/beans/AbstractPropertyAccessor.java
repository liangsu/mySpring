package spring.ls.beans;

public abstract class AbstractPropertyAccessor extends TypeConverterSupport implements ConfigurablePropertyAccessor{

	@Override
	public void setPropertyValue(PropertyValue pv) throws BeansException {
		setPropertyValue(pv.getName(), pv.getValue());
	}
	
	@Override
	public void setPropertyValues(PropertyValues pvs) throws BeansException {
		setPropertyValues(pvs, false, false);
	}
	
	@Override
	public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown) throws BeansException {
		setPropertyValues(pvs, ignoreUnknown, false);
	}
	
	@Override
	public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, boolean ignoreInvalid)
			throws BeansException {
		for(PropertyValue pv : pvs.getPropertyValues()){
			try {
				setPropertyValue(pv);
			} catch (Exception e) {
				if(!ignoreUnknown){
					throw e;
				}
			}
			
		}
	}
	
	@Override
	public abstract void setPropertyValue(String propertyName, Object value) throws BeansException;
}
