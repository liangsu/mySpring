package spring.ls.factory;

/**
 * 默认配置
 * @author warhorse
 *
 */
public class DefaultConfiguration {

	public final static String SCOPE_PROTOTYPE = "prototype";
	public final static String SCOPE_SINGLETON = "singleton";
	
	private String scope = SCOPE_PROTOTYPE;
	
	public String getScope(){
		return scope;
	}
	
}
