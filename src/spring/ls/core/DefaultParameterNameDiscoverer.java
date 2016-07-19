package spring.ls.core;

public class DefaultParameterNameDiscoverer extends PrioritizedParameterNameDiscoverer{

	private static final boolean standardReflectionAvailable =
			(JdkVersion.getMajorJavaVersion() >= JdkVersion.JAVA_18);
	
	public DefaultParameterNameDiscoverer(){
		if(standardReflectionAvailable){
			addDiscover(new StandardReflectionParameterNameDiscoverer());
		}else{
			addDiscover(new LocalVariableTableParameterNameDiscoverer());
		}
	}
}
