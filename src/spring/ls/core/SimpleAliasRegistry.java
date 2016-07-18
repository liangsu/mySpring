package spring.ls.core;

import java.util.HashMap;
import java.util.Map;

import spring.ls.util.StringUtils;

public class SimpleAliasRegistry implements AliasRegistry{

	private final Map<String, String> aliases = new HashMap<String,String>();
	
	@Override
	public void registerAlias(String beanName, String alias)  {
		String oldBeanName = aliases.get(alias);
		if(oldBeanName != null){
			if(!beanName.equals(oldBeanName)){
				throw new IllegalArgumentException("别名"+alias+"重复");
			}
		}else{
			aliases.put(alias, beanName);
		}
	}

	/**
	 * 通过别名获取beanName
	 * @param name
	 * @return
	 */
	public String cononicalName(String name) {
		String cononicalName = name;
		String resovlerName;
		do{
			resovlerName = this.aliases.get(cononicalName);
			if(StringUtils.hasLength(resovlerName)){
				cononicalName = resovlerName;
			}
		}while( resovlerName != null);
		return cononicalName;
	}
}
