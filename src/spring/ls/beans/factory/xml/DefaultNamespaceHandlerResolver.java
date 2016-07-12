package spring.ls.beans.factory.xml;

import java.util.HashMap;
import java.util.Map;

import spring.ls.util.ClassUtils;

public class DefaultNamespaceHandlerResolver implements NamespaceHandlerResolver{

	private Map<String, Object> handlerMappings;
	
	public DefaultNamespaceHandlerResolver(){
		
	}
	
	@Override
	public NamespaceHandler resolve(String namespaceUri  ) {
		Map<String, Object> handlerMappings = getHandlerMappings();
		Object objectOrString = handlerMappings.get(namespaceUri);
		if(objectOrString == null){
			return null;
			
		}else if(objectOrString instanceof NamespaceHandler){
			return (NamespaceHandler) objectOrString;
			
		}else{
			
			NamespaceHandler handler = null; 
			try {
				String className = (String) objectOrString;
				Class<?> clazz = ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
				handler = (NamespaceHandler) clazz.newInstance();
				handler.init();
				handlerMappings.put(namespaceUri, handler);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return handler;
		}
	}

	public Map<String, Object> getHandlerMappings() {
		if(handlerMappings == null){
			handlerMappings = new HashMap<String, Object>();
			//读取namespaceHandler的类路径，并放入map
			handlerMappings.put("http://www.ls.com/schema/user", "spring.ls.test.UserNamespaceHandler");
		}
		
		return handlerMappings;
	}

}
